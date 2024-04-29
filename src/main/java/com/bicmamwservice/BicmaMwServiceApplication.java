package com.bicmamwservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.Collections;

@SpringBootApplication
public class BicmaMwServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(BicmaMwServiceApplication.class, args);
	}

	@Bean
	public RestTemplate restTemplate(){
		return new RestTemplate();
	}
	@Bean
	public Docket swaggerConfiguration()
	{
		return new Docket(DocumentationType.SWAGGER_2)
				.select()
				.apis(RequestHandlerSelectors.basePackage("com.bircmamwservice"))
				.build()
				.apiInfo(apiInfo());
	}

	private ApiInfo apiInfo()

	{
		return new ApiInfo(
				"BICMA Middleware API's",
				"All API of BICMA Middleware",
				"1.0",
				"Easy to Understand if any confusion contact Back-end Team",
				new springfox.documentation.service.Contact("Beck-end Team","www.itechnologies.bt","faisal@itechnologies.bt"),
				"API Licence",
				"www.itechnologies.bt",
				Collections.emptyList()
		);
	}

}
