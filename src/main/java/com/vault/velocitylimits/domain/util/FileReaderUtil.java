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
     * This method read the contents of the given fileName present in resources folder and return list of file lines.
     *
     * @param fileName
     * @return List
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
