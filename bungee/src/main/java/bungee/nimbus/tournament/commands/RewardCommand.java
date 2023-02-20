package bungee.nimbus.tournament.commands;

import bungee.nimbus.tournament.NTBungee;
import bungee.nimbus.tournament.Utils;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.ArrayList;

public class RewardCommand extends Command {

    public RewardCommand() {
        super("reward");
    }

    @Override
    public void execute(CommandSender sender, String[] strings) {
        if (sender instanceof ProxiedPlayer p) {
            if (NTBungee.rewardedPlayers.containsKey(p.getUniqueId())) {
                ArrayList<String> allRewards = NTBungee.rewardedPlayers.getOrDefault(p.getUniqueId(), new ArrayList<>());
                String server = p.getServer().getInfo().getName();
                ArrayList<String> list = new ArrayList<>();
                list.add(p.getName());
                for (String reward : allRewards) {
                    String[] splited = reward.split("!:!");
                    if (splited[0].equals(server)) {
                        list.add(reward);

                    }
                }
                Utils.sendCustomData(p, "RewardsChannel", list);
                allRewards.removeAll(list);
                NTBungee.rewardedPlayers.put(p.getUniqueId(), allRewards);
            }
        }
    }
}
