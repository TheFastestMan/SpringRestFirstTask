package com.example.springrestfirsttask.controllers.controller;

import com.example.springrestfirsttask.entity.Image;
import com.example.springrestfirsttask.entity.User;
import com.example.springrestfirsttask.servive.ImageService;
import com.example.springrestfirsttask.servive.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/image")
@RequiredArgsConstructor
public class ImageController {
//мууу
    @Autowired
    private final ImageService imageService;
    @Autowired
    private final UserService userService;

    @PostMapping("/{id}/add")
    public String saveImage(@PathVariable("id") Long id,
                            @RequestParam("image") MultipartFile content) {
        Image image = new Image();
        imageService.addImage(image, content, id);
        return "redirect:/users/{id}";
    }


    @PostMapping("/{imageId}/delete")
    public String deleteImage(@PathVariable("imageId") Long imageId, RedirectAttributes redirectAttributes) {
        Optional<Image> imageOptional = imageService.findUserImage(imageId);
        if (imageOptional.isPresent()) {
            Image image = imageOptional.get();
            Long userId = image.getUser().getId(); // Получаем ID пользователя изображения
            imageService.delete(image);
            redirectAttributes.addAttribute("id", userId);
            return "redirect:/users/{id}"; // Перенаправляем на страницу пользователя
        } else {
            // Если изображение не найдено, перенаправляем на общий список пользователей или другую страницу по вашему выбору
            return "redirect:/users";
        }
    }



    @PostMapping("/{id}/update")
    public String updateImage(@PathVariable("id") Long imageId, // Переименовано для ясности
                              @RequestParam("image") MultipartFile newImageContent,
                              RedirectAttributes redirectAttributes) { // Добавлен RedirectAttributes для передачи параметров
        if (newImageContent.isEmpty()) {
            // Обрабатываем случай, когда файл не был отправлен
            return "redirect:/users"; // Вернуть на список пользователей, если не указано куда именно
        }

        // Найти существующее изображение пользователя
        Optional<Image> currentImageOpt = imageService.findUserImage(imageId);
        if (!currentImageOpt.isPresent()) {
            // Обрабатываем случай, когда изображение не найдено
            return "redirect:/users"; // Вернуть на список пользователей, если изображение не найдено
        }

        // Получаем существующее изображение
        Image currentImage = currentImageOpt.get();

        // Обновляем файл изображения
        imageService.updateImage(currentImage, newImageContent);

        Long userId = currentImage.getUser().getId(); // Извлекаем ID пользователя из изображения
        redirectAttributes.addAttribute("id", userId); // Добавляем ID пользователя в параметры перенаправления
        return "redirect:/users/{id}"; // Перенаправляем на страницу пользователя
    }



}
