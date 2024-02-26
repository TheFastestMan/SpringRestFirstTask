package com.example.springrestfirsttask.controllers.controller;

import com.example.springrestfirsttask.entity.Image;
import com.example.springrestfirsttask.entity.User;
import com.example.springrestfirsttask.servive.ImageService;
import com.example.springrestfirsttask.servive.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
        Optional<User> userOptional = userService.findById(id);
        model.addAttribute("images", imageService.findImagesByUserId(id));
        if (userOptional.isPresent()) {
            model.addAttribute("user", userOptional.get());
        } else {
            return "redirect:/users";
        }
        return "userGallery";
    }


}