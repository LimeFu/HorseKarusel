package dev.limefu.ua.horsekaruseel;

import org.bukkit.entity.Player;

public class HorseRider {
    public Player player;
    public long startTime;

    public HorseRider(Player player, long startTime) {
        this.player = player;
        this.startTime = startTime;
    }
}
