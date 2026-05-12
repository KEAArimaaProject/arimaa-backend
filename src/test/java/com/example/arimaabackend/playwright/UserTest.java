package com.example.arimaabackend.playwright;

import com.example.arimaabackend.dto.UserCreateRequest;
import com.example.arimaabackend.dto.UserResponse;
import com.example.arimaabackend.dto.UserUpdateRequest;
import com.microsoft.playwright.options.RequestOptions;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.microsoft.playwright.*;
import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.*;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserTest {

    private Playwright playwright;
    private Dotenv dotenv;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private APIRequestContext adminRequest;
    private APIRequestContext userRequest;

    @BeforeAll
    void setup() {
        // use the .env file in the root directory
        dotenv = Dotenv.configure()
                .directory("./")
                .ignoreIfMissing()
                .load();

        playwright = Playwright.create();

        String adminAuth = Base64.getEncoder().encodeToString(
                (dotenv.get("DEFAULT_ADMIN_USERNAME", "admin1") + ":" +
                        dotenv.get("DEFAULT_ADMIN_PASSWORD", "password")).getBytes());

        String userAuth = Base64.getEncoder().encodeToString(
                (dotenv.get("DEFAULT_USER_USERNAME", "user1") + ":" +
                        dotenv.get("DEFAULT_USER_PASSWORD", "password")).getBytes());


        adminRequest = playwright.request().newContext(
                new APIRequest.NewContextOptions()
                        .setBaseURL("http://localhost:8080")
                        .setExtraHTTPHeaders(Map.of(
                                "Accept", "application/json",
                                "Content-Type", "application/json",
                                "Authorization", "Basic " + adminAuth))
        );

        userRequest = playwright.request().newContext(
                new APIRequest.NewContextOptions()
                        .setBaseURL("http://localhost:8080")
                        .setExtraHTTPHeaders(Map.of(
                                "Accept", "application/json",
                                "Content-Type", "application/json",
                                "Authorization", "Basic " + userAuth))
        );
    }

    @AfterAll
    void teardown() {
        if (adminRequest != null) adminRequest.dispose();
        if (userRequest != null) userRequest.dispose();
        if (playwright != null) playwright.close();
    }

    @Test
    void AsUser_FailToCreateUser() {
        String uniqueSuffix = String.valueOf(System.currentTimeMillis());
        UserCreateRequest createUser = new UserCreateRequest(
                "usernametest_" + uniqueSuffix,
                "usernametest_" + uniqueSuffix + "@unknown.invalid",
                "usernametest1_userCantCreateuser");
        APIResponse response = userRequest.post("/api/users", RequestOptions.create().setData(createUser));
        assertFalse(response.ok(), "Expected 4xx status. Got: " + response.status());
        assertEquals(403, response.status());
    }

    @Test
    void AsUser_FailToDeleteUser() {
        APIResponse getFirstUserResponse = adminRequest.get("/api/users/" + 1);
        assertTrue(getFirstUserResponse.ok(), "Expected 2xx status. Got: " + getFirstUserResponse.status());
        assertEquals(200, getFirstUserResponse.status());

        APIResponse responseDelete = userRequest.delete("/api/users/" + 1);
        assertFalse(responseDelete.ok(), "Expected 4xx status. Got: " + responseDelete.status());
        assertEquals(403, responseDelete.status());
    }

    @Test
    void AsAdmin_CreateThenDeleteUser() {
        // Create user (to delete later)
        String username = "AsAdmin_CreateThenDeleteUser";
        UserCreateRequest createUser = new UserCreateRequest(
                username,
                "usernametest_" + username + "@unknown.invalid",
                "username_123_" + username);
        APIResponse response = adminRequest.post("/api/users", RequestOptions.create().setData(createUser));
        assertTrue(response.ok(), "Expected 2xx status. Got: " + response.status());
        assertEquals(201, response.status());
        JsonNode json;
        try {
            json = objectMapper.readTree(response.text());
        } catch (JsonProcessingException e) {
            fail("Invalid JSON response: " + e.getMessage());
            return;
        }
        Integer newUserId = json.path("id").asInt();
        String newUserName = json.path("username").asText();
        assertEquals(username, newUserName);

        // get user by newUserId
        APIResponse getNewUserResponse = adminRequest.get("/api/users/" + newUserId);
        assertTrue(getNewUserResponse.ok(), "Expected 2xx status. Got: " + getNewUserResponse.status());
        assertEquals(200, getNewUserResponse.status());

        // delete the user
        APIResponse responseDelete = adminRequest.delete("/api/users/" + newUserId);
        assertTrue(responseDelete.ok(), "Expected 2xx status. Got: " + responseDelete.status());
        assertEquals(204, responseDelete.status());

        // try to find the deleted user again
        APIResponse responseget18deleted = adminRequest.get("/api/users/" + newUserId);
        assertEquals(404, responseget18deleted.status(),
                "Expected 404 Not Found after user deletion, but got: " + response.status());
    }


    @Test
    void AsAdmin_GetUserById() {
        APIResponse response = adminRequest.get("/api/users/1");
        assertTrue(response.ok(), "Expected 2xx status. Got: " + response.status());
        assertEquals(200, response.status());
        String jsonString = response.text();

        JsonNode json;
        try {
            json = objectMapper.readTree(jsonString);
        } catch (JsonProcessingException e) {
            fail("Invalid JSON response: " + e.getMessage());
            return;
        }
        assertEquals(1, json.get("id").asInt());
        assertEquals("bot_ShallowBlue", json.get("username").asText());
        assertEquals("bot_ShallowBlue@unknown.invalid", json.get("email").asText());
        assertEquals("PLAYER", json.get("role").asText());

        assertValidDate(json.get("createdAt").asText());
        assertValidDate(json.get("updatedAt").asText());
    }

    @Test
    void AsAdmin_FailToGetMissingUserById() {
        APIResponse response = adminRequest.get("/api/users/43543637");
        assertFalse(response.ok(), "Expected 4xx status. Got: " + response.status());
        assertEquals(404, response.status());
    }

    @Test
    void AsAdmin_UpdateUser() throws JsonProcessingException {
        // Create user (to update later)
        String uniqueSuffix = String.valueOf(System.currentTimeMillis());
        UserCreateRequest createUser = new UserCreateRequest(
                "update_me_" + uniqueSuffix,
                "update_me_" + uniqueSuffix + "@example.com",
                "password123");
        APIResponse createResponse = adminRequest.post("/api/users", RequestOptions.create().setData(createUser));
        assertEquals(201, createResponse.status());
        JsonNode json = objectMapper.readTree(createResponse.text());
        Integer userId = json.get("id").asInt();

        // Update user
        UserUpdateRequest updateRequest = new UserUpdateRequest(
                "updated_" + uniqueSuffix,
                "updated_" + uniqueSuffix + "@example.com",
                "new_password123");

        APIResponse updateResponse = adminRequest.put("/api/users/" + userId, RequestOptions.create().setData(updateRequest));
        assertEquals(200, updateResponse.status());
        JsonNode updatedJson = objectMapper.readTree(updateResponse.text());
        Integer newUserID = updatedJson.get("id").asInt();
        assertEquals("updated_" + uniqueSuffix, updatedJson.get("username").asText());
        assertEquals("updated_" + uniqueSuffix + "@example.com", updatedJson.get("email").asText());

        // get user by newUserId
        APIResponse getNewUserResponse = adminRequest.get("/api/users/" + newUserID);
        assertTrue(getNewUserResponse.ok(), "Expected 2xx status. Got: " + getNewUserResponse.status());
        assertEquals(200, getNewUserResponse.status());

        // delete the user
        APIResponse responseDelete = adminRequest.delete("/api/users/" + newUserID);
        assertTrue(responseDelete.ok(), "Expected 2xx status. Got: " + responseDelete.status());
        assertEquals(204, responseDelete.status());

        // try to find the deleted user again
        APIResponse responseget18deleted = adminRequest.get("/api/users/" + newUserID);
        assertEquals(404, responseget18deleted.status(),
                "Expected 404 Not Found after user deletion, but got: " + responseget18deleted.status());
    }

    @Test
    void AsUser_FailToUpdateUser() {
        // Try to update username, email and password of user 1
        UserUpdateRequest updateRequest = new UserUpdateRequest(
                "hacker",
                "hacker@example.com",
                "hacked123");

        APIResponse updateResponse = userRequest.put("/api/users/1", RequestOptions.create().setData(updateRequest));
        // The users attempt to update the user should fail with a 403 Forbidden
        assertEquals(403, updateResponse.status());
    }

    private void assertValidDate(String dateString) {
        assertDoesNotThrow(() -> Instant.parse(dateString),
                "Expected a valid date (e.g. 2026-03-04T09:04:54Z), but got: " + dateString);
    }
}
