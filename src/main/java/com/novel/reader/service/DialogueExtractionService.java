package com.novel.reader.service;

import com.novel.reader.entity.Character;
import com.novel.reader.entity.Dialogue;
import com.novel.reader.entity.Chapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class DialogueExtractionService {

    // 对话识别模式
    private static final Pattern DIALOGUE_PATTERN = Pattern.compile(
        "\"([^\"]+)\"|" + // 双引号对话
        "（([^）]+)）|" + // 中文括号对话
        "「([^」]+)」|" + // 中文书名号对话
        "『([^』]+)』" // 中文双书名号对话
    );

    // 角色名称识别模式
    private static final Pattern CHARACTER_PATTERN = Pattern.compile(
        "([^：:]+)[：:]\\s*\"([^\"]+)\"|" +
        "([^：:]+)[：:]\\s*（([^）]+)）|" +
        "([^：:]+)[：:]\\s*「([^」]+)」|" +
        "([^：:]+)[：:]\\s*『([^』]+)』"
    );

    /**
     * 从章节内容中提取对话
     */
    public List<Dialogue> extractDialogues(Chapter chapter, List<Character> characters) {
        List<Dialogue> dialogues = new ArrayList<>();
        String content = chapter.getContent();
        
        // 使用角色名称模式识别对话
        Matcher characterMatcher = CHARACTER_PATTERN.matcher(content);
        while (characterMatcher.find()) {
            String characterName = null;
            String dialogueContent = null;
            
            // 提取角色名称和对话内容
            for (int i = 1; i <= characterMatcher.groupCount(); i += 2) {
                if (characterMatcher.group(i) != null && characterMatcher.group(i + 1) != null) {
                    characterName = characterMatcher.group(i).trim();
                    dialogueContent = characterMatcher.group(i + 1).trim();
                    break;
                }
            }
            
            if (characterName != null && dialogueContent != null) {
                Character character = findCharacterByName(characters, characterName);
                if (character != null) {
                    Dialogue dialogue = createDialogue(dialogueContent, character, chapter, 
                        characterMatcher.start(), characterMatcher.end());
                    dialogues.add(dialogue);
                }
            }
        }
        
        // 使用通用对话模式识别未明确角色的对话
        Matcher dialogueMatcher = DIALOGUE_PATTERN.matcher(content);
        while (dialogueMatcher.find()) {
            String dialogueContent = null;
            
            // 提取对话内容
            for (int i = 1; i <= dialogueMatcher.groupCount(); i++) {
                if (dialogueMatcher.group(i) != null) {
                    dialogueContent = dialogueMatcher.group(i).trim();
                    break;
                }
            }
            
            if (dialogueContent != null) {
                // 尝试通过上下文推断角色
                Character character = inferCharacterFromContext(content, dialogueMatcher.start(), characters);
                if (character != null) {
                    Dialogue dialogue = createDialogue(dialogueContent, character, chapter, 
                        dialogueMatcher.start(), dialogueMatcher.end());
                    dialogues.add(dialogue);
                }
            }
        }
        
        return dialogues;
    }

    /**
     * 根据角色名称查找角色
     */
    private Character findCharacterByName(List<Character> characters, String name) {
        return characters.stream()
            .filter(c -> c.getName().equals(name) || 
                        c.getName().contains(name) || 
                        name.contains(c.getName()))
            .findFirst()
            .orElse(null);
    }

    /**
     * 从上下文推断角色
     */
    private Character inferCharacterFromContext(String content, int position, List<Character> characters) {
        // 在对话前后查找角色名称
        int searchStart = Math.max(0, position - 100);
        int searchEnd = Math.min(content.length(), position + 100);
        String context = content.substring(searchStart, searchEnd);
        
        for (Character character : characters) {
            if (context.contains(character.getName())) {
                return character;
            }
        }
        
        return null;
    }

    /**
     * 创建对话对象
     */
    private Dialogue createDialogue(String content, Character character, Chapter chapter, 
                                  int startPosition, int endPosition) {
        Dialogue dialogue = new Dialogue();
        dialogue.setContent(content);
        dialogue.setCharacter(character);
        dialogue.setChapter(chapter);
        dialogue.setStartPosition(startPosition);
        dialogue.setEndPosition(endPosition);
        dialogue.setIsGenerated(false);
        return dialogue;
    }

    /**
     * 分析角色在文本中的出现频率
     */
    public Map<Character, Integer> analyzeCharacterFrequency(Chapter chapter, List<Character> characters) {
        Map<Character, Integer> frequency = new HashMap<>();
        String content = chapter.getContent();
        
        for (Character character : characters) {
            int count = 0;
            int index = 0;
            while ((index = content.indexOf(character.getName(), index)) != -1) {
                count++;
                index += character.getName().length();
            }
            frequency.put(character, count);
        }
        
        return frequency;
    }
} 