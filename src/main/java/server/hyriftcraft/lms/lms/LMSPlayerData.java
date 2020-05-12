package server.hyriftcraft.lms.lms;

import org.bukkit.Location;
import org.bukkit.inventory.PlayerInventory;

public class LMSPlayerData {
    private int killCount = 0;
    private int retried = 0;
    private Location lastKnownLocation = null;
    private PlayerInventory inventory = null;
    private boolean keepInventory = false;
    private double prize = 0;

    public LMSPlayerData() {
    }

    public double getPrize() {
        return prize;
    }

    public void setPrize(double prize) { this.prize = prize; }

    @Deprecated
    public boolean isKeepInventory() {
        return keepInventory;
    }

    @Deprecated
    public void setKeepInventory(boolean keepInventory) {
        this.keepInventory = keepInventory;
    }

    @Deprecated
    public PlayerInventory getInventory() {
        return inventory;
    }

    @Deprecated
    public void setInventory(PlayerInventory inventory) {
        this.inventory = inventory;
    }

    public int getKillCount() {
        return killCount;
    }

    public void setKillCount(int killCount) {
        this.killCount = killCount;
    }

    public void addKillCount(int add) { killCount = killCount + add; }

    public int getRetried() {
        return retried;
    }

    public void setRetried(int retried) {
        this.retried = retried;
    }

    public Location getLastKnownLocation() {
        return lastKnownLocation;
    }

    public void setLastKnownLocation(Location lastKnownLocation) { this.lastKnownLocation = lastKnownLocation; }
}
