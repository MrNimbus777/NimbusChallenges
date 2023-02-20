package net.nimbus.tournament;

import common.nimbus.tournament.Logger;

public class BukkitLogger implements Logger {
    @Override
    public void info(String message) {
        NTBukkit.a.getLogger().info(message);
    }

    @Override
    public void error(String message) {
        NTBukkit.a.getLogger().severe(message);
    }
}
