package com.tuananhdo.service;

import com.tuananhdo.payload.PostDTO;
import com.tuananhdo.payload.UserDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileUploadService {

    void saveUserWithFile(UserDTO userDTO, MultipartFile multipartFile) throws IOException;

    void savePostWithFile(PostDTO postDTO, MultipartFile multipartFile) throws IOException;

    void cleanUploadDir(Long id) throws IOException;
}
