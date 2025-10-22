package csa.controller;

import static csa.util.AuthenticationTestUtil.createAuthentication;
import static csa.util.RentalTestUtil.createActualReturnDateDto;
import static csa.util.RentalTestUtil.createFirstRentalResponseDto;
import static csa.util.RentalTestUtil.createRentalRequestDto;
import static csa.util.RoleTestUtil.createAdminRole;
import static csa.util.RoleTestUtil.createCustomerRole;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import csa.dto.rental.RentalCreateRequestDto;
import csa.dto.rental.RentalResponseDto;
import csa.dto.rental.RentalSetActualReturnDateDto;
import csa.service.telegram.NotificationService;
import java.time.LocalDate;
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
class RentalControllerTest {
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private NotificationService notificationService;

    @BeforeEach
    void setUp(@Autowired WebApplicationContext context) {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        objectMapper.configOverride(LocalDate.class)
                .setFormat(JsonFormat.Value.forPattern("dd-MM-yyy"));
    }

    @Test
    @DisplayName("getRentalById should return correct RentalResponseDto")
    @Sql(scripts = {"classpath:database/users/insert-2-users.sql",
            "classpath:database/cars/insert-2-cars.sql",
            "classpath:database/rentals/insert-3-rentals.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:database/rentals/delete-all-rentals.sql",
            "classpath:database/cars/delete-2-cars.sql",
            "classpath:database/users/delete-2-users.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getRentalById_RentalWithIdOne_True() throws Exception {
        Long userId = 3L;
        Authentication authentication = createAuthentication(userId, createCustomerRole());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        RentalResponseDto expected = createFirstRentalResponseDto();

        MvcResult mvcResult = mockMvc.perform(get("/rentals/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        RentalResponseDto result = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(), RentalResponseDto.class);

        assertEquals(expected, result);
    }

    @Test
    @DisplayName("addRental should return correct RentalResponseDto")
    @Sql(scripts = {"classpath:database/users/insert-2-users.sql",
            "classpath:database/cars/insert-2-cars.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:database/rentals/delete-all-rentals.sql",
            "classpath:database/cars/delete-2-cars.sql",
            "classpath:database/users/delete-2-users.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void addRental_RentalWithCarIdFour_True() throws Exception {
        Authentication authentication = createAuthentication(3L, createCustomerRole());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        RentalCreateRequestDto requestDto = createRentalRequestDto();
        String jsonRequestBody = objectMapper.writeValueAsString(requestDto);

        doNothing().when(notificationService).sendNewRentalNotification(any());
        MvcResult mvcResult = mockMvc.perform(post("/rentals")
                        .content(jsonRequestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        RentalResponseDto expected = createFirstRentalResponseDto();
        expected.setActualReturnDate(null);
        RentalResponseDto result = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(), RentalResponseDto.class);

        assertThat(result)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(expected);
        verify(notificationService).sendNewRentalNotification(any());
    }

    @Test
    @DisplayName("setActualReturnDate should return correct RentalResponseDto")
    @WithMockUser(roles = "ADMIN")
    @Sql(scripts = {"classpath:database/users/insert-2-users.sql",
            "classpath:database/cars/insert-2-cars.sql",
            "classpath:database/rentals/insert-3-rentals.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:database/rentals/delete-all-rentals.sql",
            "classpath:database/cars/delete-2-cars.sql",
            "classpath:database/users/delete-2-users.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void setActualReturnDate_RentalWithId_True() throws Exception {
        RentalSetActualReturnDateDto requestDto = createActualReturnDateDto();
        String jsonRequestBody = objectMapper.writeValueAsString(requestDto);

        MvcResult mvcResult = mockMvc.perform(post("/rentals/1/return")
                        .content(jsonRequestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        RentalResponseDto expected = createFirstRentalResponseDto();
        RentalResponseDto result = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(), RentalResponseDto.class);
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("getRentalByUserId should return correct Page of RentalResponseDto")
    @Sql(scripts = {"classpath:database/users/insert-2-users.sql",
            "classpath:database/cars/insert-2-cars.sql",
            "classpath:database/rentals/insert-3-rentals.sql"},
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {"classpath:database/rentals/delete-all-rentals.sql",
            "classpath:database/cars/delete-2-cars.sql",
            "classpath:database/users/delete-2-users.sql"},
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getRentalByUserId_UserWithIdThreeAndRentalIsActive_True() throws Exception {
        Long userId = 3L;
        Authentication authentication = createAuthentication(userId, createAdminRole());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        MvcResult mvcResult = mockMvc.perform(get("/rentals/byUser/" + userId)
                        .param("isActive", "true")
                        .param("page", "0")
                        .param("size", "10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode content = objectMapper.readTree(
                mvcResult.getResponse().getContentAsString())
                .get("content");
        RentalResponseDto expectedFirst = createFirstRentalResponseDto();
        List<RentalResponseDto> result = objectMapper.readValue(content.toString(),
                new TypeReference<>() {});
        assertEquals(expectedFirst, result.get(0));
    }
}
