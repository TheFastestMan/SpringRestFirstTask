package com.example.springrestfirsttask.servive;

import com.example.springrestfirsttask.dto.ImageCreateDTO;
import com.example.springrestfirsttask.dto.ImageReadDTO;
import com.example.springrestfirsttask.dto.UserReadDTO;
import com.example.springrestfirsttask.entity.Image;
import com.example.springrestfirsttask.entity.User;

import com.example.springrestfirsttask.mapper.ImageCreateMapper;
import com.example.springrestfirsttask.mapper.ImageReadMapper;
import com.example.springrestfirsttask.mapper.UserReadMapper;
import com.example.springrestfirsttask.repository.ImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;



@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ImageService {
    private final ImageRepository imageRepository;
    private final UserService userService;
    private final UserReadMapper userReadMapper;
    private final ImageReadMapper imageReadMapper;
    private final ImageCreateMapper imageCreateMapper;


    @Value("${bucket}")
    private String bucket;

    @SneakyThrows
    @Transactional
    public void addImage(ImageCreateDTO imageDTO) throws IOException {
        if (!imageDTO.getImage().isEmpty()) {
            Image image = imageCreateMapper.imageCreateDTOtoImage(imageDTO);

            // Здесь загружаем файл и получаем имя файла или путь к нему
            String filename = imageDTO.getImage().getOriginalFilename();
            upload(filename, imageDTO.getImage().getInputStream()); // Метод upload должен возвращать путь к файлу после его сохранения

            Optional<UserReadDTO> userReadDTO = Optional.ofNullable(findUser(imageDTO.getUserId())
                    .orElseThrow(()
                            -> new IllegalArgumentException("User not found with id: " + imageDTO.getUserId())));
            User user = userReadMapper.userReadDTOtoUser(userReadDTO.get());
            image.setUser(user);

            imageRepository.save(image);
        }
    }


    public Optional<UserReadDTO> findUser(Long id) {
        return userService.findById(id)
                .map(userReadMapper::userToUserReadDTO);
    }
    public Long findUserIdByImageId(Long imageId) {
        return imageRepository.findById(imageId)
                .map(Image::getUser)
                .map(User::getId)
                .orElse(null);
    }

    public List<ImageReadDTO> findImagesByUserIdDTO(Long id) {
        List<Image> images = imageRepository.findAllByUserId(id);
        return images.stream()
                .map(imageReadMapper::imageToImageReadDTO)
                .collect(Collectors.toList());
    }


    public Optional<ImageReadDTO> findUserImage(Long id) {
        return imageRepository.findById(id)
                .map(imageReadMapper::imageToImageReadDTO);
    }


    @Transactional
    public void delete(ImageReadDTO dto) {
        Image image = imageReadMapper.imageReadDTOtoImage(dto);
        imageRepository.delete(image);
    }

    @SneakyThrows
    @Transactional
    public void updateImage(ImageCreateDTO imageDTO) {
        Image image = imageRepository.findById(imageDTO.getId())
                .orElseThrow(() -> new IllegalArgumentException("Image not found with id: " + imageDTO.getId()));

        if (!imageDTO.getImage().isEmpty()) {
            String filename = StringUtils.cleanPath(imageDTO.getImage().getOriginalFilename());
            upload(filename, imageDTO.getImage().getInputStream());
            image.setImage(filename); // Обновляем путь к файлу
            imageRepository.save(image); // Сохраняем обновленное изображение
        }
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
