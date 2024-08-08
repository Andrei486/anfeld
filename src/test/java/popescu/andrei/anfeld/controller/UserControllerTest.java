package popescu.andrei.anfeld.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    /**
     * Tests that the /user endpoint correctly returns user information when a user is logged in.
     * @throws Exception if the endpoint can't be accessed at all
     */
    @Test
    @DirtiesContext
    public void testGetUser() throws Exception {
        // the name of the default user is "user"
        // set the name attribute (display name) manually
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/user")
                        .with(csrf())
                        .with(oauth2Login().attributes(attr -> attr.put("name", "displayName")))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
        // check that the response is correct
        var objectMapper = new ObjectMapper();
        var response = result.getResponse().getContentAsString();
        Map<String, Object> map = objectMapper.readValue(response, new TypeReference<>() {});
        assertEquals("displayName", map.get("name"));
        assertEquals("user", map.get("id"));
    }

    /**
     * Tests that the /user endpoint returns an unauthorized error code when a user that isn't logged in accesses it.
     * @throws Exception if the endpoint can't be accessed at all
     */
    @Test
    @DirtiesContext
    public void testGetUserNotLoggedInUnauthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/user")
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }
}
