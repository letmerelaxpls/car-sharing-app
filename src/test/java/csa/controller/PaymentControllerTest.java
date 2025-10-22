package csa.controller;

import static csa.util.AuthenticationTestUtil.createAuthentication;
import static csa.util.PaymentTestUtil.createPaymentDetailsDto;
import static csa.util.PaymentTestUtil.createPaymentRequestDto;
import static csa.util.PaymentTestUtil.createPaymentResponseDto;
import static csa.util.PaymentTestUtil.createPaymentSummaryDto;
import static csa.util.PaymentTestUtil.createSessionParams;
import static csa.util.PaymentTestUtil.createStripeSession;
import static csa.util.RoleTestUtil.createAdminRole;
import static csa.util.RoleTestUtil.createCustomerRole;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import csa.dto.payment.PaymentDetailsDto;
import csa.dto.payment.PaymentRequestDto;
import csa.dto.payment.PaymentResponseDto;
import csa.dto.payment.PaymentSummaryDto;
import csa.service.stripe.StripePaymentService;
import csa.service.telegram.NotificationService;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = {"classpath:database/cars/insert-2-cars.sql",
        "classpath:database/users/insert-2-users.sql",
        "classpath:database/rentals/insert-3-rentals.sql",
        "classpath:database/payments/insert-2-payments.sql"},
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = {"classpath:database/payments/delete-2-payments.sql",
        "classpath:database/rentals/delete-all-rentals.sql",
        "classpath:database/users/delete-2-users.sql",
        "classpath:database/cars/delete-2-cars.sql"},
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class PaymentControllerTest {
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private StripePaymentService stripePaymentService;
    @MockitoBean
    private NotificationService notificationService;

    @BeforeEach
    void setUp(@Autowired WebApplicationContext context) {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @DisplayName("getUserPayments should return correct Page of PaymentSummaryDto")
    @WithMockUser(roles = "ADMIN")
    void getUserPayments_UserWithIdThree_True() throws Exception {
        Long userId = 3L;
        Authentication authentication = createAuthentication(4L, createAdminRole());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        MvcResult mvcResult = mockMvc.perform(get("/payments")
                        .param("user_id", String.valueOf(userId))
                        .param("page", "0")
                        .param("size", "10")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode content = objectMapper.readTree(
                mvcResult.getResponse().getContentAsString()).get("content");
        PaymentSummaryDto expected = createPaymentSummaryDto();
        List<PaymentSummaryDto> result = objectMapper.readValue(content.toString(),
                new TypeReference<>() {});
        assertEquals(expected, result.get(0));
    }

    @Test
    @DisplayName("getById should return correct PaymentDetailsDto")
    @WithMockUser(roles = "ADMIN")
    void getById_PaymentWithIdOne_True() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/payments/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        PaymentDetailsDto expected = createPaymentDetailsDto();
        PaymentDetailsDto result = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(), PaymentDetailsDto.class);
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("createSessions should return correct PaymentResponseDto")
    void createSession_CorrectData_True() throws Exception {
        Long userId = 3L;
        Authentication authentication = createAuthentication(userId, createCustomerRole());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        PaymentRequestDto requestDto = createPaymentRequestDto();
        String jsonRequestBody = objectMapper.writeValueAsString(requestDto);
        BigDecimal amount = new BigDecimal(3100);
        SessionCreateParams params = createSessionParams(amount);
        Session session = createStripeSession();

        when(stripePaymentService.createSessionParams(any())).thenReturn(params);
        when(stripePaymentService.makeSession(params)).thenReturn(session);
        PaymentResponseDto expected = createPaymentResponseDto();
        MvcResult mvcResult = mockMvc.perform(post("/payments")
                        .content(jsonRequestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();
        PaymentResponseDto result = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(), PaymentResponseDto.class);

        assertEquals(expected, result);
        verify(stripePaymentService).createSessionParams(amount);
        verify(stripePaymentService).makeSession(params);
    }

    @Test
    @DisplayName("paymentSuccess should return HttpStatus OK")
    @WithMockUser(roles = "CUSTOMER")
    void paymentSuccess_CorrectData_True() throws Exception {
        String sessionId = "session_id";

        when(stripePaymentService.isSessionPaid(sessionId)).thenReturn(true);
        doNothing().when(notificationService).sendSuccessfulPaymentNotification(any());
        mockMvc.perform(get("/payments/success")
                        .param("session_id", sessionId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(stripePaymentService).isSessionPaid(sessionId);
        verify(notificationService).sendSuccessfulPaymentNotification(any());
    }

    @Test
    @DisplayName("paymentCancel should return HttpStatus OK")
    @WithMockUser(roles = "CUSTOMER")
    void paymentCancel_CorrectData_True() throws Exception {
        String sessionId = "session_id";

        doNothing().when(notificationService).sendCanceledPaymentNotification(any());
        mockMvc.perform(get("/payments/cancel")
                        .param("session_id", sessionId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(notificationService).sendCanceledPaymentNotification(any());
    }

}
