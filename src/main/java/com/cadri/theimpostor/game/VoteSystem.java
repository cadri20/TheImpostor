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

import com.cadri.theimpostor.LanguageManager;
import com.cadri.theimpostor.MessageKey;
import com.cadri.theimpostor.TheImpostor;
import com.cadri.theimpostor.arena.Arena;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Consumer;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitTask;

/**
 *
 * @author cadri
 */
public class VoteSystem{

    private Inventory inv;
    private HashMap<Player, Integer> votes = new HashMap<>();
    private List<Player> playersSkipVotes = new ArrayList<>();
    private HashMap<Player,List<Player>> votersMap = new HashMap<>();
    private boolean tie = false;
    private Arena arena;
    private String skipVoteDisplayName = LanguageManager.getTranslation(MessageKey.SKIP_VOTE);
    private BukkitTask timer;
    private Map<Player, Boolean> playersVoteFlags = new HashMap<>();

    public VoteSystem(List<Player> players, Arena arena) {
        this.arena = arena;
        inv = Bukkit.createInventory(null, GameUtils.getInventorySize(players.size() + 1) , LanguageManager.getTranslation(MessageKey.VOTE));
        for (Player player : players) {
            playersVoteFlags.put(player, false);
            votes.put(player, 0);
            PlayerColor color = arena.getPlayerColor(player);
            if (color == null) {
                TheImpostor.plugin.getLogger().log(Level.SEVERE, "Color of " + player.getName() + " is NULL");
                return;
            }
            ItemStack item = new ItemStack(color.getMaterial());
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(color.getChatColor() + player.getName());         
            item.setItemMeta(meta);
            inv.addItem(item);

        }
        addSkipVote();
        addListVoters(players);
    }

    private void addSkipVote(){
        ItemStack item = new ItemStack(Material.BARRIER);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(skipVoteDisplayName);
        item.setItemMeta(meta);
        inv.addItem(item);
    }
    
    private void addListVoters(List<Player> players){
        for(Player player: players){
            votersMap.put(player, new ArrayList<>());
        }
    }
    public void vote(Player voted, Player voter) {
        Integer voteCount = votes.get(voted);
        if (voteCount == null) {
            TheImpostor.plugin.getLogger().log(Level.SEVERE, "The player " + voted + " is not in the vote");
            return;
        }
        Integer previousCount = votes.put(voted, voteCount + 1);
        if(previousCount != null){ // If the player was in the map            
            voter.sendMessage(LanguageManager.getTranslation(MessageKey.PLAYER_VOTE_FOR, voted.getDisplayName()));
            List<Player> voters = votersMap.get(voted);
            if(voters != null){
                voters.add(voter);
                playersVoteFlags.put(voter, true);
                if(allPlayersVoted()){
                    timer.cancel();
                    arena.stopVote();
                }
            }
            else
                TheImpostor.plugin.getLogger().log(Level.SEVERE, "Voters list is null");
        }
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
        playersVoteFlags.put(voter, true);
        if (allPlayersVoted()) {
            timer.cancel();
            arena.stopVote();
        }
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
        
        
        if(thereIsTie() || maxEntry == null || skipVotesCount() >= maxEntry.getValue())
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
    
    public List<Player> getVoters(Player player){
        return votersMap.get(player);
    }
    
    public String getVotersString(Player player){
        List<Player> voters = votersMap.get(player);
        if(voters == null)
            return null;
        
        PlayerColor playerColor = arena.getPlayerColor(player);
        String votersString = playerColor.getChatColor() + player.getName() + ChatColor.WHITE + " -";
        
        for(Player voter: voters){
            PlayerColor voterColor = arena.getPlayerColor(voter);
            votersString += " " + voterColor.getChatColor() + voter.getName();
        }
        
        return votersString;
    }
    
    public Set<Player> getPlayersInVote(){
        return votes.keySet();
    }
    
    public void startTimer(){
        timer = new VoteTimer(arena.getVotingTime(), arena).runTaskTimer(TheImpostor.plugin, 10L, 20L);
    }
    
    public void stopTimer(){
        if(timer != null)
            timer.cancel();
        
    }
    
    public boolean allPlayersVoted(){
        for(Entry<Player,Boolean> entry: playersVoteFlags.entrySet()){
            if(!entry.getValue()) //If player not voted
                return false;
        }
        
        return true;
    }
}
