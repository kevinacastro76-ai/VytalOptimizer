package com.vytal.optimizer.util;

import org.bukkit.ChatColor;

public final class Colors {
    private Colors() {}

    public static String color(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static void line(org.bukkit.command.CommandSender sender, String text) {
        sender.sendMessage(color(text));
    }
}
