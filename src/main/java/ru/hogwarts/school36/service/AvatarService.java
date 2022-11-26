package ru.hogwarts.school36.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school36.exception.StudentNotFoundException;
import ru.hogwarts.school36.model.Avatar;
import ru.hogwarts.school36.model.Student;
import ru.hogwarts.school36.repository.AvatarRepository;
import ru.hogwarts.school36.repository.StudentRepository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.nio.file.StandardOpenOption.CREATE_NEW;

public class AvatarService {

    private final AvatarRepository avatarRepository;
    private final StudentRepository studentRepository;

    @Value("${path.to.avatars.folder}")
    private String avatarsDir;


    public AvatarService(AvatarRepository avatarRepository, StudentRepository studentRepository) {
        this.avatarRepository = avatarRepository;
        this.studentRepository = studentRepository;
    }


    public void uploadAvatar(Long studentId, MultipartFile file) throws IOException {
        Student student = studentRepository.getById(studentId);
        Path filePath = Path.of(avatarsDir, studentId + "." + getExtension(file.getOriginalFilename()));
        Files.createDirectories(filePath.getParent());
        Files.deleteIfExists(filePath);

        try (InputStream is = file.getInputStream();
             OutputStream os = Files.newOutputStream(filePath, CREATE_NEW);
             BufferedInputStream bis = new BufferedInputStream(is, 1024);
             BufferedOutputStream bos = new BufferedOutputStream(os, 1024);
        ) {
            bis.transferTo(bos);
        }

        Avatar avatar = avatarRepository.findByStudentId(studentId).orElseGet(Avatar::new);
        avatar.setStudent(student);
        avatar.setFilePath(filePath.toString());
        avatar.setFileSize(file.getSize());
        avatar.setMediaType(file.getContentType());
        avatar.setData(file.getBytes());

        avatarRepository.save(avatar);
    }

    public Avatar findAvatar(long studentId) {
        return avatarRepository.findByStudentId(studentId).orElse(new Avatar());
    }

    private String getExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

}
