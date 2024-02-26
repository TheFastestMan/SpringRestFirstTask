package com.example.springrestfirsttask.servive;

import com.example.springrestfirsttask.entity.Image;
import com.example.springrestfirsttask.entity.User;

import com.example.springrestfirsttask.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Optional;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ImageService {
    private final ImageRepository imageRepository;
    private final UserService userService;


    @Value("${bucket}")
    private String bucket;

    @SneakyThrows
    @Transactional
    public void addImage(Image image, MultipartFile content, Long id) {
        if (!content.isEmpty()) {
            String filename = content.getOriginalFilename();
            User user = findUser(id).orElseThrow(() ->
                    new IllegalArgumentException("User not found with id: " + id));
            image.setImage(filename);
            image.setUser(user);
            upload(filename, content.getInputStream());
            imageRepository.save(image);
        }
    }

    public Optional<User> findUser(Long id) {
        return userService.findById(id);
    }

    public List<Image> findImagesByUserId(Long id) {
        return imageRepository.findAllByUserId(id);
    }

    public Optional<Image> findUserImage(Long id) {
        return imageRepository.findById(id);
    }

    @Transactional
    public void delete(Image image) {
        imageRepository.delete(image);
    }

    @SneakyThrows
    @Transactional
    public void updateImage(Image currentImage, MultipartFile newImageContent) {
        String filename = newImageContent.getOriginalFilename();
        upload(filename, newImageContent.getInputStream());
        // Обновляем путь к файлу изображения в базе данных
        currentImage.setImage(filename);
        imageRepository.save(currentImage);
    }


    public Optional<byte[]> findImageById(Long id) {
        return imageRepository.findById(id)
                .map(Image::getImage)
                .filter(StringUtils::hasText)
                .map(this::get)
                .map(Optional::get);
    }

    @SneakyThrows
    private Optional<byte[]> get(String imagePath) {
        Path fullImagePath = Path.of(bucket, imagePath);
        try {
            return Files.exists(fullImagePath)
                    ? Optional.of(Files.readAllBytes(fullImagePath))
                    : Optional.empty();
        } catch (IOException e) {
            System.err.println("Error reading image file: " + e.getMessage());
            return Optional.empty();
        }
    }

    @SneakyThrows
    private void upload(String filename, InputStream content) { // загружаем изображение
        Path filePath = Path.of(bucket, filename);

        try {
            Files.createDirectories(filePath.getParent());
            Files.write(filePath, content.readAllBytes(),
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save file", e);
        }
    }

}
