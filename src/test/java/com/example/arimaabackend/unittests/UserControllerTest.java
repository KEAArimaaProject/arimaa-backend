package com.example.arimaabackend.unittests;

import com.example.arimaabackend.dto.UserCreateRequest;
import com.example.arimaabackend.dto.UserResponse;
import com.example.arimaabackend.security.UserDetailsServiceImpl;
import com.example.arimaabackend.services.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    private UserResponse sampleResponse() {
        return new UserResponse(1L, "alice", "alice@example.com", "USER", Instant.now(), Instant.now());
    }

    // --- POST /api/users (public) ---

    @Test
    void register_returnsCreated_withValidBody() throws Exception {
        when(userService.create(any(UserCreateRequest.class))).thenReturn(sampleResponse());

        var body = objectMapper.writeValueAsString(
                new UserCreateRequest("alice", "alice@example.com", "secret123"));

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("alice"))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    void register_returnsBadRequest_whenPasswordTooShort() throws Exception {
        var body = objectMapper.writeValueAsString(
                new UserCreateRequest("alice", "alice@example.com", "short"));

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void register_returnsBadRequest_whenEmailInvalid() throws Exception {
        var body = objectMapper.writeValueAsString(
                new UserCreateRequest("alice", "not-an-email", "secret123"));

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body)
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    // --- GET /api/users/{id} (admin only) ---

    @Test
    void getById_returnsOk_whenAdmin() throws Exception {
        when(userService.getById(1L)).thenReturn(sampleResponse());

        mockMvc.perform(get("/api/users/1").with(user("admin").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getById_returnsForbidden_whenUser() throws Exception {
        mockMvc.perform(get("/api/users/1").with(user("alice").roles("USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void getById_returnsUnauthorized_whenAnonymous() throws Exception {
        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getById_returns404_whenNotFound() throws Exception {
        when(userService.getById(99L))
                .thenThrow(new ResponseStatusException(NOT_FOUND, "User 99 not found"));

        mockMvc.perform(get("/api/users/99").with(user("admin").roles("ADMIN")))
                .andExpect(status().isNotFound());
    }

    // --- DELETE /api/users/{id} (admin only) ---

    @Test
    void deleteById_returnsNoContent_whenAdmin() throws Exception {
        doNothing().when(userService).deleteById(1L);

        mockMvc.perform(delete("/api/users/1").with(csrf()).with(user("admin").roles("ADMIN")))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteById_returnsForbidden_whenUser() throws Exception {
        mockMvc.perform(delete("/api/users/1").with(csrf()).with(user("alice").roles("USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteById_returnsUnauthorized_whenAnonymous() throws Exception {
        mockMvc.perform(delete("/api/users/1").with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deleteById_returns404_whenNotFound() throws Exception {
        doThrow(new ResponseStatusException(NOT_FOUND, "User 99 not found"))
                .when(userService).deleteById(99L);

        mockMvc.perform(delete("/api/users/99").with(csrf()).with(user("admin").roles("ADMIN")))
                .andExpect(status().isNotFound());
    }
}
