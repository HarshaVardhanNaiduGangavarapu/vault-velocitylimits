package com.vault.velocitylimits.domain.util;

import com.vault.velocitylimits.domain.service.LoadFundsException;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * @author harshagangavarapu
 */
public class FileReaderUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileReaderUtil.class);

    // prevent initialization
    private FileReaderUtil() {
    }

    /**
     * This method reads the contents of the file with the given fileName, which is expected to be located in the resources folder,
     * and returns a list of the file's lines.
     *
     * @param fileName - the name of the file to be read
     * @return List<String> - a list of the file's lines
     */
    public static List<String> readCustomerLoadFundsFromInputFile(String fileName) {
        LOGGER.info("Reading load funds transactions data from input file: {}", fileName);
        try {
            URL inputFileURL = FileReaderUtil.class.getClassLoader().getResource(fileName);
            File file = new File(inputFileURL.getFile());
            return FileUtils.readLines(file);
        } catch (IOException ex) {
            LOGGER.error("Failed to read load funds transactions data from input file: {}", fileName);
            throw new LoadFundsException("Failed to read load funds transactions data." + ex.getMessage());
        }
    }
}
