package com.example.purchase_system.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import java.util.List;

public class CsvFileUtil {
    public static List<String> loadLines(String folder, String fileName
                            , String firstRow) throws IOException {
        Path path = Path.of(folder, fileName);
        if(!Files.exists(path)) {
            Files.createDirectories(path.getParent());
            Files.createFile(path);
            Files.writeString(path, firstRow + System.lineSeparator()
                                , StandardCharsets.UTF_8);
        }
        return Files.readAllLines(path, StandardCharsets.UTF_8);
    }
    
}
