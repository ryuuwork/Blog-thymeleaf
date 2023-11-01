package com.tuananhdo.utils;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;


public class FileUploadUtil {
    public static final String USER_PHOTOS = "user-photos/";
    public static final String POST_PHOTOS = "post-photos/";
    public static String getPhotoFolderId(String folderType, Long id) {
        return folderType + id;
    }

    public static String getOriginalFileName(MultipartFile multipartFile) {
        return StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
    }

    public static void saveFileAndCleanOldImage(MultipartFile multipartFile, String fileName, String uploadDir) throws IOException {
        FileUploadUtil.cleanDir(uploadDir);
        FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
    }

    public static void saveFile(String uploadDir, String fileName, MultipartFile multipartFile) throws IOException {
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        try (InputStream inputStream = multipartFile.getInputStream()) {
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException exception) {
            throw new IOException("Could not save file :" + fileName, exception);
        }
    }

    public static void cleanDir(String dir) throws IOException {
        Path dirPath = Paths.get(dir);
        File file = dirPath.toFile();
        FileUtils.deleteDirectory(file);
    }
}
