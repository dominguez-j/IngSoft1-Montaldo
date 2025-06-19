package ar.uba.fi.grupo4.ingsoft1.futbol5api;

import org.springframework.boot.SpringApplication;

public class TestFutbol5API {

	public static void main(String[] args) {
		SpringApplication.from(Futbol5API::main).with(TestcontainersConfiguration.class).run(args);
	}
}
