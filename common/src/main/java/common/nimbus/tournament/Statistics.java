package common.nimbus.tournament;

import common.nimbus.tournament.MySQLUtils;

public class Statistics {
    /**
     *
     * @param team The name of the team whose statistic is necessary
     * @param statistic The statistic type that is necessary (ex. tournamentsWon)
     * @return Returns the Integer value of the static of the player with name team.
     */
    public static int getTeamValue(String team, String statistic){
        return MySQLUtils.getInt("TeamStatistics", "name", team, statistic);
    }

    /**
     *
     * @param uuid The UUID of the player whose statistic is necessary
     * @param statistic The statistic type that is necessary (ex. tournamentsWon)
     * @return Returns the Integer value of the static of the player with UUID.
     */
    public static int getPlayerValue(String uuid, String statistic){
        return MySQLUtils.getInt("PlayerStatistics", "name", uuid, statistic);
    }

    /**
     *
     * @param team Team's name
     * @param statistic Statistic type
     * @param value Value that need to be set
     */
    public static void setTeamValue(String team, String statistic, int value){
        MySQLUtils.set("TeamStatistics", "name", team, statistic, value);
    }
    /**
     *
     * @param uuid Player's UUID
     * @param statistic Statistic type
     * @param value Value that need to be set
     */
    public static void setPlayerValue(String uuid, String statistic, int value){
        MySQLUtils.set("PlayerStatistics", "name", uuid, statistic, value);
    }
}
