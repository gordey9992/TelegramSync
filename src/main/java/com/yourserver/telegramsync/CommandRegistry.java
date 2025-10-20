package com.yourserver.telegramsync;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;

public class CommandRegistry {
    
    public static void registerCommands(TelegramSyncPlugin plugin) {
        CommandManager commandManager = new CommandManager(plugin);
        
        registerCommand(plugin, "telegramsync", commandManager);
        registerCommand(plugin, "tsync", commandManager);
        registerCommand(plugin, "tg", commandManager);
        registerCommand(plugin, "tgbroadcast", commandManager);
    }
    
    private static void registerCommand(TelegramSyncPlugin plugin, String commandName, CommandExecutor executor) {
        PluginCommand command = plugin.getCommand(commandName);
        if (command != null) {
            command.setExecutor(executor);
        }
    }
}
