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
package com.cadri.theimpostor.game;

import com.cadri.theimpostor.TheImpostor;
import com.cadri.theimpostor.arena.Arena;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 *
 * @author cadri
 */
public class VoteSystem {
    Inventory inv;
    HashMap<Player,Integer> votes;
    Arena arena;
    public VoteSystem(List<Player> players, Arena arena){
        this.arena = arena;
        inv = Bukkit.createInventory(null, 9, "Vote");
        for(Player player: players){
            PlayerColor color = arena.getPlayerColor(player);
            if(color == null){
                TheImpostor.plugin.getLogger().log(Level.SEVERE, "Color of " + player.getName() + " is NULL");
                return;
            }
            ItemStack item = new ItemStack(color.getMaterial());
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(color.getChatColor() + player.getDisplayName());
            item.setItemMeta(meta);
            inv.addItem(item);
            
        }
        
    }
    
    public void vote(Player player){
       votes.put(player, votes.get(player) + 1);
    }
    
    public Inventory getInventory(){
        return inv;
    }
    
    public Player getMostVoted(){
        Player[] players = (Player[]) votes.keySet().toArray();
        Player mostVoted = players[0];
        
        for(Player player: players){
            int votesNumber = votes.get(player);
            if(votesNumber > votes.get(mostVoted))
                mostVoted = player;
        }
        
        return mostVoted;
    }
}
