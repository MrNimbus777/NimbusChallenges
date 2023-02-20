package bungee.nimbus.tournament;

import common.nimbus.tournament.Logger;

public class BungeeLogger implements Logger {

    @Override
    public void info(String message) {
        NTBungee.b.getLogger().info(message);
    }

    @Override
    public void error(String message) {
        NTBungee.b.getLogger().severe(message);
    }
}
