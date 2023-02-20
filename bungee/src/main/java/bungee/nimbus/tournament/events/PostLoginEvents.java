package bungee.nimbus.tournament.events;

import common.nimbus.tournament.MySQLUtils;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PostLoginEvents implements Listener {
    @EventHandler
    public void PJE(PostLoginEvent e){
        String uuid = MySQLUtils.getUuid(e.getPlayer().getName());
        if(uuid == null){
            MySQLUtils.set("UUIDs", "UUID", e.getPlayer().getUniqueId().toString(), "name", e.getPlayer().getName());
        } else {
            MySQLUtils.set("UUIDs", "name", e.getPlayer().getName(), "UUID", e.getPlayer().getUniqueId().toString());
        }
    }
}
