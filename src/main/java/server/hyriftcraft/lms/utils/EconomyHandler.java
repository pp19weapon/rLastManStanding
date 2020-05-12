package server.hyriftcraft.lms.utils;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.entity.Player;

public class EconomyHandler {
    private static EconomyHandler single_inst = null;

    private Economy economy = null;

    private EconomyHandler (){
    }

    public static EconomyHandler getInstance(){
        if (single_inst == null){
            single_inst = new EconomyHandler();
        }

        return single_inst;
    }

    public void setEconomy(Economy econ){
        economy = econ;
    }

    public boolean addMoney(Player player, double value){
        EconomyResponse r = economy.depositPlayer(player, value);
        if (r.transactionSuccess()){
            return true;
        }
        return false;
    }

    public boolean subtractMoney(Player player, double value){
        EconomyResponse r = economy.withdrawPlayer(player, value);
        if (r.transactionSuccess()){
            return true;
        }
        return false;
    }
}
