package net.nimbus.tournament;

import common.nimbus.tournament.MySQLUtils;
import common.nimbus.tournament.tournaments.Team;
import common.nimbus.tournament.tournaments.Teams;
import common.nimbus.tournament.tournaments.Tournament;
import net.nimbus.tournament.events.server.PluginMessageReceiveEvents;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class NTBukkit extends JavaPlugin {

    public static NTBukkit a;
    public String prefix;

    /**
     *
     * @param reload If true the config will be reloaded. If false the config will be created if it does not.
     */
    public void loadConfig(boolean reload){
        File config = new File(getDataFolder(), "config.yml");
        if (!config.exists()) {
            getConfig().options().copyDefaults(true);
            saveDefaultConfig();
            getLogger().info("Created new config.yml file at " + config.getPath());
        } else if (reload) {
            try {
                getConfig().load(config);
                getLogger().info("Config reloaded.");
            } catch (Exception exception) {
                getLogger().info("Failed to reload the config");
            }
        }
    }

    void loadVariables(){
        Tournament.init();
        prefix = Messages.toColor(getConfig().getString("Server.prefix"));
    }

    public void onEnable() {
        a = this;

        loadConfig(false);
        String adress = getConfig().getString("Database.address");
        String name = getConfig().getString("Database.name");
        String user = getConfig().getString("Database.user");
        String password = getConfig().getString("Database.password");
        MySQLUtils.newConnection(adress, name, user, password, new BukkitLogger());

        if(MySQLUtils.con != null){

            loadVariables();

            getServer().getMessenger().registerIncomingPluginChannel(this, "nimbus:tournamentchannel", new PluginMessageReceiveEvents());

            getLogger().info("Plugin enabled");
        } else {
            getLogger().severe("Cannot connect to MySQL Database, disabling plugin.");
            getServer().getPluginManager().getPlugin(getName()).onDisable();
        }
    }

    public void onDisable() {
        if(MySQLUtils.con != null){
            Teams.getTeams().forEach(Team::save);
        }
        getLogger().info("Plugin disabled");
    }
}
