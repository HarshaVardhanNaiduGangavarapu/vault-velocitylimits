package com.vault.velocitylimits.domain.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vault.velocitylimits.domain.model.LoadFundsAttempt;
import com.vault.velocitylimits.domain.service.LoadFundsException;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class FileWriterUtil {
    private static final String PERIOD = ".";
    private static final String SLASH = "/";
    private static final String NEW_LINE="\n";
    private static ObjectMapper objectMapper = new ObjectMapper();

    public static void writeLoadFundAttemptsToFile(String fileName,List<LoadFundsAttempt> loadFundsAttemptList) {
        FileOutputStream fileOutputStream = null;
        try {
            File outputFile = new File(FileWriterUtil.class.getClassLoader().getResource(PERIOD).getFile() + SLASH + fileName);
            fileOutputStream = new FileOutputStream(outputFile);
            for (LoadFundsAttempt loadFundsAttempt : loadFundsAttemptList) {
                fileOutputStream.write(objectMapper.writeValueAsString(loadFundsAttempt).getBytes());
                fileOutputStream.write(NEW_LINE.getBytes());
            }
        } catch (IOException ex) {
            throw new LoadFundsException("Failed to write transaction attempts info to file." + ex.getMessage());
        } finally {
            IOUtils.closeQuietly(fileOutputStream);
        }
    }
}
