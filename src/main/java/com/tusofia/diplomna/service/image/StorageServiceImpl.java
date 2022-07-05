package com.tusofia.diplomna.service.image;

import com.tusofia.diplomna.exception.StorageException;
import com.tusofia.diplomna.exception.StorageFileNotFoundException;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class StorageServiceImpl implements StorageService {

  private final Path rootLocation;

  @Autowired
  public StorageServiceImpl(StorageProperties properties) {
    this.rootLocation = Paths.get(properties.getLocation());
    init();
  }

  @Override
  public void store(MultipartFile file, Long id) {
    try {
      if (file.isEmpty()) {
        throw new StorageException("Failed to store empty file " + file.getOriginalFilename());
      }
      if (ImageIO.read(file.getInputStream()) == null) {
        throw new StorageException(
            "Failed to store empty file " + file.getOriginalFilename() + " - Not an image");
      }
      Files.copy(
          file.getInputStream(),
          this.rootLocation.resolve(id.toString() + ".jpg"),
          StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
      throw new StorageException("Failed to store file " + file.getOriginalFilename(), e);
    }
  }

  @Override
  public Path load(String filename) {
    return rootLocation.resolve(filename);
  }

  @Override
  public Resource loadAsResource(String filename) {
    try {
      Path file = load(filename);
      Resource resource = new UrlResource(file.toUri());
      if (resource.exists() || resource.isReadable()) {
        return resource;
      } else {
        throw new StorageFileNotFoundException("Could not read file: " + filename);
      }
    } catch (MalformedURLException e) {
      throw new StorageFileNotFoundException("Could not read file: " + filename, e);
    }
  }

  @Override
  public void deleteAll() {
    FileSystemUtils.deleteRecursively(rootLocation.toFile());
  }

  @Override
  public void deleteById(Long id) {
    Path path = load(id.toString() + ".jpg");
    try {
      Files.deleteIfExists(path);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public boolean isCorrectImageType(MultipartFile file) {
    String extension = FilenameUtils.getExtension(file.getOriginalFilename()).toLowerCase();
    if (extension != null && !extension.isEmpty()) {
      if (!extension.contains("png") && !extension.contains("jpg")) {
        return false;
      }
    }
    return true;
  }

  @Override
  public void init() {
    try {
      Files.createDirectory(rootLocation);
    } catch (FileAlreadyExistsException e) {
      System.out.println("Directory already exists!");
    } catch (IOException e) {
      throw new StorageException("Could not initialize storage", e);
    }
  }
}
