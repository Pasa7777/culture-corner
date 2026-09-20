package com.culturecorner;
// Fabric
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.minecraft.client.Minecraft;

// Json
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.GsonBuilder;

// Работа с файлами
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.FileReader;

// Интернет
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;


public class Main implements ModInitializer {

    public static final String MOD_ID = "culture_corner";  // без пробелов
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Мод Culture Corner загружается...");
        loadConfig();
    }
     public static final String DefaultPrompt = "РОЛЬ: Ты — потомственный аристократ, граф или князь, получивший блестящее образование в лучших пансионах Европы. Ты живёшь в Российской империи в эпоху Александра II или Николая II. Твоя речь — эталон изысканности, вежливости и высокого стиля. ЗАДАЧА: Твоя миссия — облагораживать речь. Любое предложение, которое я тебе пришлю (современный сленг, грубость, просторечие или технический текст), ты должен преобразовать в изысканный оборот, достойный пера Льва Толстого или Антона Чехова. ПРАВИЛА ТРАНСФОРМАЦИИ: 1. Лексика: Заменяй современные слова на архаизмы и историзмы. Избегай англицизмов (кроме тех, что были в моде в то время, например, \"сэр\" или \"о'кей\", но лучше использовать русские аналоги). 2. Синтаксис: Используй сложные предложения с причастными и деепричастными оборотами. Добавляй вводные слова (\"сударь\", \"милостивый государь\", \"осмелюсь доложить\", \"извольте\", \"весьма\", \"крайне\"). 3. Обращения: В каждом ответе используй уважительное обращение ко мне («Сударь», «Милостивая государыня», «Ваше благородие»), даже если в исходном тексте его не было. ИСКЛЮЧЕНИЯ (КРИТИЧЕСКИ ВАЖНО): 1. Если присланный текст представляет собой бессвязный набор букв, цифр, бессмыслицу или не имеет грамматической структуры — НЕ ПЫТАЙСЯ его переводить. Скопируй его дословно (как есть). 2. Если текст уже написан высоким штилем или является цитатой классика — оставь его без изменений (или с минимальной правкой для связи с предыдущим диалогом). СТИЛИСТИЧЕСКИЙ ПРИЕМ: Разрешается (но не обязательно) использование букв дореволюционной орфографии: Ѣ (ять), Ѳ (фита), І (и десятеричное), а также твёрдый знак (ъ) на конце слов. Однако, чтобы сохранить читаемость, можешь использовать современную орфографию, но с \"устаревшим\" синтаксисом. Если используешь старые буквы — делай это последовательно. ФОРМАТ ВЫВОДА: Только преобразованное предложение. Без пояснений, без анализа, без перевода на современный язык. Только твой ответ. ПРИМЕРЫ ОБРАБОТКИ (для понимания): Вход: \"Эта тачка едет быстро.\" Выход: \"Осмелюсь заметить, сударь, экипаж сей двигается с весьма внушительной скоростью.\" Вход: \"Привет, как дела?\" Выход: \"Позвольте приветствовать вас, милостивый государь. Как изволите поживать?\" Вход: \"lorem ipsum 123\" Выход: \"lorem ipsum 123\" Вход: \"Я хочу купить хлеб в магазине.\" Выход: \"Желание моё, сударь, простирается до приобретения хлеба в местной лавке.\" ТЕКСТ ДЛЯ ОБРАБОТКИ: ";
     public static final String DefaultModel = "openrouter/free";       
    /**
     * Создаёт JSON-файл в папке config с настройками.
     */
    public static void createJsonFile() {
        // 1. Создаём объект с данными
        Data parameters = new Data(
            "Enter Your Api key Here",
            DefaultPrompt, 
            DefaultModel
        );

        // 2. Настраиваем Gson с красивым форматированием
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        // 3. Определяем пути
        Path gameDir = FabricLoader.getInstance().getGameDir();
        Path configDir = gameDir.resolve("config");
        Path jsonFile = configDir.resolve("Culture_corner.json");

        // 4. Запись в файл
        try {
            // Создаём папку config, если её нет
            if (!Files.exists(configDir)) {
                Files.createDirectories(configDir);
                LOGGER.info("Папка config создана");
            }

            // Пишем JSON в файл
            try (FileWriter writer = new FileWriter(jsonFile.toFile())) {
                gson.toJson(parameters, writer);
            }

            // LOGGER.info("✅ JSON файл успешно создан: {}", jsonFile);
        } catch (IOException e) {
            LOGGER.error("Error for creating JSON file: ", e);
        }
    }

    
    public static class Data {
        private String apiKey;
        private String prompt;
        private String model;
        // Конструктор без параметров нужен для десериализации (если понадобится)
        public Data() {}

        // Конструктор с параметрами для удобства создания
        public Data(String apiKey, String prompt, String model) {
            this.apiKey = apiKey;
            this.prompt = prompt;
            this.model = model;
        }

        // Геттеры (и сеттеры, если нужно)
        public String getApiKey() { return apiKey; }
        public void setApiKey(String apiKey) { this.apiKey = apiKey; }

        public String getPrompt() { return prompt; }
        public void setPrompt(String prompt) { this.prompt = prompt; }
        
        public String getModel() { return model; }
        public void SetModel(String model) { this.model = model;}
    }
    
    public static Data loadConfig() {
        Path gameDir = FabricLoader.getInstance().getGameDir();
        Path configDir = gameDir.resolve("config");
        Path jsonFile = configDir.resolve("Culture_corner.json");

        if (!Files.exists(jsonFile)) {
            createJsonFile();
        }

        try (FileReader reader = new FileReader(jsonFile.toFile())) {
            Gson gson = new Gson();
            Data data = gson.fromJson(reader, Data.class);
            if (data == null) {
                LOGGER.warn("Файл конфигурации пуст, используем значения по умолчанию.");
                return new Data("Enter Your Api key Here", DefaultPrompt, DefaultModel);
            }
            return data;
        } catch (IOException e) {
            LOGGER.error("Ошибка чтения конфигурационного файла: ", e);
            return new Data("Enter Your Api key Here", DefaultPrompt, DefaultModel);
        }
}
     /**
     * Отправляет запрос к OpenRouter и возвращает ответ.
     */
    public static String sendRequestToOpenRouter(String userMessage) {
    Data config = loadConfig();
    String apiKey = config.getApiKey();
    String prompt = config.getPrompt();
    String model = config.getModel();
    try {
        URL url = new URL("https://openrouter.ai/api/v1/chat/completions");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Authorization", "Bearer " + apiKey);
        conn.setRequestProperty("HTTP-Referer", "http://localhost");
        conn.setRequestProperty("X-Title", "Culture Corner Mod");
        conn.setDoOutput(true);
        conn.setConnectTimeout(5000);
        conn.setReadTimeout(10000);

        Gson gson = new Gson();
        JsonObject body = new JsonObject();
        body.addProperty("model", model);
        Message[] messages = {
                new Message("system", prompt),
                new Message("user", userMessage)
        };
        body.add("messages", gson.toJsonTree(messages));
        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = body.toString().getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        int status = conn.getResponseCode();
        if (status != 200) {
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8))) {
                StringBuilder error = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) error.append(line);
                Minecraft minecraft = Minecraft.getInstance();
                minecraft.player.connection.sendChat(" " + "Ошибка API: " + status + " - " + error.toString());
                // return "Ошибка API: " + status + " - " + error.toString();
            }
        }

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) response.append(line);
            String jsonResponse = response.toString();

            JsonObject root = JsonParser.parseString(jsonResponse).getAsJsonObject();
            String content = root.getAsJsonArray("choices")
                    .get(0).getAsJsonObject()
                    .getAsJsonObject("message")
                    .get("content").getAsString();

            // Отправляем ответ в чат (в главном потоке)
            Minecraft minecraft = Minecraft.getInstance();
            if (minecraft != null) {
                minecraft.execute(() -> {
                    if (minecraft.player != null && minecraft.player.connection != null) {
                        minecraft.player.connection.sendChat(" " + content);
                    }
                });
            }
            return "";
        }
    } catch (Exception e) { 
        e.printStackTrace();
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.player.connection.sendChat(" " + "Ошибка ИИ: " + e.getMessage());
    }   
    return "";
}

    // Вспомогательный класс для сообщений
    private static class Message {
        String role;
        String content;
        Message(String role, String content) {
            this.role = role;
            this.content = content;
        }
    }
}

