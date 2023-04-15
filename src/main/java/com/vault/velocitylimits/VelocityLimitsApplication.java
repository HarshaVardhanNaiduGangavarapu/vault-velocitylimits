package com.vault.velocitylimits;

import com.vault.velocitylimits.domain.service.impl.LoadFundsService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;

/**
 * @author harshagangavarapu
 */
@SpringBootApplication
@Configuration
public class VelocityLimitsApplication {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(VelocityLimitsApplication.class, args);
        context.getBean(LoadFundsService.class).executeFundsLoadingToAccounts();
    }
}
