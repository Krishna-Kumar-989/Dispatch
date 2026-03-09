package com.clark.roper.Dispatch.api.localfileapi;

import com.clark.roper.Dispatch.api.FileStorageApi;
import com.clark.roper.Dispatch.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

/** Local filesystem implementation of {@link FileStorageApi}. **/



@Component
public class LocalFileStorageApi implements FileStorageApi {

  @Value("${app.upload.dir}")
  private String uploadDir;

  private static final Map<String, String> MIME_TO_EXT = Map.of(
      "image/jpeg", ".jpg",
      "image/png", ".png",
      "image/gif", ".gif",
      "image/webp", ".webp");

  @Override
  public String store(MultipartFile file, String contentType) {
    try {
      Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
      Files.createDirectories(uploadPath);

      String extension = MIME_TO_EXT.get(contentType);
      if (extension == null) {
        throw new BadRequestException("Unsupported file type");
      }

      String filename = UUID.randomUUID() + extension;
      Path filePath = uploadPath.resolve(filename);
      file.transferTo(filePath.toFile());

      return "/uploads/" + filename;
    } catch (IOException e) {
      throw new BadRequestException("Failed to upload file");
    }
  }
}
