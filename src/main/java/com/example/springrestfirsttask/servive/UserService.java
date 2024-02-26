package com.example.springrestfirsttask.servive;


import com.example.springrestfirsttask.dto.UserReadDTO;
import com.example.springrestfirsttask.entity.User;
import com.example.springrestfirsttask.mapper.UserReadMapper;
import com.example.springrestfirsttask.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final UserReadMapper userReadMapper;

    public List<User> findAllUsers(){
       return userRepository.findAll();
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<UserReadDTO> findUserByIdDTO(Long id) {
        return Optional.ofNullable(userRepository.findById(id)
                .map(userReadMapper::userToUserReadDTO)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id)));
    }

}
