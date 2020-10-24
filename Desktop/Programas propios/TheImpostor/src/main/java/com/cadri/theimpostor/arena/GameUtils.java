/*
 * Copyright (C) 2020 cadri
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.cadri.theimpostor.arena;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

/**
 *
 * @author cadri
 */
public class GameUtils {
    private static Random random = new Random();
    public static Player chooseImpostor(List<Player> players){
        int index = random.nextInt(players.size());
        
        return players.remove(index);
    }
    
    public static void setInventoryImpostors(List<Player> impostors){
        ItemStack killItem = new ItemStack(Material.RED_WOOL);
        ItemMeta killItemMeta = killItem.getItemMeta(); 
        killItemMeta.setDisplayName("Kill Player");
        List<String> loreKillItem = new ArrayList<>();
        loreKillItem.add("Hit someone with this to kill him");
        killItemMeta.setLore(loreKillItem);
        
        killItem.setItemMeta(killItemMeta);
        for(Player impostor: impostors){
            PlayerInventory impostorInventory = impostor.getInventory();
            impostorInventory.setItem(0, killItem);
        }
    }
    

}
