package bungee.nimbus.tournament;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    /**
     * Creates a file if it does not exist at the path (path+name)
     * @param path Path consists
     * @return The created File.
     */
    public static File createFileIfNotExists(String path){
        File f = new File(path);
        if(!f.exists()){
            File fd = new File(f.getParent());
            if(!fd.exists()){
                fd.mkdir();
            }
            try {
                f.createNewFile();
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        return f;
    }

    /**
     * Creates a file, then copies into it the content of other file from project's resources.
     * @param resource path to the file from resources.
     * @param file_to path to the file that is created.
     * @throws IOException
     * @return The created File.
     */
    public static File copyFileFromResource(String resource, String file_to) throws IOException {
        File f = createFileIfNotExists(file_to);
        InputStream inputStream = NTBungee.b.getClass().getResourceAsStream("/" + resource);
        FileOutputStream to = new FileOutputStream(file_to);
        to.write(inputStream.readAllBytes());
        return f;
    }

    private static final Pattern pattern = Pattern.compile("&#[a-fA-F0-9]{6}");

    /**
     *
     * @param str Input string
     * @return A colored string. Supports the HEX format.
     */
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
     * Returns a prefixed colored String.
     * */
    public static String toPrefix(String str) {
        return NTBungee.b.prefix+toColor(str);
    }

    /**
     *
     * @param sender Who receives the message
     * @param message The message that have to be sent.
     */
    public static void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(new TextComponent(message));
    }

    /**
     * Sends a plugin message to player's server.
     * @param player Player whose server is message receiver.
     * @param data Content of the message
     */
    public static void sendCustomData(ProxiedPlayer player, String ...data) {
        sendCustomData(player.getServer().getInfo(), data);

    }

    /**
     * Sends a plugin message to player's server.
     * @param player Player whose server is message receiver.
     * @param subChannel First line of the plugin message.
     * @param data Content of the message
     */
    public static void sendCustomData(ProxiedPlayer player, String subChannel, ArrayList<String> data) {
        String[] lines = new String[1+data.size()];
        lines[0] = subChannel;
        for(int i = 0; i < data.size(); i++){
            lines[i+1] = data.get(i);
        }
        sendCustomData(player.getServer().getInfo(), lines);
    }

    /**
     * Sends a plugin message to a server
     * @param server Server that have to receive the message.
     * @param data Content of the message
     */
    public static void sendCustomData(ServerInfo server, String[] data){
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        for(String str : data){
            out.writeUTF(str);
        }

        server.sendData( "nimbus:tournamentchannel", out.toByteArray());
    }
}
