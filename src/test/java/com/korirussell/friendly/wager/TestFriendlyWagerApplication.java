package com.korirussell.friendly.wager;

import org.springframework.boot.SpringApplication;

public class TestFriendlyWagerApplication {

	public static void main(String[] args) {
		SpringApplication.from(FriendlyWagerApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
