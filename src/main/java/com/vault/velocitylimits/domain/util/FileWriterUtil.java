package com.vault.velocitylimits.domain.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vault.velocitylimits.domain.model.LoadFundsAttempt;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.List;

public class FileWriterUtil {
    private static ObjectMapper objectMapper = new ObjectMapper();
    public static void writeLoadFundAttemptsToFile(List<LoadFundsAttempt> loadFundsAttemptList) {
        FileOutputStream fileOutputStream = null;
        try {
            File outputFile = new File(FileWriterUtil.class.getClassLoader().getResource(".").getFile()+"/output.txt");
            fileOutputStream = new FileOutputStream(outputFile);
            for (LoadFundsAttempt loadFundsAttempt : loadFundsAttemptList) {
               fileOutputStream.write(objectMapper.writeValueAsString(loadFundsAttempt).getBytes());
               fileOutputStream.write("\n".getBytes());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        finally {
            IOUtils.closeQuietly(fileOutputStream);
        }
    }
}
