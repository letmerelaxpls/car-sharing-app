package csa.controller;

import static csa.util.AuthenticationTestUtil.createAuthentication;
import static csa.util.RoleTestUtil.createCustomerRole;
import static csa.util.UserTestUtil.createUserResponseWithRolesDto;
import static csa.util.UserTestUtil.createUserUpdateProfileRequestDto;
import static csa.util.UserTestUtil.createUserUpdateRolesRequestDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import csa.dto.user.UserResponseWithRolesDto;
import csa.dto.user.UserUpdateRolesRequestDto;
import csa.model.enums.RoleName;
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
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerTest {
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp(@Autowired WebApplicationContext context) {
        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @DisplayName("getUserProfile should return correct UserResponseWithRolesDto")
    @Sql(scripts = "classpath:database/users/insert-2-users.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/users/delete-2-users.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void getUserProfile_UserWithIdThree_True() throws Exception {
        Long userId = 3L;
        Authentication authentication = createAuthentication(
                userId, createCustomerRole());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        UserResponseWithRolesDto expected = createUserResponseWithRolesDto();

        MvcResult mvcResult = mockMvc.perform(get("/users/me")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        UserResponseWithRolesDto result = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                UserResponseWithRolesDto.class);

        assertEquals(expected, result);
    }

    @Test
    @DisplayName("updateUserProfile should update correct User and return UserResponseWithRolesDto")
    @Sql(scripts = "classpath:database/users/insert-2-users.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/users/delete-2-users.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void updateUserProfile_ChangeFirstNameToJohn_True() throws Exception {
        Long userId = 3L;
        Authentication authentication = createAuthentication(
                userId, createCustomerRole());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        String jsonRequestBody = objectMapper
                .writeValueAsString(createUserUpdateProfileRequestDto());

        MvcResult mvcResult = mockMvc.perform(put("/users/me")
                .content(jsonRequestBody)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        UserResponseWithRolesDto expected = createUserResponseWithRolesDto();
        expected.setFirstName("John");
        UserResponseWithRolesDto result = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                UserResponseWithRolesDto.class);

        assertEquals(expected, result);
    }

    @Test
    @DisplayName("updateUserRoles should update correct User and return UserResponseWithRolesDto ")
    @WithMockUser(username = "admin", roles = "ADMIN")
    @Sql(scripts = "classpath:database/users/insert-2-users.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/users/delete-2-users.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void updateUserRoles_AddRoleAdminForUserWithIdThree_True() throws Exception {
        Long userId = 3L;
        UserUpdateRolesRequestDto requestDto = createUserUpdateRolesRequestDto();
        requestDto.getRoles().add(RoleName.ROLE_ADMIN);
        String jsonRequestBody = objectMapper
                .writeValueAsString(requestDto);

        MvcResult mvcResult = mockMvc.perform(put("/users/3/role")
                .content(jsonRequestBody)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        UserResponseWithRolesDto expected = createUserResponseWithRolesDto();
        expected.getRoles().add(RoleName.ROLE_ADMIN.name());
        UserResponseWithRolesDto result = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                UserResponseWithRolesDto.class);

        assertEquals(expected, result);
    }

}