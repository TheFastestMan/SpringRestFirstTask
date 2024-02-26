package com.example.springrestfirsttask.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
@Getter
@Setter
public class ImageCreateDTO {

    private Long id;

   private MultipartFile image;
    private Long userId;

}
