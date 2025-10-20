package com.yourserver.telegramsync;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.List;

public class ConfigManager {
    
    private final JavaPlugin plugin;
    private FileConfiguration config;
    private FileConfiguration messages;
    private File configFile;
    private File messagesFile;
    
    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        loadConfigs();
    }
    
    private void loadConfigs() {
        // Создаем файлы конфигурации, если их нет
        plugin.saveDefaultConfig();
        config = plugin.getConfig();
        
        messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }
        messages = YamlConfiguration.loadConfiguration(messagesFile);
    }
    
    public void reloadConfigs() {
        plugin.reloadConfig();
        config = plugin.getConfig();
        messages = YamlConfiguration.loadConfiguration(messagesFile);
    }
    
    public String getMessage(String path) {
        String message = messages.getString(path, "&cСообщение не найдено: " + path);
        return ChatColor.translateAlternateColorCodes('&', message);
    }
    
    public String getMessage(String path, String defaultValue) {
        String message = messages.getString(path, defaultValue);
        return ChatColor.translateAlternateColorCodes('&', message);
    }
    
    // Геттеры для конфигурации
    public String getBotToken() {
        return config.getString("telegram.bot-token");
    }
    
    public String getChatId() {
        return config.getString("telegram.chat-id");
    }
    
    public boolean isTelegramEnabled() {
        return config.getBoolean("telegram.enabled", true);
    }
    
    public boolean isMinecraftToTelegramSyncEnabled() {
        return config.getBoolean("sync.minecraft-to-telegram", true);
    }
    
    public boolean isTelegramToMinecraftSyncEnabled() {
        return config.getBoolean("sync.telegram-to-minecraft", true);
    }
    
    public List<String> getAllowedPlugins() {
        return config.getStringList("filter.allowed-plugins");
    }
    
    public List<String> getIgnoredPlugins() {
        return config.getStringList("filter.ignored-plugins");
    }
    
    public int getMaxMessageLength() {
        return config.getInt("sync.max-message-length", 400);
    }
    
    public long getMessageDelay() {
        return config.getLong("telegram.message-delay", 1000);
    }
}
