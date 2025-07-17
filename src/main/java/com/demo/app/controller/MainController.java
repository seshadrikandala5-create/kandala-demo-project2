package com.demo.app.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.*;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api")
public class MainController {

    private final S3Client s3 = S3Client.builder()
        .region(Region.of("us-east-1"))
        .credentialsProvider(ProfileCredentialsProvider.create())
        .build();

    @GetMapping("/users")
    public List<Map<String, Object>> getUsers() {
        Map<String, Object> user1 = new HashMap<>();
        user1.put("id", 1);
        user1.put("name", "Alice");

        Map<String, Object> user2 = new HashMap<>();
        user2.put("id", 2);
        user2.put("name", "Bob");

        return Arrays.asList(user1, user2);
    }

    @PostMapping("/users/create")
    public Map<String, Object> createUser(@RequestParam String name) {
        Map<String, Object> user = new HashMap<>();
        user.put("id", new Random().nextInt(1000));
        user.put("name", name);
        user.put("uploadStatus", "Simulated upload of: " + name + "_profile.txt");
        return user;
    }

    @PostMapping("/upload")
    public Map<String, Object> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        s3.putObject(PutObjectRequest.builder()
                .bucket("your-s3-bucket")
                .key(file.getOriginalFilename())
                .build(),
            RequestBody.fromBytes(file.getBytes()));

        Map<String, Object> response = new HashMap<>();
        response.put("message", "File uploaded successfully");
        response.put("fileName", file.getOriginalFilename());
        return response;
    }
}
