package common.nimbus.tournament.tournaments;

import common.nimbus.tournament.MySQLUtils;
import common.nimbus.tournament.Statistics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class Tournament {


    private static Tournament t;

    private final HashMap<String, Integer> points = new HashMap<>();

    TournamentStatus status;

    public int maxGames;

    /**
     * @return The tournament.
     */
    public static Tournament getTournament(){
        return t;
    }

    /**
     * Inits the tournament.
     */
    public static void init(){
        t = new Tournament(MySQLUtils.getStatus("TournamentStatus"));
    }

    public Tournament(TournamentStatus status){
        this.status = status;
        if(status == TournamentStatus.STARTED || status == TournamentStatus.FINISHED) loadPoints();
    }

    /**
     * @return Tournament's status.
     */
    public TournamentStatus getStatus() {
        return status;
    }

    /**
     * @param team Team's name.
     * @return Team's amount of points
     */
    public int getPoints(String team){
        return points.getOrDefault(team, 0);
    }

    /**
     * Set points to the team
     * @param team Team's name.
     * @param points Amount of points.
     */
    public void setPoints(String team, int points){
        if(Teams.exists(team)) this.points.put(team, points);
    }

    /**
     * Remove points of the team.
     * @param team Team's name.
     */
    public void removePoints(String team){
        this.points.remove(team);
        MySQLUtils.remove("Tournament", "team", team);
    }

    /**
     * Saves the tournament in database.
     */
    public void save(){
        for(String team : points.keySet()){
            MySQLUtils.set("Tournament", "team", team, "points", points.get(team));
        }
        MySQLUtils.setStatus("TournamentStatus", status);
    }

    /**
     * @return The top of teams.
     * @param top Limit of top.
     */
    public static ArrayList<String> getTeamTop(int top) {
        ArrayList<String> list = new ArrayList<>(Teams.getTeamNames());
        ArrayList<String> largest = new ArrayList<>();
        for(int y = 0; y < top && y < Teams.getTeamNames().size(); y++){
            int largestID = 0;
            for(int x = 1; x < list.size(); x++){
                if(Tournament.getTournament().getPoints(list.get(largestID)) < Tournament.getTournament().getPoints(list.get(x))) largestID = x;
            }
            largest.add(list.get(largestID));
            list.remove(largestID);
        }
        return largest;
    }

    /**
     * Starts the tournament if it's possible.
     * @return Tournament's status before starting.
     */
    public TournamentStatus start(){
        points.clear();
        Teams.getTeams().forEach(team -> team.setMinigames(new HashMap<>()));
        if(status == TournamentStatus.REWARDED){
            status = TournamentStatus.STARTED;
            return TournamentStatus.REWARDED;
        }
        return status;
    }

    /**
     * Finishes the tournament if it is possible.
     * @return Tournament's status before finishing.
     */
    public TournamentStatus finish(){
        if(status == TournamentStatus.STARTED){
            status = TournamentStatus.FINISHED;
            return TournamentStatus.STARTED;
        }
        return status;
    }

    /**
     * Rewards all players that have participated in the tournament if it's possible.
     * @param rewards HashMap of the rewards
     * @param rewardedPlayers HashMap where of players' rewards.
     * @return Tournament's status before rewarding
     */
    public TournamentStatus reward(HashMap<String, ArrayList<String>> rewards, HashMap<UUID, ArrayList<String>> rewardedPlayers){
        if(status == TournamentStatus.FINISHED){
            Teams.getTeams().forEach(team -> team.setMinigames(new HashMap<>()));
            ArrayList<String> teams = getTeamTop(Teams.getTeams().size());
            for(int i = 0; i < (rewards.size() & teams.size()); i++){
                ArrayList<String> reward = rewards.getOrDefault("top_"+i, new ArrayList<>());
                if(reward.size() > 0) {
                    for(UUID player : Teams.getTeam(teams.get(i)).getPlayers()){
                        ArrayList<String> playersRewards = rewardedPlayers.getOrDefault(player, new ArrayList<>());
                        playersRewards.addAll(reward);
                        rewardedPlayers.put(player, playersRewards);
                    }
                }
            }
            if(Teams.getTeams().size() > 0) {
                String teamWon = teams.get(0);
                for (Team t : Teams.getTeams()) {
                    if (t.getName().equals(teamWon)) {
                        Statistics.setTeamValue(t.getName(), "tournamentsWon", Statistics.getTeamValue(t.getName(), "tournamentsWon") + 1);
                        for (UUID player : t.getPlayers()) {
                            Statistics.setPlayerValue(player.toString(), "tournamentsWon", Statistics.getPlayerValue(player.toString(), "tournamentsWon") + 1);
                        }
                    } else {
                        Statistics.setTeamValue(t.getName(), "tournamentsLost", Statistics.getTeamValue(t.getName(), "tournamentsLost") + 1);
                        for (UUID player : t.getPlayers()) {
                            Statistics.setPlayerValue(player.toString(), "tournamentsLost", Statistics.getPlayerValue(player.toString(), "tournamentsLost") + 1);
                        }
                    }
                }
            }
            status = TournamentStatus.REWARDED;
            return TournamentStatus.FINISHED;
        }
        return status;
    }

    /**
     * Loads the HashMap of points.
     */
    public void loadPoints(){
        for(String team : MySQLUtils.getValuesFromColumn("Tournament", "team")){
            try {
                int points = MySQLUtils.getInt("Tournament", "team", team, "points");
                this.points.put(team, points);
            } catch (Exception e){

            }
        }
    }
}