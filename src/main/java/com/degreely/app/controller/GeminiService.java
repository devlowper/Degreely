package com.degreely.app.controller;

import com.degreely.app.model.ChatMessage;
import com.degreely.app.model.ChatSession;
import com.degreely.app.model.StudentProfile;
import com.degreely.app.model.UniTableData;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
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
            "You are Degreely, an expert study-abroad counselor for students from Bangladesh and South Asia. " +
            "Help with universities, admissions, costs, deadlines, scholarships, visas, exams (IELTS/TOEFL/GRE/GMAT), and SOP reviews. " +
            "For any comparison or university lookup, output the uni-table JSON block described below. " +
            "Never fabricate deadlines, fees, or requirements; if unsure, say so and cite where to verify. " +
            "Mention the academic intake year for all figures. Be concise, structured, friendly, and encouraging. " +
            "Match the user's language: if they write English, answer in English; if Bangla (বাংলা), answer in Bangla; if Banglish, answer in Banglish. " +
            "You are not a lawyer; for visa/legal decisions advise confirming with official embassy sources.\n\n" +
            "TABLE SPECIFICATION:\n" +
            "Whenever the user asks to compare universities, programs, or countries, or asks about specific university requirements, " +
            "reply with a short intro sentence plus a structured JSON block inside a fenced code block tagged ```uni-table or ```json with this exact structure:\n" +
            "{\n" +
            "  \"title\": \"MSc Computer Science: Germany vs Canada\",\n" +
            "  \"currency\": \"USD\",\n" +
            "  \"universities\": [\n" +
            "    {\n" +
            "      \"name\": \"Technical University of Munich\",\n" +
            "      \"country\": \"Germany\",\n" +
            "      \"program\": \"MSc Computer Science\",\n" +
            "      \"tuition_per_year\": \"$0 - $3,500\",\n" +
            "      \"living_cost_per_month\": \"$1,000 - $1,300\",\n" +
            "      \"min_gpa\": \"3.2 / 4.0\",\n" +
            "      \"english_requirement\": \"IELTS 6.5 / TOEFL 88\",\n" +
            "      \"other_tests\": \"GRE optional\",\n" +
            "      \"documents\": [\"Transcripts\", \"CV\", \"SOP\", \"2 LORs\"],\n" +
            "      \"application_deadline\": \"May 31\",\n" +
            "      \"intake\": \"Winter\",\n" +
            "      \"application_fee\": \"$0\",\n" +
            "      \"scholarships\": \"DAAD Scholarship\",\n" +
            "      \"work_rights\": \"18-month Job Seeking Visa\",\n" +
            "      \"source_url\": \"https://www.tum.de\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"notes\": \"Verify exact deadlines on official portal\"\n" +
            "}\n";

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
            throw new IllegalStateException("Gemini API Key is not set. Please enter your API key in Settings (Gear icon) or set the GEMINI_API_KEY environment variable.");
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

        // Generation Config
        JsonObject genConfig = new JsonObject();
        genConfig.addProperty("temperature", 0.7);
        genConfig.addProperty("maxOutputTokens", 2048);
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
            if (key.isBlank()) return "Study Abroad Inquiry";

            JsonObject root = new JsonObject();
            JsonArray contents = new JsonArray();
            JsonObject turn = new JsonObject();
            turn.addProperty("role", "user");
            JsonArray parts = new JsonArray();
            JsonObject p = new JsonObject();
            p.addProperty("text", "Summarize this user query into a 4 to 6 word chat title without punctuation: \"" + userPrompt + "\"");
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
        return "Study Abroad Inquiry";
    }

    public String extractUniTableJson(String text) {
        if (text == null) return null;
        // Search for ```uni-table ... ``` or ```json ... ``` with "universities"
        Pattern pattern = Pattern.compile("```(?:uni-table|json)?\\s*(\\{[\\s\\S]*?\"universities\"[\\s\\S]*?\\})\\s*```", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return null;
    }

    private String parseErrorMessage(String body, int statusCode) {
        try {
            JsonObject obj = JsonParser.parseString(body).getAsJsonObject();
            if (obj.has("error")) {
                JsonObject err = obj.getAsJsonObject("error");
                String message = err.get("message").getAsString();
                return "Gemini API Error (" + statusCode + "): " + message;
            }
        } catch (Exception ignored) {}
        if (statusCode == 400) return "Bad request or invalid model (" + selectedModel + "). Check your prompt or model name.";
        if (statusCode == 401 || statusCode == 403) return "Invalid or unauthorized API Key. Please verify your Gemini API Key in Settings.";
        if (statusCode == 429) return "Quota or rate limit reached. Please wait a minute or use a different API key.";
        return "Gemini API request failed with status code " + statusCode;
    }

    private void loadConfig() {
        if (!configFile.exists()) return;
        try (Reader reader = new FileReader(configFile)) {
            JsonObject obj = JsonParser.parseString(reader.toString()).getAsJsonObject();
            if (obj.has("apiKey")) this.apiKey = obj.get("apiKey").getAsString();
            if (obj.has("model")) this.selectedModel = obj.get("model").getAsString();
        } catch (Exception ignored) {}
    }

    private void saveConfig() {
        try (Writer writer = new FileWriter(configFile)) {
            JsonObject obj = new JsonObject();
            obj.addProperty("apiKey", this.apiKey);
            obj.addProperty("model", this.selectedModel);
            gson.toJson(obj, writer);
        } catch (Exception ignored) {}
    }
}
