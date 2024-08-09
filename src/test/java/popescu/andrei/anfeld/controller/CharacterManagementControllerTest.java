package popescu.andrei.anfeld.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import popescu.andrei.anfeld.model.WildcardCharacter;
import popescu.andrei.anfeld.repository.WildcardCharacterRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class CharacterManagementControllerTest {

    private static final String DEFAULT_USER_ID = "user"; // as defined by the oauthlogin test user

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WildcardCharacterRepository characterRepository;

    @BeforeEach
    public void setup() {
        characterRepository.deleteAll();
    }

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

    @Test
    @DirtiesContext
    public void testCreateCharacter() throws Exception {
        final String characterName = "testCharacterName";
        var character = new WildcardCharacter();
        character.setCharacterName(characterName);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/createCharacter")
                        .flashAttr("character", character)
                        .with(csrf())
                        .with(oauth2Login().attributes(attr -> attr.put("name", "displayName"))))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/")); // test redirect to index, this may change
        // confirm character was created with correct ID
        var addedCharacter = characterRepository.findCharacterById(character.getId());
        assertEquals(DEFAULT_USER_ID, addedCharacter.getOwnerId());
        assertEquals(characterName, addedCharacter.getCharacterName());
    }

    @Test
    @DirtiesContext
    public void testCreateCharacterNotLoggedInUnauthorized() throws Exception {
        final String characterName = "testCharacterName";
        var character = new WildcardCharacter();
        character.setCharacterName(characterName);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/createCharacter")
                        .flashAttr("character", character)
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }
}
