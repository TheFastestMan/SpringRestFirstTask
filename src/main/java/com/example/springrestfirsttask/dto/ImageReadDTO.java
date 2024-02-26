package com.example.springrestfirsttask.dto;

import com.example.springrestfirsttask.entity.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ImageReadDTO {
    private Long id;

    private String image;

    private Long userId;

}
