package ar.com.aeb.alquileres.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    public String storeFile(String basePath, MultipartFile file, String... pathSegments) {
        try {
            Path root = Paths.get(basePath, pathSegments);
            if (!Files.exists(root)) {
                Files.createDirectories(root);
            }

            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path targetLocation = root.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // Join segments with "/" and the filename
            StringBuilder relativePath = new StringBuilder();
            for (String segment : pathSegments) {
                relativePath.append(segment).append("/");
            }
            relativePath.append(fileName);

            return relativePath.toString();
        } catch (IOException e) {
            throw new RuntimeException("Could not store file " + file.getOriginalFilename() + ". Please try again!", e);
        }
    }

    public void deleteFile(String basePath, String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return;
        }
        try {
            Path path = Paths.get(basePath).resolve(filePath).normalize();
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new RuntimeException("Could not delete file " + filePath, e);
        }
    }
}
