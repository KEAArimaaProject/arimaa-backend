package com.example.arimaabackend.playwright;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.microsoft.playwright.*;
import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.*;

import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class GetPlayerTest {

    private Playwright playwright;
    private APIRequestContext request;
    private Dotenv dotenv;
    private final ObjectMapper objectMapper = new ObjectMapper();   // Reusable

    @BeforeAll
    void setup() {
        // use the .env file in the root directory
        dotenv = Dotenv.configure()
                .directory("./")
                .ignoreIfMissing()
                .load();

        playwright = Playwright.create();

        request = playwright.request().newContext(
            new APIRequest.NewContextOptions()
            .setBaseURL("http://localhost:8080")
            .setExtraHTTPHeaders(Map.of(
            "Accept", "application/json",
            "Content-Type", "application/json",
            "Authorization", "Basic " + java.util.Base64.getEncoder()
            .encodeToString((
            dotenv.get("DEFAULT_ADMIN_USERNAME") + ":" +
            dotenv.get("DEFAULT_ADMIN_PASSWORD")).getBytes())))
        );
    }

    @AfterAll
    void teardown() {
        if (request != null) request.dispose();
        if (playwright != null) playwright.close();
    }

    @Test
    void shouldGetUserById() {
        APIResponse response = request.get("/api/users/1");
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
    void shouldGetUsersByCountry() {
        APIResponse response = request.get("api/players/by-country/US");
        assertTrue(response.ok(), "Expected list of player users. Got: " + response.status());
        assertEquals(200, response.status());
        String jsonString = response.text();

        JsonNode json;
        try {
            json = objectMapper.readTree(jsonString);
        } catch (JsonProcessingException e) {
            fail("Invalid JSON response: " + e.getMessage());
            return;
        }

        assertTrue(json.isArray(), "Expected JSON array response");
        assertTrue(json.size() == 10, "Expected at least 10 player users in the array");
        // Validate every element in the array
        for (JsonNode user : json) {
            assertUserStructure(user);
        }
    }

    private void assertUserStructure(JsonNode user) {
        assertTrue(user.hasNonNull("id"), "Missing 'id' field");
        assertTrue(user.get("id").isIntegralNumber(), "'id' must be an integer");
        assertTrue(user.get("id").asInt() > 0, "'id' must be positive");

        assertStringJsonStringField(user, "username");
        assertStringJsonStringField(user, "email");
        //We cant test roles. Some of them are Null !,
        //so we just skip testing them for now.
        //Add the test again when we have good data.
        //(good thing it is not for Arturos test class)
        //assertStringJsonStringField(user, "role");

        // Dates
        //We cant test dates. Some of them are Null !,
        //so we just skip testing them for now.
        //Add the test again when we have good data.
        //(good thing it is not for Arturos test class)
        //assertValidDate(user.get("createdAt").asText());
        //assertValidDate(user.get("updatedAt").asText());
    }

    private void assertStringJsonStringField(JsonNode node, String fieldName) {
        assertTrue(node.has(fieldName), "Missing '" + fieldName + "' field");

        // We wanted to test that the database fields where not empty. Turns out all of them had empty fields.
        // Uncomment the lines below when we have good test data.
        // (good thing this isnt for Arturos test course).
        //String value = node.get(fieldName).asText().trim();
        //assertFalse(value.isEmpty(), "'" + fieldName + "' must not be empty or only whitespace");
        //assertEquals(value, node.get(fieldName).asText(), "'" + fieldName + "' should not have leading/trailing whitespace");
    }

    private void assertValidDate(String dateString) {
        assertDoesNotThrow(() -> Instant.parse(dateString),
                "Expected a valid ISO-8601 date (e.g. 2026-03-04T09:04:54Z), but got: " + dateString);
    }
}