//package com.example.board_api.file.domain.entity;
//
//import com.example.board_api.file.domain.FileCategory;
//import jakarta.persistence.*;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//
//import static lombok.AccessLevel.PROTECTED;
//
//@Entity
//@Getter
//@Table(name = "files")
//@NoArgsConstructor(access = PROTECTED)
//public class File {
//    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long fileId;
//
//    private String filePath = "/public/images/default-profile.png";
//
//    @Column(nullable = false, columnDefinition = "TINYINT")
//    private FileCategory fileCategory;
//
//    private Long uploaderId;  // 업로더 추적용 (nullable)
//
//    // Constructor
//    public File(String filePath, FileCategory fileCategory, Long uploaderId) {
//        this.filePath = filePath;
//        this.fileCategory = fileCategory;
//        this.uploaderId = uploaderId;
//    }
//
//    // Factory Methods
//    public static File createProfileImage(String filePath, Long uploaderId) {
//        return new File(filePath, FileCategory.PROFILE_IMAGE, uploaderId);
//    }
//}