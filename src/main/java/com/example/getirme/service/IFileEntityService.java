package com.example.getirme.service;

import com.example.getirme.model.FileEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;

public interface IFileEntityService {
    FileEntity saveFileEntity(MultipartFile file);
    byte[] fileToByteArray(FileEntity fileEntity);
    void deleteFileFromDisk(FileEntity fileEntity);
}
