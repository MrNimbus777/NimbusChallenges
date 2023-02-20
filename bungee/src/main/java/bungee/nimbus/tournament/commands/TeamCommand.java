package bungee.nimbus.tournament.commands;

import bungee.nimbus.tournament.NTBungee;
import bungee.nimbus.tournament.Utils;
import common.nimbus.tournament.MySQLUtils;
import common.nimbus.tournament.tournaments.Team;
import common.nimbus.tournament.tournaments.Teams;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

import java.util.ArrayList;
import java.util.UUID;

public class TeamCommand extends Command {
    public TeamCommand() {
        super("team");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender.hasPermission("nt.teams.admin")) {
            if (args.length > 0) {
                if (args[0].equalsIgnoreCase("create")) {
                    if (args.length > 1) {
                        if (!Teams.exists(args[1])) {
                            NTBungee.preTeam.put(args[1], new ArrayList<>());
                            Utils.sendMessage(sender, Utils.toPrefix(NTBungee.getConfig().getString("Messages.Commands.team.precreate").replace("%name%", args[1])));
                        } else
                            Utils.sendMessage(sender, Utils.toPrefix(NTBungee.getConfig().getString("Messages.Commands.team.already-exists")));
                    } else
                        Utils.sendMessage(sender, Utils.toPrefix(NTBungee.getConfig().getString("Messages.Commands.team.Usage.team-create")));
                } else if (args[0].equalsIgnoreCase("delete") || args[0].equalsIgnoreCase("remove")) {
                    if (args.length > 1) {
                        if (Teams.exists(args[1])) {
                            Teams.getTeam(args[1]).remove();
                            Utils.sendMessage(sender, Utils.toPrefix(NTBungee.getConfig().getString("Messages.Commands.team.delete").replace("%name%", args[1])));
                        } else
                            Utils.sendMessage(sender, Utils.toPrefix(NTBungee.getConfig().getString("Messages.Commands.team.no-such-team")));
                    } else
                        Utils.sendMessage(sender, Utils.toPrefix(NTBungee.getConfig().getString("Messages.Commands.team.Usage.team-delete")));
                } else if (args[0].equalsIgnoreCase("add")) {
                    if (args.length > 1) {
                        if (NTBungee.preTeam.containsKey(args[1])) {
                            ArrayList<UUID> list = NTBungee.preTeam.getOrDefault(args[1], new ArrayList<>());
                            if (args.length > 2) {
                                String uuid = MySQLUtils.getUuid(args[2]);
                                if (uuid != null) {
                                    UUID id = UUID.fromString(uuid);
                                    boolean contains = list.contains(id);
                                    for (Team t : Teams.getTeams()) {
                                        contains = contains || t.getPlayers().contains(id);
                                    }
                                    if (!contains) {
                                        list.add(id);
                                        if (list.size() == 10) {
                                            Team team = new Team(args[1], list);
                                            Teams.register(team);
                                            Utils.sendMessage(sender, Utils.toPrefix(NTBungee.getConfig().getString("Messages.Commands.team.create").replace("%name%", args[1])));
                                        } else
                                            Utils.sendMessage(sender, Utils.toPrefix(NTBungee.getConfig().getString("Messages.Commands.team.players-left").replace("%team%", args[1]).replace("%player%", args[2]).replace("%left%", "" + (10 - list.size()))));
                                    } else
                                        Utils.sendMessage(sender, Utils.toPrefix(NTBungee.getConfig().getString("Messages.Commands.team.player-already-added")));
                                } else
                                    Utils.sendMessage(sender, Utils.toPrefix(NTBungee.getConfig().getString("Messages.Commands.team.no-such-uuid")));
                            } else
                                Utils.sendMessage(sender, Utils.toPrefix(NTBungee.getConfig().getString("Messages.Commands.team.Usage.team-add")));
                        } else
                            Utils.sendMessage(sender, Utils.toPrefix(NTBungee.getConfig().getString("Messages.Commands.team.no-such-team")));
                    } else
                        Utils.sendMessage(sender, Utils.toPrefix(NTBungee.getConfig().getString("Messages.Commands.team.Usage.team-add")));
                } else
                    Utils.sendMessage(sender, Utils.toPrefix(NTBungee.getConfig().getString("Messages.Commands.team.Usage.team")));
            } else
                Utils.sendMessage(sender, Utils.toPrefix(NTBungee.getConfig().getString("Messages.Commands.team.Usage.team")));
        } else Utils.sendMessage(sender, Utils.toPrefix(NTBungee.getConfig().getString("Messages.no-permission")));
    }
}