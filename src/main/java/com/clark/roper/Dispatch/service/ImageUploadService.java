package com.clark.roper.Dispatch.service;

import com.clark.roper.Dispatch.api.FileStorageApi;
import com.clark.roper.Dispatch.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

/**
 * Handles image validation and delegates storage to the active
 * {@link FileStorageApi} implementation.
 */
@Service
@RequiredArgsConstructor
public class ImageUploadService {

  private final FileStorageApi fileStorageApi;

  private static final long MAX_SIZE_BYTES = 5 * 1024 * 1024;

  private static final Set<String> ALLOWED_TYPES = Set.of(
      "image/jpeg", "image/png", "image/gif", "image/webp");


   // Validates and stores an image file and return public url
   //@throws BadRequestException if validation fails or IO error occurs


   public String upload(MultipartFile file) {
    validate(file);
    return fileStorageApi.store(file, file.getContentType());
  }

  private void validate(MultipartFile file) {
    if (file.isEmpty()) {
      throw new BadRequestException("File is empty");
    }
    String contentType = file.getContentType();
    if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
      throw new BadRequestException("Only JPEG, PNG, GIF, and WebP images are allowed");
    }
    if (file.getSize() > MAX_SIZE_BYTES) {
      throw new BadRequestException("File size must be under 5MB");
    }
  }
}
