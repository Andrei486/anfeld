package popescu.andrei.anfeld.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import popescu.andrei.anfeld.repository.WildcardCharacterRepository;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class CharacterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private WildcardCharacterRepository characterRepository;

    @Test
    @DirtiesContext
    public void testCreateCharacterForm() throws Exception {
        // the name of the default user is "user"
        // set the name attribute (display name) manually
        mockMvc.perform(MockMvcRequestBuilders.get("/createCharacterForm")
                        .with(csrf())
                        .with(oauth2Login().attributes(attr -> attr.put("name", "displayName")))
                        .accept(MediaType.TEXT_HTML))
                .andExpect(status().is2xxSuccessful())
                .andExpect(xpath("//input[@id=\"characterName\"]").exists());
    }

    @Test
    @DirtiesContext
    public void testCreateCharacterFormNotLoggedInUnauthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/createCharacterForm")
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }
}
