package com.example.springrestfirsttask.controllers.controller;

import com.example.springrestfirsttask.dto.ImageReadDTO;
import com.example.springrestfirsttask.dto.UserReadDTO;
import com.example.springrestfirsttask.servive.ImageService;
import com.example.springrestfirsttask.servive.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    @Autowired
    private final UserService userService;
    @Autowired
    private final ImageService imageService;

    @GetMapping
    public String users(Model model) {
        model.addAttribute("users", userService.findAllUsers());
        return "users";
    }


    @GetMapping("/{id}")
    public String user(@PathVariable("id") Long id, Model model) {
        Optional<UserReadDTO> userOptional = userService.findUserByIdDTO(id);
        List<ImageReadDTO> imageDTOs = imageService.findImagesByUserIdDTO(id);
        model.addAttribute("images", imageDTOs);
        userOptional.ifPresent(user -> model.addAttribute("user", user));
        return userOptional.isPresent()
                ? "userGallery"
                : "redirect:/users";
    }


}