package popescu.andrei.anfeld;

import org.springframework.boot.SpringApplication;

public class TestAnfeldCharacterBuilderApplication {

	public static void main(String[] args) {
		SpringApplication.from(AnfeldCharacterBuilderApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
