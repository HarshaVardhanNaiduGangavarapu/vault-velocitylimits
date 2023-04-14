package com.vault.velocitylimits;

import com.vault.velocitylimits.domain.service.LoadCustomerFundsService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
@Configuration
public class VelocitylimitsApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context =SpringApplication.run(VelocitylimitsApplication.class, args);
		context.getBean(LoadCustomerFundsService.class).loadFunds();
	}

}
