# AI小说阅读器

一个基于Spring Boot的智能小说阅读器，具备文本对话识别和AI语音生成功能。

## 功能特色

### 🎯 核心功能
- **智能对话识别**: 自动识别小说中的对话内容，支持多种对话格式
- **AI语音生成**: 根据角色特征自动生成符合人物特色的AI语音
- **沉浸式阅读**: 结合文本和语音的沉浸式阅读体验
- **角色管理**: 自动提取和管理小说中的角色信息

### 🔧 技术特性
- **对话识别**: 使用正则表达式和上下文分析识别对话
- **语音合成**: 集成OpenAI TTS API生成个性化语音
- **响应式UI**: 现代化的Web界面，支持移动端
- **实时播放**: 支持单个对话播放和连续播放

## 技术栈

- **后端**: Spring Boot 3.2.0, Spring Data JPA, Spring Web
- **数据库**: H2 Database (开发环境)
- **AI服务**: OpenAI API (GPT + TTS)
- **前端**: Bootstrap 5, Thymeleaf, JavaScript
- **构建工具**: Maven

## 快速开始

### 环境要求
- Java 17+
- Maven 3.6+
- OpenAI API Key

### 安装步骤

1. **克隆项目**
```bash
git clone <repository-url>
cd novel-reader
```

2. **配置OpenAI API**
编辑 `src/main/resources/application.properties` 文件：
```properties
openai.api.key=your-openai-api-key-here
```

3. **编译运行**
```bash
mvn clean install
mvn spring-boot:run
```

4. **访问应用**
打开浏览器访问: http://localhost:8080

### 使用流程

1. **上传小说**: 在首页点击"开始上传小说"，填写小说信息并上传文件
2. **AI处理**: 系统自动识别对话和角色，生成个性化语音
3. **开始阅读**: 在阅读页面享受带有AI语音的沉浸式阅读体验

## 项目结构

```
novel-reader/
├── src/main/java/com/novel/reader/
│   ├── config/           # 配置类
│   ├── controller/       # 控制器
│   ├── entity/          # 实体类
│   └── service/         # 服务类
├── src/main/resources/
│   ├── templates/       # Thymeleaf模板
│   └── application.properties
└── pom.xml
```

## 核心组件

### 实体类 (Entity)
- `Novel`: 小说实体
- `Chapter`: 章节实体
- `Character`: 角色实体
- `Dialogue`: 对话实体

### 服务类 (Service)
- `DialogueExtractionService`: 对话提取服务
- `VoiceGenerationService`: 语音生成服务

### 控制器 (Controller)
- `NovelReaderController`: 主要业务控制器

## API接口

### 小说管理
- `GET /`: 首页
- `GET /upload`: 上传页面
- `POST /upload`: 上传小说
- `GET /read/{novelId}`: 阅读页面

### 语音服务
- `GET /voice/{dialogueId}`: 获取语音文件
- `POST /regenerate-voice/{dialogueId}`: 重新生成语音

### 分析服务
- `GET /analyze/{chapterId}`: 分析角色频率

## 配置说明

### 数据库配置
```properties
spring.datasource.url=jdbc:h2:file:./novel_reader_db
spring.datasource.username=sa
spring.datasource.password=password
```

### 文件上传配置
```properties
spring.servlet.multipart.max-file-size=50MB
spring.servlet.multipart.max-request-size=50MB
```

### 语音输出配置
```properties
voice.output.directory=voices
```

## 开发指南

### 添加新的对话识别模式
在 `DialogueExtractionService` 中修改正则表达式模式：

```java
private static final Pattern DIALOGUE_PATTERN = Pattern.compile(
    "\"([^\"]+)\"|" + // 双引号对话
    "（([^）]+)）|" + // 中文括号对话
    "「([^」]+)」|" + // 中文书名号对话
    "『([^』]+)』" // 中文双书名号对话
);
```

### 自定义角色语音特征
在 `Character` 实体中设置语音参数：

```java
character.setVoiceCharacteristics("年轻、充满活力");
character.setVoiceModel("alloy");
character.setVoiceSpeed(1.0f);
character.setVoicePitch(1.0f);
```

## 部署说明

### 生产环境配置
1. 修改数据库配置为生产数据库
2. 配置OpenAI API密钥
3. 设置语音文件存储路径
4. 配置日志级别

### Docker部署
```dockerfile
FROM openjdk:17-jdk-slim
COPY target/novel-reader-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

## 贡献指南

1. Fork 项目
2. 创建功能分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开 Pull Request

## 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情

## 联系方式

- 项目维护者: [Your Name]
- 邮箱: [your.email@example.com]
- 项目地址: [https://github.com/yourusername/novel-reader]

## 更新日志

### v1.0.0 (2024-01-01)
- 初始版本发布
- 基础对话识别功能
- AI语音生成功能
- 响应式Web界面 