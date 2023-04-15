package com.vault.velocitylimits.domain.service;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.File;
import java.io.IOException;

/**
 * @author harshagangavarapu
 */

@SpringBootTest
@ActiveProfiles("test")
public class LoadServiceE2ETest {
    @Value("${load.funds.expected.output.file}")
    private String expectedOutputFile;
    @Value("${load.funds.output.file}")
    private String actualOutputFile;
    @Autowired
    private ILoadFundsService loadFundsService;

    @Test
    @DisplayName("Test End-to-End functionality of load funds service")
    public void testLoadFundsEndToEnd() throws IOException {
        // setup
        File expectedFile = new File(this.getClass().getClassLoader().getResource(expectedOutputFile).getFile());
        String expectedOutput = FileUtils.readFileToString(expectedFile).replace("\r\n","\n");

        // execute
        loadFundsService.executeFundsLoadingToAccounts();

        // verify
        File actualFile = new File(this.getClass().getClassLoader().getResource(actualOutputFile).getFile());
        String actualOutput = FileUtils.readFileToString(actualFile).trim();
        Assertions.assertEquals(expectedOutput, actualOutput);
    }
}
