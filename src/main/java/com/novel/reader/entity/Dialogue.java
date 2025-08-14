package com.novel.reader.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "dialogues")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Dialogue {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "character_id")
    private Character character;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapter_id")
    private Chapter chapter;
    
    @Column(name = "start_position")
    private Integer startPosition; // 在章节中的起始位置
    
    @Column(name = "end_position")
    private Integer endPosition; // 在章节中的结束位置
    
    @Column(name = "voice_file_path")
    private String voiceFilePath; // 生成的语音文件路径
    
    @Column(name = "is_generated")
    private Boolean isGenerated = false; // 是否已生成语音
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
} 