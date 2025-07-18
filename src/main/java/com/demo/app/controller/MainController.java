package com.demo.app.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
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
        .region(Region.of("eu-north-1"))  // 👈 Update to your region
        .credentialsProvider(EnvironmentVariableCredentialsProvider.create())  // ✅ Use env vars (works in Docker/EC2)
        .build();

    // ✅ Simple test endpoint
    @GetMapping("/hello")
    public String hello() {
        return "Hello from Spring Boot on EC2!";
    }

    // ✅ GET users
    @GetMapping("/users")
    public List<Map<String, Object>> getUsers() {
        Map<String, Object> user1 = Map.of("id", 1, "name", "Seshadri Kandala");
        Map<String, Object> user2 = Map.of("id", 2, "name", "Project Demo Session");
        return List.of(user1, user2);
    }

    // ✅ Create user
    @PostMapping("/users/create")
    public Map<String, Object> createUser(@RequestParam String name) {
        Map<String, Object> user = new HashMap<>();
        user.put("id", new Random().nextInt(1000));
        user.put("name", name);
        user.put("uploadStatus", "Simulated upload of: " + name + "_profile.txt");
        return user;
    }

    // ✅ Upload file to S3
    @PostMapping("/upload")
    public Map<String, Object> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        String bucketName = System.getenv("S3_BUCKET");  // ✅ Use env variable

        if (bucketName == null || bucketName.isEmpty()) {
            throw new RuntimeException("S3_BUCKET environment variable is not set");
        }

        s3.putObject(PutObjectRequest.builder()
                .bucket(bucketName)
                .key(file.getOriginalFilename())
                .build(),
            RequestBody.fromBytes(file.getBytes()));

        return Map.of(
            "message", "File uploaded successfully",
            "fileName", file.getOriginalFilename()
        );
    }
}
