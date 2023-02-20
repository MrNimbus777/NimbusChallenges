package common.nimbus.tournament.tournaments;

import common.nimbus.tournament.MySQLUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class Teams {

    static HashMap<String, Team> hash = new HashMap<>();

    /**
     * Puts the team in Teams' HashMap.
     * @param team A new Team
     */
    public static void register(Team team) {
        hash.put(team.getName(), team);
    }

    /**
     *
     * @param name Team's name
     * @return The Team with name.
     */
    public static Team getTeam(String name) {
        return hash.getOrDefault(name, null);
    }

    /**
     * Removes the team from Teams' HashMap
     * @param team
     */
    public static void unRegister(Team team) {
        hash.remove(team.getName());
    }

    /**
     *
     * @return A list of all teams.
     */
    public static ArrayList<Team> getTeams() {
        return new ArrayList<>(hash.values());
    }

    /**
     *
     * @return A list of names of all teams.
     */
    public static ArrayList<String> getTeamNames() {
        return new ArrayList<>(hash.keySet());
    }

    /**
     *
     * @param name Name of the team.
     * @return true if the team exists and false if not.
     */
    public static boolean exists(String name) {
        return hash.containsKey(name);
    }

    /**
     * Clears the Teams' HashMap
     */
    public static void clearHash() {
        hash.clear();
    }

    /**
     * Loads Team objects from MySQL Database.
     */
    public static void loadTeams() {
        for (String team : MySQLUtils.getValuesFromColumn("Teams", "name")) {
            try {
                HashMap<String, Integer> minigames = new HashMap<>();
                String miniGames = MySQLUtils.getString("Teams", "name", team, "minigames");
                for (String m : miniGames.split(",")) {
                    String[] split = m.split(":");
                    minigames.put(split[0], Integer.parseInt(split[1]));
                }
                ArrayList<UUID> playerList = new ArrayList<>();
                String players = MySQLUtils.getString("Teams", "name", team, "players");
                for (String p : players.split(",")) {
                    playerList.add(UUID.fromString(p));
                }

                Team t = new Team(team, playerList);
                register(t);
            } catch (Exception e) {
                MySQLUtils.log.error("Cannot load team " + team);
            }
        }
    }
}