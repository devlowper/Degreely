package com.degreely.app.controller;

import com.degreely.app.model.ChatMessage;
import com.degreely.app.model.ChatSession;
import com.degreely.app.model.StudentProfile;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.time.Duration;
import java.util.Base64;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GeminiService {

    private static GeminiService instance;
    private final HttpClient httpClient;
    private final Gson gson = new Gson();
    private final File configFile;

    private String apiKey = "";
    private String selectedModel = "gemini-2.5-flash";

    private static final String SYSTEM_PROMPT =
            "You are Degreely, an elite, highly knowledgeable AI Study-Abroad Counselor specialized in higher education admissions worldwide (US, UK, Canada, Germany, Australia, Europe, Asia) for ambitious students from Bangladesh and South Asia.\n\n" +
            "CRITICAL RESPONSE STYLE GUIDELINES (CHATGPT-STYLE IN-DEPTH PRESENTATION):\n" +
            "1. IN-DEPTH, DETAILED & COMPREHENSIVE: Provide thorough, deeply researched, multi-faceted answers like ChatGPT / Claude. Never give lazy, one-line, or bare-bones template replies. Address the student's inquiry with extensive context, realistic nuance, and complete explanations.\n" +
            "2. STRUCTURED FORMATTING: Use clean, professional Markdown with clear section headers (###), bold emphasis, organized bullet points, and numbered action steps.\n" +
            "3. ESSENTIAL COUNSELING DIMENSIONS: When advising on universities, majors, or study destinations, thoroughly cover:\n" +
            "   - Academic Requirements & Cutoffs: Minimum GPA, degree prerequisites, rigorous cutoffs, grading scale equivalencies.\n" +
            "   - English Proficiency & Standardized Tests: IELTS, TOEFL, Duolingo bands, GRE/GMAT requirements, waivers.\n" +
            "   - Tuition Fees & Real Financial Cost: Exact annual tuition, comprehensive living expenses (rent, utilities, groceries, transport), health insurance, visa deposits.\n" +
            "   - Scholarships & Funding: Merit scholarships, need-based aid, university fellowships, external scholarships (DAAD, Fulbright, Chevening, Commonwealth), and on-campus work eligibility.\n" +
            "   - Application Deadlines & Intakes: Fall/Winter/Spring deadlines, priority rounds, document requirements (SOP, LORs, CV, transcripts).\n" +
            "   - Post-Study Work Visas & Career Pathways: Post-graduation work permit (OPT/STEM OPT, UK Graduate Route, Canada PGWP, Germany 18-month visa), local tech & corporate job market prospects.\n" +
            "   - Strategic Counselor Advice: Concrete recommendations tailored to maximize the student's admission chances.\n" +
            "4. LANGUAGE FLEXIBILITY: If the student writes in English, reply in articulate, natural English. If in Bengali (বাংলা), reply in natural Bengali. If in Banglish, reply accordingly.\n" +
            "5. COMPARISONS & TABLES: When comparing universities or programs, write an in-depth analytical breakdown comparing strengths, campus culture, ROI, and requirements. Then, at the very end of your detailed reply, append a structured JSON block inside a ```uni-table fenced block so Degreely can render an interactive comparison table beneath your detailed advice.\n\n" +
            "TABLE SPECIFICATION (Append at the end of comparison responses):\n" +
            "```uni-table\n" +
            "{\n" +
            "  \"title\": \"Comparison Title\",\n" +
            "  \"currency\": \"USD\",\n" +
            "  \"universities\": [\n" +
            "    {\n" +
            "      \"name\": \"University Name\",\n" +
            "      \"country\": \"Country\",\n" +
            "      \"program\": \"Program Name\",\n" +
            "      \"tuition_per_year\": \"$XX,XXX / yr\",\n" +
            "      \"living_cost_per_month\": \"$X,XXX / mo\",\n" +
            "      \"min_gpa\": \"X.X / 4.0\",\n" +
            "      \"english_requirement\": \"IELTS X.X / TOEFL XX\",\n" +
            "      \"other_tests\": \"GRE/GMAT info\",\n" +
            "      \"documents\": [\"Transcripts\", \"CV\", \"SOP\", \"2 LORs\"],\n" +
            "      \"application_deadline\": \"Month Day\",\n" +
            "      \"intake\": \"Intake term\",\n" +
            "      \"application_fee\": \"$XX\",\n" +
            "      \"scholarships\": \"Scholarship names\",\n" +
            "      \"work_rights\": \"Post-study visa details\",\n" +
            "      \"source_url\": \"https://...\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"notes\": \"Verify exact requirements on official university portal\"\n" +
            "}\n" +
            "```\n";

    private GeminiService() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .build();

        String userHome = System.getProperty("user.home");
        File dir = new File(userHome, ".degreely");
        if (!dir.exists()) dir.mkdirs();
        this.configFile = new File(dir, "config.json");

        loadConfig();
    }

    public static synchronized GeminiService getInstance() {
        if (instance == null) {
            instance = new GeminiService();
        }
        return instance;
    }

    public String getApiKey() {
        if (apiKey != null && !apiKey.isBlank()) {
            return apiKey;
        }
        String envKey = System.getenv("GEMINI_API_KEY");
        return envKey != null ? envKey.trim() : "";
    }

    public void setApiKey(String key) {
        this.apiKey = key != null ? key.trim() : "";
        saveConfig();
    }

    public String getSelectedModel() {
        return selectedModel;
    }

    public void setSelectedModel(String model) {
        this.selectedModel = model;
        saveConfig();
    }

    public boolean hasApiKey() {
        String key = getApiKey();
        return key != null && !key.isBlank();
    }

    /**
     * Sends conversation to Gemini API and returns the assistant's reply.
     */
    public ChatMessage sendMessage(ChatSession session, String userPrompt, List<File> attachedFiles) throws Exception {
        String key = getApiKey();
        if (key.isBlank()) {
            throw new IllegalStateException("Gemini API Key is not set. Please enter your API key in Gemini Settings or set the GEMINI_API_KEY environment variable.");
        }

        // 1. Build Payload
        JsonObject root = new JsonObject();

        // System Instruction
        JsonObject systemInst = new JsonObject();
        JsonArray sysParts = new JsonArray();
        JsonObject sysTextPart = new JsonObject();

        StudentProfile profile = StudentProfileService.getInstance().getProfile();
        String fullSystemPrompt = SYSTEM_PROMPT + "\n" + profile.toContextPrompt();
        sysTextPart.addProperty("text", fullSystemPrompt);
        sysParts.add(sysTextPart);
        systemInst.add("parts", sysParts);
        root.add("system_instruction", systemInst);

        // Contents (Conversation History + Current Prompt)
        JsonArray contents = new JsonArray();

        // Include previous messages (up to last 10 messages for context)
        List<ChatMessage> history = session.getMessages();
        int startIndex = Math.max(0, history.size() - 8);
        for (int i = startIndex; i < history.size(); i++) {
            ChatMessage msg = history.get(i);
            JsonObject turn = new JsonObject();
            turn.addProperty("role", "assistant".equals(msg.getRole()) ? "model" : "user");
            JsonArray parts = new JsonArray();
            JsonObject p = new JsonObject();
            p.addProperty("text", msg.getContent());
            parts.add(p);
            turn.add("parts", parts);
            contents.add(turn);
        }

        // Current User Message
        JsonObject currentTurn = new JsonObject();
        currentTurn.addProperty("role", "user");
        JsonArray currentParts = new JsonArray();

        // Attach files if any
        if (attachedFiles != null && !attachedFiles.isEmpty()) {
            for (File file : attachedFiles) {
                if (!file.exists()) continue;
                String mimeType = Files.probeContentType(file.toPath());
                if (mimeType == null) mimeType = "application/octet-stream";

                if (mimeType.startsWith("text/") || file.getName().endsWith(".txt") || file.getName().endsWith(".csv")) {
                    String fileText = Files.readString(file.toPath());
                    JsonObject filePart = new JsonObject();
                    filePart.addProperty("text", "[Uploaded File: " + file.getName() + "]\n" + fileText);
                    currentParts.add(filePart);
                } else {
                    // Send as base64 inline data
                    byte[] bytes = Files.readAllBytes(file.toPath());
                    String base64 = Base64.getEncoder().encodeToString(bytes);
                    JsonObject inlinePart = new JsonObject();
                    JsonObject inlineData = new JsonObject();
                    inlineData.addProperty("mime_type", mimeType);
                    inlineData.addProperty("data", base64);
                    inlinePart.add("inline_data", inlineData);
                    currentParts.add(inlinePart);
                }
            }
        }

        JsonObject userTextPart = new JsonObject();
        userTextPart.addProperty("text", userPrompt);
        currentParts.add(userTextPart);

        currentTurn.add("parts", currentParts);
        contents.add(currentTurn);

        root.add("contents", contents);

        // Generation Config with generous token limit for deep, ChatGPT-style detailed answers
        JsonObject genConfig = new JsonObject();
        genConfig.addProperty("temperature", 0.7);
        genConfig.addProperty("maxOutputTokens", 4096);
        root.add("generationConfig", genConfig);

        String jsonPayload = gson.toJson(root);

        // 2. Send HTTP Request
        String url = "https://generativelanguage.googleapis.com/v1beta/models/" + selectedModel + ":generateContent?key=" + key;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .timeout(Duration.ofSeconds(60))
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            String errorMsg = parseErrorMessage(response.body(), response.statusCode());
            throw new RuntimeException(errorMsg);
        }

        // 3. Parse Response
        JsonObject respJson = JsonParser.parseString(response.body()).getAsJsonObject();
        JsonArray candidates = respJson.getAsJsonArray("candidates");
        if (candidates == null || candidates.isEmpty()) {
            throw new RuntimeException("No response received from Gemini.");
        }

        JsonObject firstCandidate = candidates.get(0).getAsJsonObject();
        JsonObject contentObj = firstCandidate.getAsJsonObject("content");
        JsonArray partsArr = contentObj.getAsJsonArray("parts");
        if (partsArr == null || partsArr.isEmpty()) {
            throw new RuntimeException("Empty response body from model.");
        }

        String replyText = partsArr.get(0).getAsJsonObject().get("text").getAsString();

        // 4. Extract Uni-Table JSON if present
        ChatMessage assistantMessage = new ChatMessage("assistant", replyText);
        String tableJson = extractUniTableJson(replyText);
        if (tableJson != null && !tableJson.isBlank()) {
            assistantMessage.setUniTableJson(tableJson);
        }

        return assistantMessage;
    }

    /**
     * Auto-generates a short title (4-6 words) for a new chat session.
     */
    public String generateChatTitle(String userPrompt) {
        try {
            String key = getApiKey();
            if (key.isBlank()) return "Study Abroad Consultation";

            JsonObject root = new JsonObject();
            JsonArray contents = new JsonArray();
            JsonObject turn = new JsonObject();
            turn.addProperty("role", "user");
            JsonArray parts = new JsonArray();
            JsonObject p = new JsonObject();
            p.addProperty("text", "Summarize this user query into a concise 4 to 6 word chat title without quotation marks: \"" + userPrompt + "\"");
            parts.add(p);
            turn.add("parts", parts);
            contents.add(turn);
            root.add("contents", contents);

            String url = "https://generativelanguage.googleapis.com/v1beta/models/" + selectedModel + ":generateContent?key=" + key;
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .timeout(Duration.ofSeconds(10))
                    .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(root)))
                    .build();

            HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
            if (resp.statusCode() == 200) {
                JsonObject json = JsonParser.parseString(resp.body()).getAsJsonObject();
                String title = json.getAsJsonArray("candidates")
                        .get(0).getAsJsonObject()
                        .getAsJsonObject("content")
                        .getAsJsonArray("parts")
                        .get(0).getAsJsonObject()
                        .get("text").getAsString().trim();
                title = title.replaceAll("[\"'\n\r]", "");
                if (title.length() > 40) title = title.substring(0, 40) + "...";
                return title;
            }
        } catch (Exception ignored) {}
        return "Study Abroad Consultation";
    }

    public String extractUniTableJson(String text) {
        if (text == null) return null;

        // Pattern 1: tagged with ```uni-table
        Pattern pUniTable = Pattern.compile("```(?:uni-table)\\s*([\\s\\S]*?)```", Pattern.CASE_INSENSITIVE);
        Matcher m1 = pUniTable.matcher(text);
        if (m1.find()) {
            return m1.group(1).trim();
        }

        // Pattern 2: tagged with ```json that contains "universities" array
        Pattern pJson = Pattern.compile("```(?:json)?\\s*(\\{[\\s\\S]*?\"universities\"[\\s\\S]*?\\})\\s*```", Pattern.CASE_INSENSITIVE);
        Matcher m2 = pJson.matcher(text);
        if (m2.find()) {
            return m2.group(1).trim();
        }

        return null;
    }

    private String parseErrorMessage(String body, int statusCode) {
        try {
            JsonObject json = JsonParser.parseString(body).getAsJsonObject();
            if (json.has("error")) {
                JsonObject err = json.getAsJsonObject("error");
                String msg = err.get("message").getAsString();
                return "Gemini API Error (" + statusCode + "): " + msg;
            }
        } catch (Exception ignored) {}
        return "Gemini API returned HTTP status " + statusCode;
    }

    private void loadConfig() {
        if (!configFile.exists()) return;
        try (Reader reader = new FileReader(configFile)) {
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
            if (json.has("apiKey")) {
                this.apiKey = json.get("apiKey").getAsString();
            }
            if (json.has("model")) {
                this.selectedModel = json.get("model").getAsString();
            }
        } catch (Exception ignored) {}
    }

    private void saveConfig() {
        try (Writer writer = new FileWriter(configFile)) {
            JsonObject json = new JsonObject();
            json.addProperty("apiKey", apiKey);
            json.addProperty("model", selectedModel);
            gson.toJson(json, writer);
        } catch (Exception ignored) {}
    }
}
