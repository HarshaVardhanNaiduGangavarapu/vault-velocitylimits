package com.vault.velocitylimits.domain.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vault.velocitylimits.domain.model.LoadFundsAttempt;
import com.vault.velocitylimits.domain.service.LoadFundsException;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * @author harshagangavarapu
 */
public class FileWriterUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileReaderUtil.class);
    private static final String PERIOD = ".";
    public static final String SLASH = "/";
    private static final String NEW_LINE = "\n";
    private static ObjectMapper objectMapper = new ObjectMapper();

    // prevent initialization
    private FileWriterUtil() {
    }

    /**
     * This method writes the content of the list into the given fileName under resource folder.
     *
     * @param fileName
     * @param loadFundsAttemptList
     */
    public static void writeLoadFundAttemptsToFile(String fileName, List<LoadFundsAttempt> loadFundsAttemptList) {
        FileOutputStream fileOutputStream = null;
        try {
            LOGGER.info("Writing load funds transaction attempts into file with fileName: " + fileName);
            File outputFile = new File(FileWriterUtil.class.getClassLoader().getResource(PERIOD).getFile() + SLASH + fileName);
            fileOutputStream = new FileOutputStream(outputFile);
            for (LoadFundsAttempt loadFundsAttempt : loadFundsAttemptList) {
                fileOutputStream.write(getJSONStringFromObj(loadFundsAttempt).getBytes());
                fileOutputStream.write(NEW_LINE.getBytes());
            }
        } catch (IOException ex) {
            LOGGER.error("Failed to write load funds transaction attempts info to file.", ex);
            throw new LoadFundsException("Failed to write load funds transaction attempts info to file." + ex.getMessage());
        } finally {
            IOUtils.closeQuietly(fileOutputStream);
        }
    }

    /**
     * This method converts the object value into a JSON string
     *
     * @param objectValue
     * @return String
     */
    public static String getJSONStringFromObj(Object objectValue) {
        try {
            return objectMapper.writeValueAsString(objectValue);
        } catch (JsonProcessingException jpe) {
            LOGGER.error("Failed to convert object value to JSON String.", jpe);
            throw new LoadFundsException("Failed to convert object value to JSON String." + jpe.getMessage());
        }
    }
}
