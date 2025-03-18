package com.example.fileservice.service;


import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service(value = "s3FileService")
public class S3FileService implements FileService {

    private final Logger logger = LoggerFactory.getLogger(S3FileService.class);

    @Override
    public List<String> saveFile(String prefix, List<MultipartFile> files) {
        logger.debug("");
        throw new UnsupportedOperationException("Unimplemented method 'saveFile'");
    }

}
