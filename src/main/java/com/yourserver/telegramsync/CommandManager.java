package com.yourserver.telegramsync;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandManager implements CommandExecutor {
    
    private final TelegramSyncPlugin plugin;
    private final ConfigManager configManager;
    
    public CommandManager(TelegramSyncPlugin plugin) {
        this.plugin = plugin;
        this.configManager = plugin.getConfigManager();
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (command.getName().toLowerCase()) {
            case "telegramsync":
            case "tsync":
            case "tg":
                return handleTelegramSyncCommand(sender, args);
            case "tgbroadcast":
                return handleBroadcastCommand(sender, args);
            default:
                sender.sendMessage(ChatColor.RED + "Неизвестная команда");
        }
        return true;
    }
    
    private boolean handleTelegramSyncCommand(CommandSender sender, String[] args) {
        if (args.length == 0) {
            showHelp(sender);
            return true;
        }
        
        switch (args[0].toLowerCase()) {
            case "reload":
                return handleReload(sender);
            case "status":
                return handleStatus(sender);
            case "test":
                return handleTest(sender);
            case "broadcast":
                return handleBroadcast(sender, args);
            case "help":
            default:
                showHelp(sender);
        }
        
        return true;
    }
    
    private boolean handleReload(CommandSender sender) {
        if (!sender.hasPermission("telegramsync.reload")) {
            sender.sendMessage(configManager.getMessage("system.no-permission"));
            return true;
        }
        
        plugin.reloadConfiguration();
        sender.sendMessage(configManager.getMessage("system.reload-success"));
        return true;
    }
    
    private boolean handleStatus(CommandSender sender) {
        String status = plugin.getStatus();
        sender.sendMessage(ChatColor.GOLD + "📊 " + ChatColor.YELLOW + "Статус TelegramSync:");
        sender.sendMessage(status);
        return true;
    }
    
    private boolean handleTest(CommandSender sender) {
        if (!sender.hasPermission("telegramsync.admin")) {
            sender.sendMessage(configManager.getMessage("system.no-permission"));
            return true;
        }
        
        if (plugin.isBotConnected()) {
            plugin.sendTelegramMessage("🧪 Тестовое сообщение от " + sender.getName());
            sender.sendMessage(configManager.getMessage("success.test-success"));
        } else {
            sender.sendMessage(configManager.getMessage("bot.connection-failed"));
        }
        return true;
    }
    
    private boolean handleBroadcast(CommandSender sender, String[] args) {
        if (!sender.hasPermission("telegramsync.broadcast")) {
            sender.sendMessage(configManager.getMessage("system.no-permission"));
            return true;
        }
        
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Используйте: /telegramsync broadcast <сообщение>");
            return true;
        }
        
        StringBuilder message = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            message.append(args[i]).append(" ");
        }
        
        plugin.sendTelegramMessage("📢 " + sender.getName() + ": " + message.toString());
        sender.sendMessage(configManager.getMessage("success.broadcast-sent"));
        return true;
    }
    
    private boolean handleBroadcastCommand(CommandSender sender, String[] args) {
        if (!sender.hasPermission("telegramsync.broadcast")) {
            sender.sendMessage(configManager.getMessage("system.no-permission"));
            return true;
        }
        
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Используйте: /tgbroadcast <сообщение>");
            return true;
        }
        
        StringBuilder message = new StringBuilder();
        for (String arg : args) {
            message.append(arg).append(" ");
        }
        
        String broadcastMessage = configManager.getMessage("formats.system-message")
            .replace("<message>", message.toString());
        
        plugin.sendTelegramMessage(broadcastMessage);
        sender.sendMessage(configManager.getMessage("success.broadcast-sent"));
        return true;
    }
    
    private void showHelp(CommandSender sender) {
        sender.sendMessage(configManager.getMessage("commands.help.title"));
        sender.sendMessage(configManager.getMessage("commands.help.reload"));
        sender.sendMessage(configManager.getMessage("commands.help.status"));
        sender.sendMessage(configManager.getMessage("commands.help.test"));
        sender.sendMessage(configManager.getMessage("commands.help.broadcast"));
        sender.sendMessage(configManager.getMessage("commands.help.send"));
        sender.sendMessage(configManager.getMessage("commands.help.footer"));
    }
}
