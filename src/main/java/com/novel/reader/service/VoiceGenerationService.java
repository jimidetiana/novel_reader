package com.novel.reader.service;

import com.novel.reader.entity.Character;
import com.novel.reader.entity.Dialogue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class VoiceGenerationService {



    @Value("${voice.output.directory:voices}")
    private String voiceOutputDirectory;


    /**
     * 为对话生成语音
     */
    public String generateVoiceForDialogue(Dialogue dialogue) {
        try {
            Character character = dialogue.getCharacter();
            String text = dialogue.getContent();
            
            // 创建语音输出目录
            createVoiceDirectory();
            
            // 生成文件名
            String fileName = generateFileName(dialogue);
            String filePath = voiceOutputDirectory + File.separator + fileName;
            
            // 构建语音生成提示
            String prompt = buildVoicePrompt(character, text);
            
            // 调用OpenAI TTS API生成语音
            byte[] audioData = generateTTS(prompt, character);
            
            // 保存音频文件
            saveAudioFile(filePath, audioData);
            
            // 更新对话记录
            dialogue.setVoiceFilePath(filePath);
            dialogue.setIsGenerated(true);
            
            log.info("Generated voice for dialogue: {}", fileName);
            return filePath;
            
        } catch (Exception e) {
            log.error("Error generating voice for dialogue: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 构建语音生成提示
     */
    private String buildVoicePrompt(Character character, String text) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("角色：").append(character.getName()).append("\n");
        
        if (character.getDescription() != null) {
            prompt.append("角色描述：").append(character.getDescription()).append("\n");
        }
        
        if (character.getPersonalityTraits() != null) {
            prompt.append("性格特征：").append(character.getPersonalityTraits()).append("\n");
        }
        
        if (character.getVoiceCharacteristics() != null) {
            prompt.append("语音特征：").append(character.getVoiceCharacteristics()).append("\n");
        }
        
        prompt.append("对话内容：").append(text).append("\n");
        prompt.append("请根据角色特征生成符合人物特色的语音。");
        
        return prompt.toString();
    }

    /**
     * 调用OpenAI TTS API生成语音
     */
    private byte[] generateTTS(String prompt, Character character) {
        try {
            // 这里使用OpenAI的TTS API
            // 注意：实际实现需要根据OpenAI的TTS API文档进行调整
            
            // 构建TTS请求
            String voiceModel = character.getVoiceModel() != null ? 
                character.getVoiceModel() : "alloy"; // 默认使用alloy模型
            
            // 这里应该调用实际的TTS API
            // 由于OpenAI Java库可能不直接支持TTS，这里提供框架代码
            return generateTTSWithOpenAI(prompt, voiceModel, character);
            
        } catch (Exception e) {
            log.error("Error calling TTS API: {}", e.getMessage(), e);
            return new byte[0];
        }
    }

    /**
     * 使用千问生成TTS（框架代码）
     */
    private static final String MODEL = "qwen-tts";
    private byte[] generateTTSWithOpenAI(String text, String voice, Character character) {
        // 这里需要根据OpenAI的TTS API实现
        // 由于OpenAI Java库的限制，这里提供占位符实现

        try {
            // 构建HTTP请求到OpenAI TTS API
            // 实际实现需要使用HTTP客户端调用OpenAI的TTS端点
            
            // 示例请求结构：
            // POST https://api.openai.com/v1/audio/speech
            // {
            //   "model": "tts-1",
            //   "input": text,
            //   "voice": voice,
            //   "speed": character.getVoiceSpeed(),
            //   "response_format": "mp3"
            // }

            log.info("Would generate TTS for text: {}", text.substring(0, Math.min(50, text.length())));
            
            // 返回模拟的音频数据
            return generateMockAudioData();
            
        } catch (Exception e) {
            log.error("Error in TTS generation: {}", e.getMessage(), e);
            return new byte[0];
        }
    }

    /**
     * 生成模拟音频数据（用于测试）
     */
    private byte[] generateMockAudioData() {
        // 生成一个简单的音频文件头（MP3格式）
        byte[] header = {
            0x49, 0x44, 0x33, // ID3
            0x03, 0x00, // Version
            0x00, // Flags
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 // Size
        };
        
        // 添加一些模拟的音频数据
        byte[] audioData = new byte[1024];
        for (int i = 0; i < audioData.length; i++) {
            audioData[i] = (byte) (Math.random() * 256);
        }
        
        byte[] result = new byte[header.length + audioData.length];
        System.arraycopy(header, 0, result, 0, header.length);
        System.arraycopy(audioData, 0, result, header.length, audioData.length);
        
        return result;
    }

    /**
     * 保存音频文件
     */
    private void saveAudioFile(String filePath, byte[] audioData) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            fos.write(audioData);
            fos.flush();
        }
    }

    /**
     * 创建语音输出目录
     */
    private void createVoiceDirectory() throws IOException {
        Path directory = Paths.get(voiceOutputDirectory);
        if (!Files.exists(directory)) {
            Files.createDirectories(directory);
        }
    }

    /**
     * 生成文件名
     */
    private String generateFileName(Dialogue dialogue) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String characterName = dialogue.getCharacter().getName().replaceAll("[^a-zA-Z0-9]", "_");
        return String.format("%s_%s_%s.mp3", characterName, dialogue.getId(), timestamp);
    }

    /**
     * 批量生成语音
     */
    public void generateVoicesForDialogues(List<Dialogue> dialogues) {
        for (Dialogue dialogue : dialogues) {
            if (!dialogue.getIsGenerated()) {
                generateVoiceForDialogue(dialogue);
                
                // 添加延迟避免API限制
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }

    /**
     * 获取语音文件
     */
    public byte[] getVoiceFile(String filePath) {
        try {
            Path path = Paths.get(filePath);
            if (Files.exists(path)) {
                return Files.readAllBytes(path);
            }
        } catch (IOException e) {
            log.error("Error reading voice file: {}", e.getMessage(), e);
        }
        return new byte[0];
    }
} 