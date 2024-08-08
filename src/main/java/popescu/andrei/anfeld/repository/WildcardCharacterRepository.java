package popescu.andrei.anfeld.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import popescu.andrei.anfeld.model.WildcardCharacter;

import java.util.List;

public interface WildcardCharacterRepository extends MongoRepository<WildcardCharacter, String> {

    WildcardCharacter findCharacterById(String id);

    List<WildcardCharacter> findCharactersByOwnerId(String ownerId);
}
