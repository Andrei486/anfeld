package popescu.andrei.anfeld.model;

import org.springframework.data.annotation.Id;

public class WildcardCharacter {

    @Id
    private String id;

    private String ownerId = null;
    private String characterName;

    public WildcardCharacter() {}

    public WildcardCharacter(String ownerId) {
        this();
        this.ownerId = ownerId;
    }

    public WildcardCharacter(String ownerId, String characterName) {
        this(ownerId);
        this.characterName = characterName;
    }

    public String getId() {
        return id;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        if (this.ownerId == null) {
            this.ownerId = ownerId;
        }
    }

    public String getCharacterName() {
        return characterName;
    }

    public void setCharacterName(String characterName) {
        this.characterName = characterName;
    }
}
