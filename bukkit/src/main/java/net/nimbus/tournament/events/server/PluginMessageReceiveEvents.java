package net.nimbus.tournament.events.server;

import common.nimbus.tournament.PluginMessageReceiver;
import common.nimbus.tournament.tournaments.Teams;
import common.nimbus.tournament.tournaments.Tournament;
import common.nimbus.tournament.tournaments.TournamentStatus;
import net.nimbus.tournament.NTBukkit;
import net.nimbus.tournament.Messages;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

public class PluginMessageReceiveEvents implements PluginMessageListener {

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] bytes) {
        if (!channel.equalsIgnoreCase("nimbus:tournamentchannel")) {
            return;
        }
        Bukkit.broadcastMessage("Recieved.");
        PluginMessageReceiver pmr = new PluginMessageReceiver(bytes);
        if (pmr.getSubChannel().equals("RewardsChannel")) {
            player = Bukkit.getPlayer(pmr.getStrings().get(0));
            if (player != null) {
                for (int i = 1; i < pmr.getStrings().size(); i++) {
                    String[] split = pmr.getStrings().get(i).split("!:!");
                    NTBukkit.a.getServer().dispatchCommand(Bukkit.getConsoleSender(), split[1].replace("%player%", player.getName()));
                    player.sendMessage(Messages.toPrefix(NTBukkit.a.getConfig().getString("Messages.rewards.got").replace("%reward%", split[2])));
                }
                player.sendMessage(Messages.toPrefix(NTBukkit.a.getConfig().getString("Messages.rewards.got-all")));
            }
        } else if (pmr.getSubChannel().equals("TournamentUpdate")) {
            Bukkit.broadcastMessage("Updated.");
            Tournament.init();
            if (Tournament.getTournament().getStatus() == TournamentStatus.REWARDED) {
                Teams.clearHash();
                Teams.loadTeams();
                Teams.getTeams().forEach(t -> Tournament.getTournament().removePoints(t.getName()));
            }
        }
    }
}