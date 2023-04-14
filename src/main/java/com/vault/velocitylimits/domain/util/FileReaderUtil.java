package com.vault.velocitylimits.domain.util;

import com.vault.velocitylimits.domain.model.LoadCustomerFunds;
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

    public static List<String> readCustomerLoadFundsFromInputFile() {
        try {
            URL inputFileURL = FileReaderUtil.class.getClassLoader().getResource("input.txt");
            File file = new File(inputFileURL.getFile());
            return FileUtils.readLines(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
