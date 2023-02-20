package bungee.nimbus.tournament;

import bungee.nimbus.tournament.commands.RewardCommand;
import bungee.nimbus.tournament.commands.TeamCommand;
import bungee.nimbus.tournament.commands.TournamentCommand;
import bungee.nimbus.tournament.events.PostLoginEvents;
import common.nimbus.tournament.MySQLUtils;
import common.nimbus.tournament.tournaments.Team;
import common.nimbus.tournament.tournaments.Teams;
import common.nimbus.tournament.tournaments.Tournament;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class NTBungee extends Plugin {

    public static NTBungee b;


    private static Configuration c;
    private static Configuration r;
    private static File file;
    private static File rFile;

    static ConfigurationProvider provider = ConfigurationProvider.getProvider(YamlConfiguration.class);

    public static HashMap<String, ArrayList<String>> rewards = new HashMap<>();
    public static HashMap<UUID, ArrayList<String>> rewardedPlayers = new HashMap<>();

    public static HashMap<String, ArrayList<UUID>> preTeam = new HashMap<>();

    public String prefix;

    public void loadConfig() {
        file = new File(b.getDataFolder(), "config.yml");
        if (!file.exists()) {
            File fd = new File(file.getParent());
            if (!fd.exists()) {
                fd.mkdir();
            }
            try {
                file.createNewFile();
                saveDefaultConfig();
                getLogger().info("Config has been loaded!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                c = provider.load(file);
                getLogger().info("Config has been reloaded!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    void loadEvents() {
        getProxy().getPluginManager().registerListener(b, new PostLoginEvents());
    }

    void loadCommands() {
        getProxy().getPluginManager().registerCommand(b, new TeamCommand());
        getProxy().getPluginManager().registerCommand(b, new TournamentCommand());
        getProxy().getPluginManager().registerCommand(b, new RewardCommand());
    }

    void loadVariables() {
        for (String str : getConfig().getSection("Rewards").getKeys()) {
            ArrayList<String> list = new ArrayList<>(getConfig().getStringList("Rewards." + str));
            rewards.put(str, list);
        }
        prefix = Utils.toColor(getConfig().getString("Server.prefix"));
        for(String uuid : getRewards().getKeys()){
            rewardedPlayers.put(UUID.fromString(uuid), (ArrayList<String>) getRewards().getStringList(uuid));
        }
    }

    public void onEnable() {
        b = this;

        loadConfig();
        rFile = Utils.createFileIfNotExists(b.getDataFolder()+"/rewards.yml");
        try {
            r = provider.load(rFile);
        } catch (Exception e){e.printStackTrace();}
        loadEvents();
        loadCommands();
        loadVariables();


        getProxy().registerChannel("nimbus:tournamentchannel");

        String address = getConfig().getString("Database.address");
        String name = getConfig().getString("Database.name");
        String user = getConfig().getString("Database.user");
        String password = getConfig().getString("Database.password");
        MySQLUtils.newConnection(address, name, user, password, new BungeeLogger());

        MySQLUtils.createIfNotExists("Tournament", "team", "VARCHAR(30)", "points", "INT(16)");
        MySQLUtils.createIfNotExists("Teams", "name", "VARCHAR(30)", "players", "VARCHAR(500)", "minigames", "VARCHAR(10000)");
        MySQLUtils.createIfNotExists("UUIDs", "UUID", "VARCHAR(36)", "name", "VARCHAR(36)");
        MySQLUtils.createIfNotExists("TournamentStatus", "Status", "VARCHAR(8)");
        MySQLUtils.createIfNotExists("TeamStatistics", "name", "VARCHAR(30)", "tournamentsWon", "INT(16)", "tournamentsLost", "INT(16)", "points", "INT(32)");
        MySQLUtils.createIfNotExists("PlayerStatistics", "uuid", "VARCHAR(36)", "tournamentsWon", "INT(16)", "tournamentsLost", "INT(16)", "points", "INT(32)", "kills", "INT(32)");


        Tournament.init();
        Tournament.getTournament().maxGames = getConfig().getInt("Server.maximum-played-games");

        getLogger().info("Server Enabled!");
    }

    public void onDisable() {
        saveConfig();
        saveRewards();
        Teams.getTeams().forEach(Team::save);
        Tournament.getTournament().save();
    }

    /**
     *
     * @return Configuration of the File config.yml
     */
    public static Configuration getConfig() {
        return c;
    }

    /**
     *
     * @return Configuration of the File rewards.yml
     */
    public static Configuration getRewards() {
        return r;
    }

    /**
     * Saves the rewards.yml File.
     */
    public static void saveConfig(){
        try {
            provider.save(c, file);
        } catch (Exception e){e.printStackTrace();}
    }

    /**
     * Saves the rewards.yml File.
     */
    public static void saveReward(){
        try {
            provider.save(r, rFile);
        } catch (Exception e){e.printStackTrace();}
    }

    /**
     * Resets the content of the config.yml File to default one.
     */
    public static void saveDefaultConfig() {
        try {
            Utils.copyFileFromResource("config.yml", file.getPath());
            c = provider.load(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Saves player's rewards into a file.
     */
    public static void saveRewards(){
        for(UUID player : rewardedPlayers.keySet()){
            getRewards().set(player.toString(), rewardedPlayers.get(player));
        }
        saveReward();
    }
}