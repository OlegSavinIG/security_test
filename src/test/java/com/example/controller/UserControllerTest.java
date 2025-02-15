package com.example.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(roles = "USER")
    void getProfile_WithUserRole_ShouldReturnOk() throws Exception {
        mockMvc.perform(get("/user/profile"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getProfile_WithWrongRole_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/user/profile"))
                .andExpect(status().isForbidden());
    }

    @Test
    void getProfile_Unauthorized_ShouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/user/profile"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "MODERATOR")
    void moderateContent_WithModeratorRole_ShouldReturnOk() throws Exception {
        mockMvc.perform(post("/user/moderate"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void moderateContent_WithUserRole_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(post("/user/moderate"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "SUPER_ADMIN")
    void deleteUser_WithSuperAdminRole_ShouldReturnOk() throws Exception {
        mockMvc.perform(delete("/user/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteUser_WithAdminRole_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(delete("/user/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "MODERATOR")
    void deleteUser_WithModeratorRole_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(delete("/user/1"))
                .andExpect(status().isForbidden());
    }
}