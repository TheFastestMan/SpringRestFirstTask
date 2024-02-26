package com.example.springrestfirsttask.controllers.rest;

import com.example.springrestfirsttask.servive.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/rest")
@RequiredArgsConstructor
public class ImageRestController {
    @Autowired
    private final ImageService imageService;

    @GetMapping("/images/{id}")
    public ResponseEntity<byte[]> findImageById(@PathVariable Long id) {
        return imageService.findImageById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

}
