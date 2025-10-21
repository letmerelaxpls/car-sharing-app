package csa.controller;

import static csa.util.UserTestUtil.createUserLoginRequestDto;
import static csa.util.UserTestUtil.createUserLoginResponseDto;
import static csa.util.UserTestUtil.createUserRegRequestDto;
import static csa.util.UserTestUtil.createUserResponseDto;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import csa.dto.user.UserLoginRequestDto;
import csa.dto.user.UserLoginResponseDto;
import csa.dto.user.UserRegRequestDto;
import csa.dto.user.UserResponseDto;
import csa.service.security.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = "classpath:database/users/delete-2-users.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class AuthenticationControllerTest {
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp(@Autowired WebApplicationContext context) {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @DisplayName("register should return correct UserResponseDto")
    void register_UserWithFirstNameTest_True() throws Exception {
        UserRegRequestDto requestDto = createUserRegRequestDto();
        String jsonRequestBody = objectMapper.writeValueAsString(requestDto);

        MvcResult mvcResult = mockMvc.perform(post("/auth/register")
                        .content(jsonRequestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        UserResponseDto expected = createUserResponseDto();
        UserResponseDto result = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(), UserResponseDto.class);

        assertThat(expected)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(result);
    }

    @Test
    @DisplayName("login should return correct UserLoginResponseDto")
    @Sql(scripts = "classpath:database/users/insert-2-users.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void login_CorrectData_True() throws Exception {
        UserLoginRequestDto requestDto = createUserLoginRequestDto();
        String jsonRequestBody = objectMapper.writeValueAsString(requestDto);
        UserLoginResponseDto expected = createUserLoginResponseDto();

        when(authenticationService.authenticate(requestDto)).thenReturn(expected);

        MvcResult mvcResult = mockMvc.perform(post("/auth/login")
                        .content(jsonRequestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        UserLoginResponseDto result = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(), UserLoginResponseDto.class);

        assertEquals(expected, result);
    }
}