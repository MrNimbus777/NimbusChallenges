package net.nimbus.tournament;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Messages {
    private static final Pattern pattern = Pattern.compile("&#[a-fA-F0-9]{6}");
    /**
     * Returns a colored String. Supports HEX format
     * */
    public static String toColor(String str) {
        Matcher match = pattern.matcher(str);
        while (match.find()) {
            String color = str.substring(match.start() + 1, match.end());
            str = str.replace("&" + color, ChatColor.of(color) + "");
            match = pattern.matcher(str);
        }
        return str.replace("&", "\u00a7");
    }
    /**
     * Returns a prefixed colored String
     * */
    public static String toPrefix(String str) {
        return NTBukkit.a.prefix+toColor(str);
    }

    /**
     * Sends a String message to any CommandSender
     * */
    public static void sendMessage(CommandSender sender, ArrayList<String> strings) {
        for (String str : strings) {
            sender.sendMessage(str);
        }
    }
}
