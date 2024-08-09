package popescu.andrei.anfeld.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import popescu.andrei.anfeld.model.WildcardCharacter;
import popescu.andrei.anfeld.repository.WildcardCharacterRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class CharacterViewControllerTest {

    private static final String DEFAULT_USER_ID = "user"; // as defined by the oauthlogin test user

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WildcardCharacterRepository characterRepository;

    @BeforeEach
    public void setup() {
        characterRepository.deleteAll();
        var character1 = new WildcardCharacter(DEFAULT_USER_ID, "Test1");
        var character2 = new WildcardCharacter(DEFAULT_USER_ID, "Test2");
        var character3 = new WildcardCharacter(DEFAULT_USER_ID + "1", "Test3");
        characterRepository.save(character1);
        characterRepository.save(character2);
        characterRepository.save(character3);
    }

    @Test
    @DirtiesContext
    public void testGetCharactersForUser() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/characters/" + DEFAULT_USER_ID)
                        .with(csrf())
                        .with(oauth2Login().attributes(attr -> attr.put("name", "displayName"))))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
        var objectMapper = new ObjectMapper();
        var response = result.getResponse().getContentAsString();
        List<WildcardCharacter> characterList = objectMapper.readValue(response, new TypeReference<>() {});
        var characterNames = characterList.stream().map(WildcardCharacter::getCharacterName).toList();
        assertTrue(characterNames.contains("Test1"));
        assertTrue(characterNames.contains("Test2"));
        assertFalse(characterNames.contains("Test3"));
    }

    @Test
    @DirtiesContext
    public void testGetCharacterWithoutUserUnauthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/characters/user")
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DirtiesContext
    public void testGetCharacterForOtherUserEmpty() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/characters/" + DEFAULT_USER_ID + "1")
                        .with(csrf())
                        .with(oauth2Login().attributes(attr -> attr.put("name", "displayName"))))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
        var objectMapper = new ObjectMapper();
        var response = result.getResponse().getContentAsString();
        List<WildcardCharacter> characterList = objectMapper.readValue(response, new TypeReference<>() {});
        var characterNames = characterList.stream().map(WildcardCharacter::getCharacterName).toList();
        assertFalse(characterNames.contains("Test1"));
        assertFalse(characterNames.contains("Test2"));
    }

    @Test
    @DirtiesContext
    public void testViewCharacters() throws Exception {
        // the name of the default user is "user"
        mockMvc.perform(MockMvcRequestBuilders.get("/characterList")
                        .with(csrf())
                        .with(oauth2Login().attributes(attr -> attr.put("name", "displayName")))
                        .accept(MediaType.TEXT_HTML))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("viewCharacters"))
                .andExpect(xpath("//h2[text()='Test1']").exists())
                .andExpect(xpath("//h2[text()='Test2']").exists())
                .andExpect(xpath("//h2[text()='Test3']").doesNotExist());
    }
}
