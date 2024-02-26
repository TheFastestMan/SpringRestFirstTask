package com.example.springrestfirsttask.mapper;

import com.example.springrestfirsttask.dto.ImageCreateDTO;
import com.example.springrestfirsttask.entity.Image;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ImageCreateMapper {

    @Mapping(target = "image", ignore = true) // Ignore
    ImageCreateDTO imageToImageCreateDTO(Image image);

    @Mapping(target = "image", source = "image.originalFilename")
    Image imageCreateDTOtoImage(ImageCreateDTO dto);
}
