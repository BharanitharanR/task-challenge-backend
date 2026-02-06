package com.banyan.orchestrator;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Utility class for parsing ZIP files containing source artifacts.
 * Provides secure ZIP extraction with protection against Zip Slip attacks.
 */
public final class ZipFileParser {
    
    /**
     * Parses a ZIP file and extracts all JSON source files.
     * 
     * @param zipFilePath Path to the ZIP file
     * @return List of JSON source strings
     * @throws IOException if file cannot be read or parsed
     */
    public static List<String> parseZipFile(String zipFilePath) throws IOException {
        Path tempDir = Files.createTempDirectory("banyan-zip-extract-");
        
        try {
            // Extract ZIP contents to temporary directory
            extractZip(zipFilePath, tempDir);
            
            // Read all JSON files from the extracted directory
            return readJsonFiles(tempDir);
            
        } finally {
            // Clean up temporary directory
            deleteDirectory(tempDir);
        }
    }
    
    /**
     * Extracts a ZIP file to the specified directory with security checks.
     * 
     * @param zipFilePath Path to the ZIP file
     * @param targetDir Target directory for extraction
     * @throws IOException if extraction fails
     */
    private static void extractZip(String zipFilePath, Path targetDir) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFilePath))) {
            ZipEntry entry = zis.getNextEntry();
            
            while (entry != null) {
                Path newPath = targetDir.resolve(entry.getName()).normalize();
                
                // Security check: Zip Slip protection
                if (!newPath.startsWith(targetDir)) {
                    throw new IOException("Entry is outside of the target dir: " + entry.getName());
                }
                
                if (entry.isDirectory()) {
                    Files.createDirectories(newPath);
                } else {
                    Files.createDirectories(newPath.getParent());
                    Files.copy(zis, newPath, StandardCopyOption.REPLACE_EXISTING);
                }
                
                zis.closeEntry();
                entry = zis.getNextEntry();
            }
        }
    }
    
    /**
     * Reads all JSON files from a directory recursively.
     * 
     * @param directory Directory to search for JSON files
     * @return List of JSON content strings
     * @throws IOException if files cannot be read
     */
    private static List<String> readJsonFiles(Path directory) throws IOException {
        List<String> jsonFiles = new ArrayList<>();
        
        Files.walk(directory)
             .filter(path -> path.toString().endsWith(".json"))
             .forEach(path -> {
                 try {
                     String content = Files.readString(path);
                     jsonFiles.add(content);
                 } catch (IOException e) {
                     throw new RuntimeException("Failed to read JSON file: " + path, e);
                 }
             });
        
        return jsonFiles;
    }
    
    /**
     * Recursively deletes a directory and all its contents.
     * 
     * @param directory Directory to delete
     * @throws IOException if deletion fails
     */
    private static void deleteDirectory(Path directory) throws IOException {
        Files.walk(directory)
             .sorted((a, b) -> b.compareTo(a)) // Reverse order for deletion
             .map(Path::toFile)
             .forEach(File::delete);
    }
}