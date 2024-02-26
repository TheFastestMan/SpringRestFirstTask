package com.example.springrestfirsttask.controllers.controller;

import com.example.springrestfirsttask.dto.ImageCreateDTO;
import com.example.springrestfirsttask.dto.ImageReadDTO;

import com.example.springrestfirsttask.servive.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.Optional;

@Controller
@RequestMapping("/image")
@RequiredArgsConstructor
public class ImageController {


    private final ImageService imageService;

    @PostMapping("/{id}/add")
    public String saveImage(@PathVariable("id") Long id,
                            @RequestParam("image") MultipartFile content) throws IOException {
        ImageCreateDTO dto = new ImageCreateDTO();
        dto.setImage(content);
        dto.setUserId(id);
        imageService.addImage(dto);
        return "redirect:/users/{id}";
    }


    @PostMapping("/{imageId}/delete")
    public String deleteImage(@PathVariable("imageId") Long imageId, RedirectAttributes redirectAttributes) {
        Optional<ImageReadDTO> imageOptional = imageService.findUserImage(imageId);
        if (imageOptional.isPresent()) {
            ImageReadDTO image = imageOptional.get();
            Long userId = image.getUserId(); // Теперь получаем ID пользователя из ImageReadDTO
            imageService.delete(image); // Используем ID для удаления, без необходимости конвертации обратно в сущность
            redirectAttributes.addAttribute("id", userId);
            return "redirect:/users/{id}";
        } else {
            return "redirect:/users";
        }
    }

    @PostMapping("/{id}/update")
    public String updateImage(@PathVariable("id") Long imageId,
                              @RequestParam("image") MultipartFile newImageContent,
                              RedirectAttributes redirectAttributes) {
        // Проверяем, был ли файл отправлен
        if (newImageContent.isEmpty()) {
            // Файл не был отправлен, возвращаем пользователя на страницу со списком пользователей
            return "redirect:/users";
        }

        ImageCreateDTO imageDTO = new ImageCreateDTO();
        imageDTO.setId(imageId);
        imageDTO.setImage(newImageContent);

        // Обновляем изображение
        try {
            imageService.updateImage(imageDTO);

            // После успешного обновления, ищем userId, связанный с изображением
            Long userId = imageService.findUserIdByImageId(imageId); // Метод должен быть определен в ImageService
            if (userId != null) {
                // Если userId найден, перенаправляем на страницу пользователя
                return "redirect:/users/" + userId;
            } else {
                return "redirect:/users";
            }
        } catch (Exception e) {

            return "redirect:/users";
        }
    }


}
