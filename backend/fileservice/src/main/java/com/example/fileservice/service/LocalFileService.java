package com.example.fileservice.service;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.ServletContext;
import lombok.RequiredArgsConstructor;

@Primary
@Service(value = "localFileService")
@RequiredArgsConstructor
public class LocalFileService implements FileService {
    
    private final ServletContext servletContext;

    private Path folderPath;

    private final Logger logger = LoggerFactory.getLogger(LocalFileService.class);


    @PostConstruct
    public void init() {
        String pathStr = servletContext.getRealPath("/uploads");
        logger.debug("pathStr : {}", pathStr);
        folderPath = Paths.get(pathStr);
    }

    @Override
    public List<String> saveFile(String prefix, List<MultipartFile> files) {
        String pathStr = servletContext.getRealPath("/uploads");

        Path dirPath = folderPath.resolve(pathStr);
        throw new UnsupportedOperationException("Unimplemented method 'saveFile'");
    }

}
