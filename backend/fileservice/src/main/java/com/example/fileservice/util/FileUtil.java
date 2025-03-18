package com.example.fileservice.util;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

public class FileUtil {

    public enum FileType {
        LOCAL,
        SERVER
    }

    public String saveFile(Path folderDir, MultipartFile file, FileType type) throws IOException {
        Files.createDirectories(folderDir);

        String originFileName = Optional.ofNullable(file.getOriginalFilename())
                .map(StringUtils::cleanPath)  // 파일명 정리
                .filter(name -> !name.isBlank())  // 빈 파일명 거르기
                .orElseThrow(() -> new IllegalArgumentException("파일명이 비어 있음"));

        String baseName = StringUtils.getFilename(originFileName);
        String extension = StringUtils.getFilenameExtension(originFileName);
        String fileSuffix = ObjectUtils.isEmpty(extension) ? "" : "." + extension;

        Path targetPath = folderDir.resolve(originFileName);

        int count = 1;

        while (Files.exists(targetPath)) {
            originFileName = String.format("%s (%d)%s", baseName, count++, fileSuffix);
            targetPath = folderDir.resolve(originFileName);
        }

        try (InputStream in = file.getInputStream()) {
            Files.copy(in, targetPath, StandardCopyOption.REPLACE_EXISTING);
        }

        return originFileName;
    }

}
