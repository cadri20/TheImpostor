/*
 * Copyright (C) 2020 Hp
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

import com.cadri.theimpostor.LanguageManager;
import com.cadri.theimpostor.MessageKeys;
import com.cadri.theimpostor.TheImpostor;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

/**
 *
 * @author cadri
 */
public class Arena  {

    private String name;
    private int maxPlayers;
    private int minPlayers;
    private List<Player> players;
    private Location lobby;
    private Location spawn;
    private boolean started;
    
    private Scoreboard board;
    private Objective objective ;
    public Arena(String name, Location lobby){
        this(name,1,10,lobby,null);
    }
    
    public Arena(String name, int maxPlayers, int minPlayers, Location lobby){
        this(name,maxPlayers,minPlayers,lobby,null);
    }
    public Arena(String name, int maxPlayers, int minPlayers, Location lobby, Location spawn) {
        this.name = name;
        this.maxPlayers = maxPlayers;
        this.minPlayers = minPlayers;
        this.players = new ArrayList<>();
        this.lobby = lobby;
        this.started = false;
        this.board = Bukkit.getScoreboardManager().getNewScoreboard();
        objective = board.registerNewObjective("Arena", "dummy", TheImpostor.plugin.getName());
        objective.setDisplaySlot(DisplaySlot.SIDEBAR); 
        objective.getScore(ChatColor.GREEN + "Arena: " + name).setScore(1);
        
        
    }

    public void initCountDown(){
        for(Player player: players)
            player.sendMessage(LanguageManager.getTranslation(MessageKeys.ARENA_READY.key));
        BukkitTask countdown = new ArenaTimer(10, this).runTaskTimer(TheImpostor.plugin, 10L, 20L);
        
    }

    public Scoreboard getBoard() {
        return board;
    }
    

    public void addPlayer(Player player) {
        players.add(player);      
        player.setScoreboard(board);
        
    }
    
    public void removePlayer(Player player){
        if(players.remove(player)){
            player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
            player.sendMessage("Abandonaste la arena");
        }
        else
            player.sendMessage("You're not in Arena");
            
            
    }
    
    public List<Player> getPlayers() {
        return players;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public int getMinPlayers() {
        return minPlayers;
    }

    public void setMinPlayers(int minPlayers) throws IllegalArgumentException {
        if (minPlayers > 4) {
            this.minPlayers = minPlayers;
        } else {
            throw new IllegalArgumentException("Invalid min number of players");
        }
    }

    public Location getLobby() {
        return lobby;
    }

    public void setLobby(Location lobby) {
        this.lobby = lobby;
    }

    public void setSpawn(Location spawn) {
        this.spawn = spawn;
    }

    public Location getSpawn() {
        return spawn;
    }

    

}
