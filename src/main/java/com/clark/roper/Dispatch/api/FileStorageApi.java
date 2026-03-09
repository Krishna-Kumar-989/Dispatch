package com.clark.roper.Dispatch.api;

import org.springframework.web.multipart.MultipartFile;

//Abstraction for file storage.


public interface FileStorageApi {


   // Stores a file and returns the public-facing URL.
   // @param file        the uploaded multipart file
   // @param contentType the MIME type of the file
   // @return the URL/path to access the stored file

  String store(MultipartFile file, String contentType);
}
