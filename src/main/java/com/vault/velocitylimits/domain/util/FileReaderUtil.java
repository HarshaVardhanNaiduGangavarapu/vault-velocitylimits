package com.vault.velocitylimits.domain.util;

import com.vault.velocitylimits.domain.service.LoadFundsException;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 * @author harshavardhannaidugangavarapu
 */
public class FileReaderUtil {
    // prevent initialization
    private FileReaderUtil() {
    }

    public static List<String> readCustomerLoadFundsFromInputFile(String fileName) {
        try {
            URL inputFileURL = FileReaderUtil.class.getClassLoader().getResource(fileName);
            File file = new File(inputFileURL.getFile());
            return FileUtils.readLines(file);
        } catch (IOException ex) {
            throw new LoadFundsException("Failed to read load funds transactions data."+ ex.getMessage());
        }
    }
}
