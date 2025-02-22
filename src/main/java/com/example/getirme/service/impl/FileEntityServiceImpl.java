package com.example.getirme.service.impl;

import com.example.getirme.model.FileEntity;
import com.example.getirme.repository.FileEntityRepository;
import com.example.getirme.service.IFileEntityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Base64;
import java.util.UUID;

@Service
public class FileEntityServiceImpl implements IFileEntityService {

    private String uploadDir = "./images";

    @Autowired
    FileEntityRepository fileEntityRepository;

    @Override
    public  FileEntity saveFileEntity(MultipartFile file) {
        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String fileExtension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
            String fileName = UUID.randomUUID().toString() + fileExtension;

            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);


            FileEntity fileEntity = new FileEntity();
            fileEntity.setName(file.getOriginalFilename());
            fileEntity.setType(file.getContentType());
            fileEntity.setSize(file.getSize());
            fileEntity.setData(fileName);
            return fileEntityRepository.save(fileEntity);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] fileToByteArray(FileEntity fileEntity){
        try{
            Path filePath = Paths.get(uploadDir).resolve(fileEntity.getData());
            return Files.readAllBytes(filePath);
        }catch (IOException e){
            throw new RuntimeException("IOException while reading file");
        }
    }


}
