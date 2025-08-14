package com.novel.reader.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "characters")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Character {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "voice_characteristics")
    private String voiceCharacteristics; // 语音特征描述
    
    @Column(name = "voice_model")
    private String voiceModel; // 使用的语音模型
    
    @Column(name = "voice_speed")
    private Float voiceSpeed = 1.0f; // 语速
    
    @Column(name = "voice_pitch")
    private Float voicePitch = 1.0f; // 音调
    
    @Column(name = "voice_volume")
    private Float voiceVolume = 1.0f; // 音量
    
    @Column(name = "personality_traits")
    private String personalityTraits; // 性格特征
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "novel_id")
    private Novel novel;
    
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