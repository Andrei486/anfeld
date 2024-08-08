package popescu.andrei.anfeld.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import popescu.andrei.anfeld.model.WildcardCharacter;
import popescu.andrei.anfeld.repository.WildcardCharacterRepository;

import java.util.List;

@Controller
public class CharacterController {

    @Autowired
    WildcardCharacterRepository characterRepository;

    @GetMapping("/createCharacterForm")
    public String createCharacterForm(Model model, @AuthenticationPrincipal OAuth2User principal) {
        model.addAttribute("character", new WildcardCharacter());
        return "createCharacter";
    }

    @PostMapping("/createCharacter")
    public String createCharacter(
            @ModelAttribute("character") WildcardCharacter character,
            @AuthenticationPrincipal OAuth2User principal) {
        character.setOwnerId(principal.getName());
        characterRepository.save(character);
        return "redirect:/";
    }

    @GetMapping("characters/{ownerId}")
    @ResponseBody
    public List<WildcardCharacter> getCharactersForOwner(
            Model model,
            @PathVariable String ownerId,
            @AuthenticationPrincipal OAuth2User principal) {
        if (!ownerId.equals(principal.getName())) {
            return null; // trying to access another user's characters
        }
        return characterRepository.findCharactersByOwnerId(ownerId);
    }
}
