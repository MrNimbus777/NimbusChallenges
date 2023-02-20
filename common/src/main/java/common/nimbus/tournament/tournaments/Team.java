package common.nimbus.tournament.tournaments;

import common.nimbus.tournament.MySQLUtils;
import common.nimbus.tournament.Statistics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class Team {

    String name;
    ArrayList<UUID> players;
    HashMap<String, Integer> minigames = new HashMap<>();

    public Team(String name, ArrayList<UUID> players) {
        this.name = name;
        this.players = players;
    }

    /**
     * @return List of team members
     */
    public ArrayList<UUID> getPlayers() {
        return players;
    }

    /**
     * Sets the list of team members.
     * @param players
     */
    public void setPlayers(ArrayList<UUID> players){
        this.players = players;
    }

    public String getName() {
        return name;
    }

    /**
     *
     * @return HashMap of all played by team minigames.
     */
    public HashMap<String, Integer> getMinigames() {
        return minigames;
    }

    /**
     * Sets the list of minigames that have been played by team.
     * @param minigames
     */
    public void setMinigames(HashMap<String, Integer> minigames) {
        this.minigames = minigames;
    }

    /**
     * Used when a team wins a minigame and gets points.
     * @param minigame Minigame name.
     * @param points Amount of points.
     */
    public void won(String minigame, int points){
        int times = minigames.getOrDefault(minigame, 0);
        if(Tournament.getTournament().getStatus() == TournamentStatus.STARTED && times < Tournament.getTournament().maxGames){
            minigames.put(minigame, times+1);
            Tournament.getTournament().setPoints(name, Tournament.getTournament().getPoints(name)+points);
            Statistics.setTeamValue(getName(), "points", Statistics.getTeamValue(getName(), "points")+points);
            for(UUID player : getPlayers()){
                Statistics.setPlayerValue(player.toString(), "points", Statistics.getPlayerValue(player.toString(), "points")+points);
            }
        }
    }

    /**
     * Saves the Team in Database.
     */
    public void save() {
        String minig = "";
        if(minigames.size()>0){
            ArrayList<String> miniGames = new ArrayList<>(minigames.keySet());
            minig = miniGames.get(0);
            minig = minig+":"+minigames.get(minig);
            for (int i = 1; i < miniGames.size(); i++) {
                minig = minig + "," + miniGames.get(i);
            }
        }
        String pls = "";
        for (int i = 0; i < 10; i++){
            pls = pls + "," + players.get(i);
        }
        MySQLUtils.set("Teams", "name", name, "players", pls);
        MySQLUtils.set("Teams", "name", name, "minigames", minig);
    }

    /**
     * Removes the Team from the Universe...
     */
    public void remove() {
        MySQLUtils.remove("Teams", "name", name);
        Tournament.getTournament().removePoints(name);
        Teams.unRegister(this);
    }
}
