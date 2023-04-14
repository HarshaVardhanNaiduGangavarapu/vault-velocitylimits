package com.vault.velocitylimits.domain.util;

import com.vault.velocitylimits.domain.model.LoadFundsAttempt;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;

public class FileWriterUtil {
    public static void writeLoadFundAttemptsToFile(List<LoadFundsAttempt> loadFundsAttemptList) {
        FileOutputStream fileOutputStream = null;
        try {
            URL url = FileWriterUtil.class.getClass().getResource("output.txt");
            File outputFile = new File(url.getPath());
            fileOutputStream = new FileOutputStream(outputFile);
            for (LoadFundsAttempt loadFundsAttempt : loadFundsAttemptList) {
               fileOutputStream.write(loadFundsAttempt.toString().getBytes());
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
