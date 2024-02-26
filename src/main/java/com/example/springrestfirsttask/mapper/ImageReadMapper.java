package com.example.springrestfirsttask.mapper;

import com.example.springrestfirsttask.dto.ImageReadDTO;
import com.example.springrestfirsttask.dto.UserReadDTO;
import com.example.springrestfirsttask.entity.Image;
import com.example.springrestfirsttask.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ImageReadMapper {

    @Mapping(source = "user.id", target = "userId")
    ImageReadDTO imageToImageReadDTO(Image image);

    Image imageReadDTOtoImage(ImageReadDTO dto);
}
