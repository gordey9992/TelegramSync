package com.yourserver.telegramsync;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TelegramSyncPlugin extends JavaPlugin implements Listener {

    private static TelegramSyncPlugin instance;
    private ConfigManager configManager;
    private TelegramBot bot;
    private boolean botConnected;
    private long messagesSent;
    private long messagesReceived;
    private final Set<String> blockedWords;
    private final Logger logger;

    // Красивое приветствие в консоли
    private void printWelcomeMessage() {
        getLogger().info("╔══════════════════════════════════════════════════════════════════════════════╗");
        getLogger().info("║                                                                              ║");
        getLogger().info("║  ████████╗███████╗██╗     ███████╗ ██████╗ ██████╗  █████╗ ███╗   ███╗       ║");
        getLogger().info("║  ╚══██╔══╝██╔════╝██║     ██╔════╝██╔════╝ ██╔══██╗██╔══██╗████╗ ████║       ║");
        getLogger().info("║     ██║   █████╗  ██║     █████╗  ██║  ███╗██████╔╝███████║██╔████╔██║       ║");
        getLogger().info("║     ██║   ██╔══╝  ██║     ██╔══╝  ██║   ██║██╔══██╗██╔══██║██║╚██╔╝██║       ║");
        getLogger().info("║     ██║   ███████╗███████╗███████╗╚██████╔╝██║  ██║██║  ██║██║ ╚═╝ ██║       ║");
        getLogger().info("║     ╚═╝   ╚══════╝╚══════╝╚══════╝ ╚═════╝ ╚═╝  ╚═╝╚═╝  ╚═╝╚═╝     ╚═╝       ║");
        getLogger().info("║                                                                              ║");
        getLogger().info("║              🎮 СИНХРОНИЗАЦИЯ MINECRAFT И TELEGRAM 🎮                      ║");
        getLogger().info("║                                                                              ║");
        getLogger().info("║              ✨ Авторы: gordey25690 и DeepSeek ✨                           ║");
        getLogger().info("║              🌟 Версия: 1.0.0 | Minecraft 1.21 🌟                         ║");
        getLogger().info("║              📅 " + new Date().toString() + " 📅                          ║");
        getLogger().info("║                                                                              ║");
        getLogger().info("╚══════════════════════════════════════════════════════════════════════════════╝");
    }

    public TelegramSyncPlugin() {
        this.blockedWords = new HashSet<>();
        this.logger = getLogger();
    }

    @Override
    public void onEnable() {
        instance = this;
        this.configManager = new ConfigManager(this);
        
        // Показываем красивое приветствие
        printWelcomeMessage();
        
        // Регистрируем события
        getServer().getPluginManager().registerEvents(this, this);
        
        // Загружаем блокируемые слова
        loadBlockedWords();
        
        // Инициализируем Telegram бота
        initializeTelegramBot();
        
        // Регистрируем команды
        registerCommands();
        
        // Отправляем уведомление о запуске
        sendTelegramMessage(configManager.getMessage("telegram-texts.server-start"));
        
        getLogger().info("✅ Плагин успешно активирован!");
        Bukkit.broadcastMessage(configManager.getMessage("system.plugin-enabled"));
    }

    @Override
    public void onDisable() {
        // Отправляем уведомление об остановке
        if (botConnected) {
            sendTelegramMessage(configManager.getMessage("telegram-texts.server-stop"));
        }
        
        if (bot != null) {
            bot.removeGetUpdatesListener();
        }
        
        getLogger().info("🔴 Плагин отключен");
        Bukkit.broadcastMessage(configManager.getMessage("system.plugin-disabled"));
    }

    public static TelegramSyncPlugin getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    private void loadBlockedWords() {
        blockedWords.clear();
        List<String> words = getConfig().getStringList("filter.blocked-words");
        blockedWords.addAll(words);
        getLogger().info("✅ Загружено " + blockedWords.size() + " блокируемых слов");
    }

    private void initializeTelegramBot() {
        String botToken = getConfig().getString("telegram.bot-token");
        String chatId = getConfig().getString("telegram.chat-id");
        
        if (botToken == null || botToken.equals("YOUR_BOT_TOKEN_HERE") || 
            chatId == null || chatId.equals("YOUR_CHAT_ID_HERE")) {
            getLogger().warning("❌ Токен бота или Chat ID не настроены в config.yml");
            botConnected = false;
            return;
        }
        
        try {
            bot = new TelegramBot(botToken);
            
            // Тестируем соединение
            SendResponse testResponse = bot.execute(new SendMessage(chatId, "🟢 Сервер Minecraft запущен!"));
            botConnected = testResponse.isOk();
            
            if (botConnected) {
                getLogger().info("✅ Telegram бот успешно подключен!");
                Bukkit.broadcastMessage(configManager.getMessage("bot.connected"));
                
                // Настраиваем слушатель сообщений
                bot.setUpdatesListener(updates -> {
                    for (Update update : updates) {
                        if (update.message() != null && update.message().text() != null) {
                            handleTelegramMessage(update);
                        }
                    }
                    return UpdatesListener.CONFIRMED_UPDATES_ALL;
                });
                
            } else {
                getLogger().warning("❌ Не удалось подключиться к Telegram. Проверьте токен и Chat ID.");
            }
            
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "❌ Ошибка инициализации Telegram бота: ", e);
            botConnected = false;
        }
    }

    private void registerCommands() {
        // Регистрируем команды через plugin.yml
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        if (!botConnected || !getConfig().getBoolean("sync.minecraft-to-telegram", true)) {
            return;
        }
        
        Player player = event.getPlayer();
        String message = event.getMessage();
        
        // Проверяем на блокируемые слова
        if (containsBlockedWords(message)) {
            player.sendMessage(configManager.getMessage("errors.message-blocked"));
            event.setCancelled(true);
            return;
        }
        
        // Форматируем сообщение для Telegram
        String formattedMessage = formatMinecraftToTelegram(player.getName(), message);
        
        // Отправляем в Telegram
        sendTelegramMessage(formattedMessage);
        
        messagesSent++;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!botConnected || !getConfig().getBoolean("auto-messages.player-join", true)) {
            return;
        }
        
        Player player = event.getPlayer();
        String joinMessage = configManager.getMessage("formats.player-join")
            .replace("<player>", player.getName());
        
        sendTelegramMessage(joinMessage);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (!botConnected || !getConfig().getBoolean("auto-messages.player-quit", true)) {
            return;
        }
        
        Player player = event.getPlayer();
        String quitMessage = configManager.getMessage("formats.player-quit")
            .replace("<player>", player.getName());
        
        sendTelegramMessage(quitMessage);
    }

    @EventHandler
    public void onServerLoad(ServerLoadEvent event) {
        // Сервер полностью загружен
        if (botConnected) {
            String statusMessage = configManager.getMessage("telegram-texts.status")
                .replace("<online>", String.valueOf(Bukkit.getOnlinePlayers().size()))
                .replace("<max>", String.valueOf(Bukkit.getMaxPlayers()));
            
            sendTelegramMessage(statusMessage);
        }
    }

    private void handleTelegramMessage(Update update) {
        new BukkitRunnable() {
            @Override
            public void run() {
                String username = update.message().from().firstName();
                if (update.message().from().lastName() != null) {
                    username += " " + update.message().from().lastName();
                }
                
                String message = update.message().text();
                
                // Игнорируем команды бота
                if (message.startsWith("/")) {
                    return;
                }
                
                // Форматируем сообщение для Minecraft
                String formattedMessage = formatTelegramToMinecraft(username, message);
                
                // Отправляем в игровой чат
                Bukkit.broadcastMessage(formattedMessage);
                
                messagesReceived++;
                
                // Логируем получение сообщения
                getLogger().info("📱 Получено сообщение из Telegram от " + username + ": " + message);
            }
        }.runTask(this);
    }

    public void sendTelegramMessage(String message) {
        if (!botConnected || bot == null) {
            return;
        }
        
        String chatId = getConfig().getString("telegram.chat-id");
        if (chatId == null || chatId.isEmpty()) {
            return;
        }
        
        // Проверяем длину сообщения
        int maxLength = getConfig().getInt("sync.max-message-length", 400);
        if (message.length() > maxLength) {
            message = message.substring(0, maxLength - 3) + "...";
        }
        
        try {
            SendResponse response = bot.execute(new SendMessage(chatId, message));
            if (!response.isOk()) {
                getLogger().warning("❌ Ошибка отправки сообщения в Telegram: " + response.description());
            }
        } catch (Exception e) {
            getLogger().log(Level.WARNING, "❌ Исключение при отправке сообщения в Telegram: ", e);
        }
    }

    public void sendBroadcastToMinecraft(String message) {
        String formattedMessage = configManager.getMessage("formats.system")
            .replace("<message>", message);
        Bukkit.broadcastMessage(formattedMessage);
    }

    private String formatMinecraftToTelegram(String playerName, String message) {
        return getConfig().getString("format.minecraft-to-telegram", "🎮 <player>: <message>")
            .replace("<player>", playerName)
            .replace("<message>", message);
    }

    private String formatTelegramToMinecraft(String username, String message) {
        return ChatColor.translateAlternateColorCodes('&',
            getConfig().getString("format.telegram-to-minecraft", "&9[Telegram] &b<username>: &f<message>")
                .replace("<username>", username)
                .replace("<message>", message)
        );
    }

    private boolean containsBlockedWords(String message) {
        String lowerMessage = message.toLowerCase();
        for (String word : blockedWords) {
            if (lowerMessage.contains(word.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    // Методы для команд
    public void reloadConfiguration() {
        reloadConfig();
        configManager.reloadConfigs();
        loadBlockedWords();
        initializeTelegramBot();
    }

    public String getStatus() {
        String status = botConnected ? 
            configManager.getMessage("commands.status.online") : 
            configManager.getMessage("commands.status.offline");
        
        String telegramStatus = botConnected ?
            configManager.getMessage("commands.status.telegram-connected") :
            configManager.getMessage("commands.status.telegram-disconnected");
        
        String messagesInfo = configManager.getMessage("commands.status.messages-sent")
            .replace("<count>", String.valueOf(messagesSent)) + "\n" +
            configManager.getMessage("commands.status.messages-received")
            .replace("<count>", String.valueOf(messagesReceived));
        
        return status + "\n" + telegramStatus + "\n" + messagesInfo;
    }

    public boolean isBotConnected() {
        return botConnected;
    }
}

    private void registerCommands() {
        CommandRegistry.registerCommands(this);
    }
