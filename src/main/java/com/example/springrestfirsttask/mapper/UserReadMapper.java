package com.example.springrestfirsttask.mapper;

import com.example.springrestfirsttask.dto.UserReadDTO;
import com.example.springrestfirsttask.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserReadMapper {

    @Mapping(source = "username", target = "username")
    UserReadDTO userToUserReadDTO(User user);

    @Mapping(source = "username", target = "username")
    User userReadDTOtoUser(UserReadDTO dto);
}
