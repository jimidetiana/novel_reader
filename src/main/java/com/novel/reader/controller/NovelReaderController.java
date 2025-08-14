package com.novel.reader.controller;

import com.novel.reader.entity.*;
import com.novel.reader.entity.Character;
import com.novel.reader.service.DialogueExtractionService;
import com.novel.reader.service.VoiceGenerationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@Slf4j
public class NovelReaderController {

    private final DialogueExtractionService dialogueExtractionService;
    private final VoiceGenerationService voiceGenerationService;

    /**
     * 主页
     */
    @GetMapping("/")
    public String index() {
        return "index";
    }

    /**
     * 上传小说页面
     */
    @GetMapping("/upload")
    public String uploadPage() {
        return "upload";
    }

    /**
     * 处理小说上传
     */
    @PostMapping("/upload")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> uploadNovel(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam("author") String author,
            @RequestParam(value = "description", required = false) String description) {
        
        try {
            //保存文件
            file.transferTo(new File("novel/" + title));
            // 读取文件内容
            String content = new String(file.getBytes(), StandardCharsets.UTF_8);
            
            // 创建小说对象
            Novel novel = new Novel();
            novel.setTitle(title);
            novel.setAuthor(author);
            novel.setDescription(description);
            novel.setContent(content);
            
            // 创建章节
            Chapter chapter = new Chapter();
            chapter.setTitle("第一章");
            chapter.setContent(content);
            chapter.setChapterNumber(1);
            chapter.setNovel(novel);
            
            // 创建默认角色（这里简化处理，实际应该通过AI分析提取角色）
            Character character1 = new Character();
            character1.setName("主角");
            character1.setDescription("故事的主要角色");
            character1.setVoiceCharacteristics("年轻、充满活力");
            character1.setPersonalityTraits("勇敢、善良");
            character1.setVoiceModel("alloy");
            character1.setNovel(novel);
            
            Character character2 = new Character();
            character2.setName("配角");
            character2.setDescription("故事的重要配角");
            character2.setVoiceCharacteristics("成熟、稳重");
            character2.setPersonalityTraits("智慧、冷静");
            character2.setVoiceModel("echo");
            character2.setNovel(novel);
            
            // 提取对话
            List<Character> characters = List.of(character1, character2);
            List<Dialogue> dialogues = dialogueExtractionService.extractDialogues(chapter, characters);
            
            // 生成语音
            voiceGenerationService.generateVoicesForDialogues(dialogues);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "小说上传成功",
                "dialoguesCount", dialogues.size(),
                "novelId", novel.getId()
            ));
            
        } catch (IOException e) {
            log.error("Error processing uploaded file: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("success", false, "message", "文件处理失败"));
        }
    }

    /**
     * 阅读页面
     */
    @GetMapping("/read/{novelId}")
    public String readNovel(@PathVariable Long novelId, Model model) {
        // 这里应该从数据库获取小说信息
        // 简化处理，使用模拟数据
        Novel novel = new Novel();
        novel.setId(novelId);
        novel.setTitle("示例小说");
        novel.setAuthor("示例作者");
        
        Chapter chapter = new Chapter();
        chapter.setTitle("第一章");
        chapter.setContent("这是一个示例章节内容。主角说：\"你好，世界！\"配角回答：\"你好！\"");
        
        Character character1 = new Character();
        character1.setName("主角");
        character1.setVoiceCharacteristics("年轻、充满活力");
        
        Character character2 = new Character();
        character2.setName("配角");
        character2.setVoiceCharacteristics("成熟、稳重");
        
        List<Character> characters = List.of(character1, character2);
        List<Dialogue> dialogues = dialogueExtractionService.extractDialogues(chapter, characters);
        
        model.addAttribute("novel", novel);
        model.addAttribute("chapter", chapter);
        model.addAttribute("dialogues", dialogues);
        model.addAttribute("characters", characters);
        
        return "read";
    }

    /**
     * 获取语音文件
     */
    @GetMapping("/voice/{dialogueId}")
    public ResponseEntity<byte[]> getVoiceFile(@PathVariable Long dialogueId) {
        try {
            // 这里应该从数据库获取对话信息
            // 简化处理，返回模拟音频数据
            byte[] audioData = voiceGenerationService.getVoiceFile("voices/sample.mp3");
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("audio/mpeg"));
            headers.setContentLength(audioData.length);
            
            return new ResponseEntity<>(audioData, headers, HttpStatus.OK);
            
        } catch (Exception e) {
            log.error("Error getting voice file: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 重新生成语音
     */
    @PostMapping("/regenerate-voice/{dialogueId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> regenerateVoice(@PathVariable Long dialogueId) {
        try {
            // 这里应该从数据库获取对话信息
            // 简化处理
            Dialogue dialogue = new Dialogue();
            dialogue.setId(dialogueId);
            dialogue.setContent("示例对话内容");
            
            Character character = new Character();
            character.setName("主角");
            character.setVoiceCharacteristics("年轻、充满活力");
            dialogue.setCharacter(character);
            
            String voicePath = voiceGenerationService.generateVoiceForDialogue(dialogue);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "语音重新生成成功",
                "voicePath", voicePath
            ));
            
        } catch (Exception e) {
            log.error("Error regenerating voice: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("success", false, "message", "语音生成失败"));
        }
    }

    /**
     * 分析角色频率
     */
    @GetMapping("/analyze/{chapterId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> analyzeCharacters(@PathVariable Long chapterId) {
        try {
            // 这里应该从数据库获取章节信息
            Chapter chapter = new Chapter();
            chapter.setContent("示例内容，包含多个角色对话。");
            
            Character character1 = new Character();
            character1.setName("主角");
            
            Character character2 = new Character();
            character2.setName("配角");
            
            List<Character> characters = List.of(character1, character2);
            Map<Character, Integer> frequency = dialogueExtractionService.analyzeCharacterFrequency(chapter, characters);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "frequency", frequency
            ));
            
        } catch (Exception e) {
            log.error("Error analyzing characters: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("success", false, "message", "分析失败"));
        }
    }
} 