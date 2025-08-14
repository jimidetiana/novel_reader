package com.novel.reader.service;

import com.alibaba.dashscope.audio.tts.SpeechSynthesizer;
import com.alibaba.dashscope.audio.tts.SpeechSynthesisParam;
import com.alibaba.dashscope.audio.tts.SpeechSynthesisAudioFormat;

import java.io.*;
import java.nio.ByteBuffer;

public class Main {
    public static void syncAudioDataToFile() {
        SpeechSynthesizer synthesizer = new SpeechSynthesizer();
        SpeechSynthesisParam param = SpeechSynthesisParam.builder()
                // 若没有将API Key配置到环境变量中，需将下面这行代码注释放开，并将apiKey替换为自己的API Key
                .apiKey("sk-3bed6048e8d84ab7886cb82e2f9979ce")
                .model("sambert-zhichu-v1")
                .text("今天天气怎么样")
                .sampleRate(48000)
                .format(SpeechSynthesisAudioFormat.WAV)
                .build();

        File file = new File("output.wav");
        // 提交同步合成任务，获取完整的音频数据
        ByteBuffer audio = synthesizer.call(param);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(audio.array());
            System.out.println("synthesis done!");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        syncAudioDataToFile();
        System.exit(0);
    }
}