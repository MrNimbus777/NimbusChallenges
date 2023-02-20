package common.nimbus.tournament;

import common.nimbus.tournament.tournaments.Tournament;
import common.nimbus.tournament.tournaments.TournamentStatus;

import java.sql.*;
import java.util.ArrayList;
import java.util.Locale;

public class MySQLUtils {
    public static Connection con;
    public static Logger log;
    static int tries = 10;


    static String adress;
    static String name;
    static String user;
    static String password;

    public static void newConnection(String adress, String name, String user, String password, Logger log) {
        MySQLUtils.adress = adress;
        MySQLUtils.name = name;
        MySQLUtils.user = user;
        MySQLUtils.password = password;
        try {
            MySQLUtils.log = log;
            con = DriverManager.getConnection("jdbc:mysql://" + adress + "/" + name, user, password);
        } catch (Exception e) {
            con = null;
        }
    }

    public static String getUuid(String name) {
        return MySQLUtils.getString("UUIDs", "name", name, "UUID");
    }

    public static String getName(String uuid) {
        return MySQLUtils.getString("UUIDs", "UUID", uuid, "name");
    }

    public static void newConnection(Logger log) {
        try {
            MySQLUtils.log = log;
            con = DriverManager.getConnection("jdbc:mysql://" + adress + "/" + name, user, password);
        } catch (Exception e) {
            con = null;
        }
    }

    public static boolean setStatus(String table, TournamentStatus status) {
        try {
            MySQLUtils.remove(table, "Status", getStatus(table).name());
            PreparedStatement st = con.prepareStatement("SELECT * FROM " + table);
            st.execute("INSERT INTO " + table + "(Status) VALUES ('" + status.name() + "') ON DUPLICATE KEY UPDATE Status  = '" + status.name() +"'");
            st.close();
            tries = 10;
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            tries--;
            if (tries > 0) {
                newConnection(log);
                log.error("Trying to recconect to MySQL Database...");
                if (con != null) {
                    setStatus(table, status);
                    return true;
                }
            }
            return false;
        }
    }

    public static TournamentStatus getStatus(String table) {
        try {
            return TournamentStatus.valueOf(MySQLUtils.getValuesFromColumn(table, "Status").get(0));
        } catch (Exception e){
            return TournamentStatus.REWARDED;
        }
    }

    public static boolean set(String table, String column_from, String key, String column_to, double value) {
        try {
            PreparedStatement st = con.prepareStatement("SELECT * FROM " + table + " WHERE " + column_from + " = '" + key + "'");
            st.execute("INSERT INTO " + table + "(" + column_from + "," + column_to + ") VALUES ('" + key + "', " + value + ") ON DUPLICATE KEY UPDATE " + column_to + " = " + value);
            st.close();
            tries = 10;
            return true;
        } catch (SQLException e) {
            tries--;
            if (tries > 0) {
                newConnection(log);
                log.error("Trying to recconect to MySQL Database...");
                if (con != null) {
                    set(table, column_from, key, column_to, value);
                    return true;
                }
            }
            return false;
        }
    }

    public static boolean set(String table, String column_from, String key, String column_to, int value) {
        return set(table, column_from, key, column_to, (double) value);
    }


    public static boolean set(String table, String column_from, String key, String column_to, String value) {
        try {
            PreparedStatement st = con.prepareStatement("SELECT * FROM " + table + " WHERE " + column_from + " = '" + key + "'");
            st.execute("INSERT INTO " + table + "(" + column_from + ", " + column_to + ") VALUES ('" + key + "', '" + value + "') ON DUPLICATE KEY UPDATE " + column_to + " = '" + value+"'");
            st.close();
            tries = 10;
            return true;
        } catch (SQLException e) {
            tries--;
            if (tries > 0) {
                newConnection(log);
                log.error("Trying to recconect to MySQL Database...");
                if (con != null) {
                    set(table, column_from, key, column_to, value);
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * @param table
     * @param column
     * @return Returns all values form specified column in a table.
     */
    public static ArrayList<String> getValuesFromColumn(String table, String column) {
        ArrayList<String> keys = new ArrayList<>();

        try {
            PreparedStatement statement = con.prepareStatement("SELECT * FROM " + table);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                keys.add(rs.getString(column));
            }
            tries = 10;
            return keys;
        } catch (SQLException e) {
            tries--;
            if (tries > 0) {
                newConnection(log);
                log.error("Trying to recconect to MySQL Database...");
                if (con != null) return getValuesFromColumn(table, column);
            }
            tries = 10;
            return new ArrayList<>();
        }
    }

    public static String getString(String table, String column_from, String key, String column_to) {
        try {
            PreparedStatement statement = con.prepareStatement("SELECT * FROM " + table + " WHERE " + column_from + " = '" + key + "'");
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                String s = rs.getString(column_to);
                tries = 10;
                return s;
            }
            tries = 10;
            return null;
        } catch (SQLException e) {
            tries--;
            if (tries > 0) {
                newConnection(log);
                log.error("Trying to recconect to MySQL Database...");
                if (con != null) return getString(table, column_from, key, column_to);
            }
            tries = 10;
            return null;
        }
    }

    public static double getDouble(String table, String column_from, String key, String column_to) {
        try {
            PreparedStatement statement = con.prepareStatement("SELECT * FROM " + table + " WHERE " + column_from + " = '" + key + "'");
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                double d = rs.getDouble(column_to);
                tries = 10;
                return d;
            }
            tries = 10;
            return 0;
        } catch (SQLException e) {
            tries--;
            if (tries > 0) {
                newConnection(log);
                log.error("Trying to recconect to MySQL Database...");
                if (con != null) return getDouble(table, column_from, key, column_to);
            }
            tries = 10;
            return 0;
        }
    }

    public static int getInt(String table, String column_from, String key, String column_to) {
        try {
            PreparedStatement statement = con.prepareStatement("SELECT * FROM " + table + " WHERE " + column_from + " = '" + key + "'");
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                int d = rs.getInt(column_to);
                tries = 10;
                return d;
            }
            tries = 10;
            return 0;
        } catch (SQLException e) {
            tries--;
            if (tries > 0) {
                newConnection(log);
                log.error("Trying to recconect to MySQL Database...");
                if (con != null) return getInt(table, column_from, key, column_to);
            }
            tries = 10;
            return 0;
        }
    }

    public static boolean exists(String table, String column_from, String key) {
        try {
            PreparedStatement statement = con.prepareStatement("SELECT * FROM " + table + " WHERE " + column_from + " = '" + key + "'");
            ResultSet rs = statement.executeQuery();
            tries = 10;
            return rs.next();
        } catch (SQLException e) {
            tries--;
            if (tries > 0) {
                newConnection(log);
                log.error("Trying to recconect to MySQL Database...");
                if (con != null) return exists(table, column_from, key);
            }
            return false;
        }
    }

    public static boolean remove(String table, String column_from, String key) {
        try {
            PreparedStatement st = con.prepareStatement("SELECT * FROM " + table);
            st.execute("DELETE FROM " + table + " WHERE " + column_from + " = '" + key + "'");
            st.close();
            tries = 10;
            return true;
        } catch (SQLException e) {
            tries--;
            if (tries > 0) {
                newConnection(log);
                log.error("Trying to recconect to MySQL Database...");
                if (con != null) {
                    remove(table, column_from, key);
                    return true;
                }
            }
            tries = 10;
            return false;
        }
    }

    public static boolean createIfNotExists(String table, String primary_key, String primary_value_type, String... args) {
        try {
            log.info("Trying to create " + table + " mysql table...");
            String cmd = "";
            for (int i = 0; i < args.length; i+=2) {
                cmd = cmd + ", " + args[i] + " " + args[i + 1].toUpperCase(Locale.ROOT);
            }
            PreparedStatement statement = con.prepareStatement("CREATE TABLE IF NOT EXISTS " + table + " (" + primary_key + " " + primary_value_type + cmd + ", PRIMARY KEY (" + primary_key + "))");
            statement.execute();
            statement.close();
            log.info("Success!");
            return true;
        } catch (SQLException e) {
            log.error("Failed!");
            e.printStackTrace();
            return false;
        }
    }
}