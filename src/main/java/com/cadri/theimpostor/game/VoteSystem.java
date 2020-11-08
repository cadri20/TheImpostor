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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import javafx.scene.paint.Color;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
public class VoteSystem{

    private Inventory inv;
    private HashMap<Player, Integer> votes = new HashMap<>();
    private List<Player> playersSkipVotes = new ArrayList<>();
    private boolean tie = false;
    private Arena arena;
    private String skipVoteDisplayName = ChatColor.DARK_PURPLE + "Skip Vote";

    public VoteSystem(List<Player> players, Arena arena) {
        this.arena = arena;
        inv = Bukkit.createInventory(null, 9, "Vote");
        for (Player player : players) {
            votes.put(player, 0);
            PlayerColor color = arena.getPlayerColor(player);
            if (color == null) {
                TheImpostor.plugin.getLogger().log(Level.SEVERE, "Color of " + player.getName() + " is NULL");
                return;
            }
            ItemStack item = new ItemStack(color.getMaterial());
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(color.getChatColor() + player.getDisplayName());
            item.setItemMeta(meta);
            inv.addItem(item);

        }
        addSkipVote();

    }

    private void addSkipVote(){
        ItemStack item = new ItemStack(Material.BARRIER);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(skipVoteDisplayName);
        item.setItemMeta(meta);
        inv.addItem(item);
    }
    public void vote(Player voted, Player voter) {
        Integer voteCount = votes.get(voted);
        if (voteCount == null) {
            TheImpostor.plugin.getLogger().log(Level.SEVERE, "The player " + voted + " is not in the vote");
            return;
        }
        Integer previousCount = votes.put(voted, voteCount + 1);
        if(previousCount != null)
            voter.sendMessage("You voted for " + voted.getName());
    }

    public void vote(PlayerColor color, Player voter) {
        Player voted = arena.getPlayer(color);
        if (voted == null) {
            TheImpostor.plugin.getLogger().log(Level.SEVERE, "Player has not a color assigned");
        }
        vote(voted, voter);
    }

    public void skipVote(Player voter){
        playersSkipVotes.add(voter);
    }
    public Inventory getInventory() {
        return inv;
    }

    /**
     * 
     * @return The player most voted, if there is a tie or the majority skipped the vote it returns null;
     */
    public Player getMostVoted() {
        Entry<Player,Integer> maxEntry = null;
        
        for(Entry<Player,Integer> entry: votes.entrySet()){
            int comparison;
            if(maxEntry == null){
                maxEntry = entry;
            }else if((comparison = entry.getValue().compareTo(maxEntry.getValue())) >= 0){
                tie = comparison == 0;
                maxEntry = entry;
            }
        }
        if(thereIsTie() || skipVotesCount() >= maxEntry.getValue())
            return null;

        return maxEntry.getKey();
    }
    
    public int skipVotesCount(){
        return playersSkipVotes.size();
    }
    
    public boolean thereIsTie(){
        return tie;
    }

    public String getSkipVoteText() {
        return skipVoteDisplayName;
    }
    
    
}
