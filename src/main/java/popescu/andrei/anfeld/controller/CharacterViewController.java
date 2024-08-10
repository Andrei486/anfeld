package popescu.andrei.anfeld.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import popescu.andrei.anfeld.model.WildcardCharacter;
import popescu.andrei.anfeld.repository.WildcardCharacterRepository;

import java.util.LinkedList;
import java.util.List;

@Controller
public class CharacterViewController {

    @Autowired
    WildcardCharacterRepository characterRepository;

    @GetMapping("/api/characters/{ownerId}")
    @ResponseBody
    public List<WildcardCharacter> getCharactersForOwner(
            Model model,
            @PathVariable String ownerId,
            @AuthenticationPrincipal OAuth2User principal) {
        if (!ownerId.equals(principal.getName())) {
            return new LinkedList<>(); // trying to access another user's characters is not allowed
        }
        return characterRepository.findCharactersByOwnerId(ownerId);
    }

    @GetMapping("/characterList")
    public String viewCharacters(
            Model model,
            @AuthenticationPrincipal OAuth2User principal) {
        model.addAttribute("characters", characterRepository.findCharactersByOwnerId(principal.getName()));
        return "viewCharacters";
    }
}
