package com.example.projetoRestSpringBoot.services;

import com.example.projetoRestSpringBoot.controller.FileController;
import com.example.projetoRestSpringBoot.exception.FileNotFoundException;
import com.example.projetoRestSpringBoot.exception.FileStorageException;
import com.example.projetoRestSpringBoot.config.FileStorageConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileStorageService {
    private final Path fileStorageLocation;

    private static final Logger logger = LoggerFactory.getLogger(FileController.class);

    @Autowired
    public FileStorageService(FileStorageConfig fileStorageConfig) {
        Path path = Paths.get(fileStorageConfig.getUploadDir())
                .toAbsolutePath()
                .normalize();

        this.fileStorageLocation = path;
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception e) {
            throw new FileStorageException("Nao pode criar o diretorio onde os arquivos serao armazenados.", e);
        }
    }
        public String storeFile(MultipartFile file){
            String name = StringUtils.cleanPath(file.getOriginalFilename());
            try {
                if(name.contains("..")){
                    throw new FileStorageException("Arquivo possui um nome invalido " + name + ". Por favor, tente novamente!");
                }
                Path targetLocation = this.fileStorageLocation.resolve(name);
                Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
                return name;
        }catch (Exception e){
                throw new FileStorageException("Nao foi possivel armazenar o arquivo " + name + ". Por favor, tente novamente!", e);
            }
        }

        public Resource loadFileAsResource(String name) {
            try {
                Path filePath = this.fileStorageLocation.resolve(name).normalize();
                Resource resource = new UrlResource(filePath.toUri());

                if (resource.exists()) {
                    return resource;
                } else {
                    throw new FileNotFoundException("Arquivo nao encontrado " + name);
                }
            } catch (Exception e) {
                throw new FileNotFoundException("Arquivo nao encontrado " + name, e);
            }
        }


}
