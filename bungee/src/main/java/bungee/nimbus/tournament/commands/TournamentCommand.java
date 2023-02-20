package bungee.nimbus.tournament.commands;

import bungee.nimbus.tournament.NTBungee;
import bungee.nimbus.tournament.Utils;
import common.nimbus.tournament.tournaments.Tournament;
import common.nimbus.tournament.tournaments.TournamentStatus;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class TournamentCommand extends Command {

    public TournamentCommand() {
        super("tournament");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(sender.hasPermission("nt.tournament.admin")){
            if(args.length>0){
                if(args[0].equalsIgnoreCase("start")){
                    TournamentStatus status = Tournament.getTournament().start();
                    if(status == TournamentStatus.REWARDED){
                        Utils.sendMessage(sender, Utils.toPrefix(NTBungee.getConfig().getString("Messages.Commands.tournament.start")));
                        for(ProxiedPlayer p : NTBungee.b.getProxy().getPlayers()){
                            Utils.sendMessage(p, Utils.toPrefix(NTBungee.getConfig().getString("Messages.Commands.tournament.start-for-team-members")));
                        }
                        Tournament.getTournament().save();
                        for(ServerInfo server : NTBungee.b.getProxy().getServers().values()){
                            Utils.sendCustomData(server, new String[]{"TournamentUpdate"});
                        }
                    } else if(status == TournamentStatus.STARTED){
                        Utils.sendMessage(sender, Utils.toPrefix(NTBungee.getConfig().getString("Messages.Commands.tournament.already-started")));
                    } else {
                        Utils.sendMessage(sender, Utils.toPrefix(NTBungee.getConfig().getString("Messages.Commands.tournament.not-rewarded")));
                    }
                } else if(args[0].equalsIgnoreCase("finish")){
                    TournamentStatus status = Tournament.getTournament().finish();
                    if(status == TournamentStatus.STARTED){
                        Utils.sendMessage(sender, Utils.toPrefix(NTBungee.getConfig().getString("Messages.Commands.tournament.finish")));
                        for(ProxiedPlayer p : NTBungee.b.getProxy().getPlayers()){
                            Utils.sendMessage(p, Utils.toPrefix(NTBungee.getConfig().getString("Messages.Commands.tournament.finish-for-team-members")));
                        }
                        Tournament.getTournament().save();
                        for(ServerInfo server : NTBungee.b.getProxy().getServers().values()){
                            Utils.sendCustomData(server, new String[]{"TournamentUpdate"});
                        }
                    } else if(status == TournamentStatus.FINISHED){
                        Utils.sendMessage(sender, Utils.toPrefix(NTBungee.getConfig().getString("Messages.Commands.tournament.already-finished")));
                    } else {
                        Utils.sendMessage(sender, Utils.toPrefix(NTBungee.getConfig().getString("Messages.Commands.tournament.not-started")));
                    }
                } else if(args[0].equalsIgnoreCase("reward")){
                    TournamentStatus status = Tournament.getTournament().reward(NTBungee.rewards, NTBungee.rewardedPlayers);
                    if(status == TournamentStatus.FINISHED){
                        Utils.sendMessage(sender, Utils.toPrefix(NTBungee.getConfig().getString("Messages.Commands.tournament.reward")));
                        for(ProxiedPlayer p : NTBungee.b.getProxy().getPlayers()){
                            Utils.sendMessage(p, Utils.toPrefix(NTBungee.getConfig().getString("Messages.Commands.tournament.reward-for-team-members")));
                        }
                        Tournament.getTournament().save();
                        for(ServerInfo server : NTBungee.b.getProxy().getServers().values()){
                            Utils.sendCustomData(server, new String[]{"TournamentUpdate"});
                        }
                    } else if(status == TournamentStatus.REWARDED){
                        Utils.sendMessage(sender, Utils.toPrefix(NTBungee.getConfig().getString("Messages.Commands.tournament.already-rewarded")));
                    } else {
                        Utils.sendMessage(sender, Utils.toPrefix(NTBungee.getConfig().getString("Messages.Commands.tournament.not-finished")));
                    }
                }
            }
        }
    }
}
