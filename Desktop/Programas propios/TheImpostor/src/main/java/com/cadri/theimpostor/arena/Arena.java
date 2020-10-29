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

import com.cadri.theimpostor.game.GameUtils;
import com.cadri.theimpostor.LanguageManager;
import com.cadri.theimpostor.MessageKeys;
import com.cadri.theimpostor.TheImpostor;
import com.cadri.theimpostor.game.ItemOptions;
import com.cadri.theimpostor.game.PlayerColor;
import com.cadri.theimpostor.game.VoteStartTimer;
import com.cadri.theimpostor.game.VoteSystem;
import com.sun.prism.paint.Paint;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.paint.Color;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.golde.bukkit.corpsereborn.CorpseAPI.CorpseAPI;
import org.golde.bukkit.corpsereborn.nms.Corpses.CorpseData;

/**
 *
 * @author cadri
 */
public class Arena {

    private String name;
    private int maxPlayers;
    private int minPlayers;
    private List<Player> players;
    private Location lobby;
    private Location spawn;
    private boolean started;
    private List<Player> crew;
    private List<Player> impostors;
    private Map<Player,Boolean> aliveMap;
    private Map<Player,PlayerColor> playersColor = new HashMap<>();
    private List<CorpseData> corpses;

    private File fileSettings;
    private FileConfiguration yamlSettings;
    private Scoreboard board;
    private Objective objective;

    public Arena(String name, Location lobby) {
        this(name, 1, 10, lobby, null);
        
    }

    public Arena(String name, int maxPlayers, int minPlayers, Location lobby) {
        this(name, maxPlayers, minPlayers, lobby, null);
    }

    public Arena(String name, int maxPlayers, int minPlayers, Location lobby, Location spawn) {
        this.name = name;
        this.maxPlayers = maxPlayers;
        this.minPlayers = minPlayers;
        this.players = new ArrayList<>();
        this.lobby = lobby;
        this.started = false;
        this.crew = new ArrayList<>();
        this.impostors = new ArrayList<>();
        this.aliveMap = new HashMap<>();
        this.corpses = new ArrayList<>();
        this.board = Bukkit.getScoreboardManager().getNewScoreboard();
        this.fileSettings = new File(TheImpostor.plugin.getDataFolder() + File.separator + name + File.separator + "arena_settings.yml");
        this.yamlSettings = YamlConfiguration.loadConfiguration(fileSettings);
        objective = board.registerNewObjective("Arena", "dummy", TheImpostor.plugin.getName());
        
        makeScoreBoard();
    }

    public void initCountDown() {
        for (Player player : players) {
            player.sendMessage(LanguageManager.getTranslation(MessageKeys.ARENA_READY.key));
        }
        BukkitTask countdown = new ArenaTimer(10, this).runTaskTimer(TheImpostor.plugin, 10L, 20L);

    }

    private void makeScoreBoard(){
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.getScore(ChatColor.GREEN + "Arena: " + name).setScore(1);  
        objective.getScore(ChatColor.BLUE + "MinPlayers: " + minPlayers).setScore(0);
    }
    public Scoreboard getBoard() {
        return board;
    }

    public void addPlayer(Player player) {
        players.add(player);
        objective.getScore("Players number: " + players.size()).setScore(2);
        player.setScoreboard(board);
        player.getInventory().addItem(ItemOptions.CHOOSE_COLOR.getItem());
        
        if(players.size() == minPlayers)
            this.initCountDown();
    }

    public void removePlayer(Player player) {
        if (players.remove(player)) {
            player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
            player.sendMessage("Abandonaste la arena");
        } else {
            player.sendMessage("You're not in Arena");
        }

    }

    public void addCorpse(CorpseData corpse){
        corpses.add(corpse);
    }
    public void startGame() {
        for (Player player : this.getPlayers()) {
            player.sendMessage(LanguageManager.getTranslation(MessageKeys.ARENA_GAME_START.key));
        }
        setRoles();
        try{
        for (Player player : players) {
            player.teleport(spawn);
        }
        }catch(IllegalArgumentException e){
            if(spawn == null){
                TheImpostor.plugin.getLogger().log(Level.SEVERE,"Error, spawn is null");
            }else{
            TheImpostor.plugin.getLogger().log(Level.SEVERE,"Location spawn error: " + String.format("X: %d Y: %d Z: %d", spawn.getX(), spawn.getY(), spawn.getZ()));
            }
        }
        for (Player impostor : impostors) {
            String title = LanguageManager.getTranslation(MessageKeys.IMPOSTOR_TITLE.key);
            String subtitle = LanguageManager.getTranslation(MessageKeys.IMPOSTOR_SUBTITLE.key);
            impostor.sendTitle(title, subtitle, 4, 40, 5);
        }

        for (Player crewmate : crew) {
            String title = LanguageManager.getTranslation(MessageKeys.CREWMATE_TITLE.key);
            String subtitle = LanguageManager.getTranslation(MessageKeys.CREWMATE_SUBTITLE.key);
            crewmate.sendTitle(title, subtitle, 5, 40, 5);
        }
        
        GameUtils.setInventoryImpostors(impostors);
        setAliveAll();
        
        if(! areColorsSelected())
            GameUtils.selectRandomColors(playersColor, players, this);
        
        //showColorsOnBoard();
        started = true;
    }

    private void setAliveAll(){
        for(Player player: players){
            aliveMap.put(player, true);
        }
    }
    
    private void showColorsOnBoard(){
        for(Player player: players){
            Objective obj = player.getScoreboard().getObjective(objective.getName());
            if(obj == null){
                TheImpostor.plugin.getLogger().log(Level.SEVERE, "Objective" + objective.getName() + " not found");
                return;
            }        
            PlayerColor color = this.getPlayerColor(player);
            if(color == null){
                TheImpostor.plugin.getLogger().log(Level.SEVERE, "Color of player " + player.getName() + " not assigned");
                return;
            }
            obj.getScore("Color: " + this.getPlayerColor(player).getName()).setScore(3);
        }
    }
    public void setRoles() {
        crew.addAll(players);
        impostors.add(GameUtils.chooseImpostor(crew));
    }

    public void corpseReported(){
        int timeToVote = 30;
        for(Player player: players){
            player.sendTitle("Dead body reported!", "Voting started", 20, 70, 20);
            player.teleport(spawn);
            player.sendMessage("The vote will start in " + timeToVote + " seconds");
        }
        for(CorpseData corpse: corpses){
            CorpseAPI.removeCorpse(corpse);
        }
        
        BukkitTask countdown = new VoteStartTimer(timeToVote, this).runTaskTimer(TheImpostor.plugin, 10L, 20L);
        
    }
    
    public void setPlayerColor(Player player, PlayerColor color){
        playersColor.put(player,color);
        String colorName = color.getChatColor() + player.getName();
        player.setDisplayName(color.getChatColor() + player.getDisplayName());
        player.setPlayerListName(colorName);
        player.setPlayerListHeader(colorName);
    }
    
    public boolean areColorsSelected(){
        for(Player player: players){
            if(playersColor.get(player) == null)
                return false;
        }
        return true;
    }
    
    public void startVoting(){
        VoteSystem voteSystem = new VoteSystem(this.getAlivePlayers(),this);
        
        try{
        for(Player player: players){
            if(isAlive(player))
                player.openInventory(voteSystem.getInventory());
        }
        }catch(NullPointerException e){
            TheImpostor.plugin.getLogger().log(Level.SEVERE, "Error");
        }
    }
    
   
    @Override
    public boolean equals(Object obj) {
        Arena arena = (Arena) obj;
        if (this.getName().equals(arena.getName())) {
            return true;
        }
        return false;
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

        this.yamlSettings.set("spawn.world", spawn.getWorld().getName());
        this.yamlSettings.set("spawn.x", spawn.getX());
        this.yamlSettings.set("spawn.y", spawn.getY());
        this.yamlSettings.set("spawn.z", spawn.getZ());

        try {
            yamlSettings.save(fileSettings);
        } catch (IOException ex) {
            Logger.getLogger(Arena.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public Location getSpawn() {
        return spawn;
    }

    public Map<Player, Boolean> getAliveMap() {
        return aliveMap;
    }

    public boolean isAlive(Player player){
        return aliveMap.get(player);
    }
    public List<Player> getAlivePlayers(){
        ArrayList<Player> alivePlayers = new ArrayList<>();
        for(Player player: players){
            if(isAlive(player))
                alivePlayers.add(player);
        }
        
        return alivePlayers;
    }
    
    public Map<Player, PlayerColor> getPlayersColor() {
        return playersColor;
    }

    public PlayerColor getPlayerColor(Player player){
        return playersColor.get(player);
    }
    
    public boolean isColorSelected(PlayerColor color){
        for(Player player: playersColor.keySet()){
            if(playersColor.get(player).equals(color))
                return true;
        }
        return false;
    }
}
