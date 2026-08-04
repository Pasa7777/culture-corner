package com.culturecorner.clientmod;

import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.Minecraft;
import com.culturecorner.Main;

public class culturecorner_main implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        
        // 1. Регистрируем обработчик сообщений
        ClientSendMessageEvents.MODIFY_CHAT.register((message) -> {
            if (message == null || message.isEmpty()) {
                return message;
            }
            if (message.startsWith(" ")) {
                return message; // не обрабатываем
            }
            char firstSymbol = message.charAt(0);
            if (firstSymbol == '\\') {
                return message; // Команды игнорируем
            }
            if (firstSymbol == '.' || firstSymbol == '/') {
                return message; // Команды игнорируем
            }

            // 2. Создаем отдельный поток
            Thread apiResponseThread = new Thread(() -> {
                // Вызываем функцию отправки (укажите имя класса, если перенесли её в другой файл)
                Main.sendRequestToOpenRouter(message);
            }); 
            
            // 3. Запускаем поток
            apiResponseThread.start();

            // 4. Возвращаем пустую строку вместо оригинального текста игрока
            return ""; 
        });
        
    }
} 
