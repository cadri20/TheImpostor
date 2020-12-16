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

import com.cadri.theimpostor.game.GameUtils;
import com.cadri.theimpostor.LanguageManager;
import com.cadri.theimpostor.MessageKeys;
import com.cadri.theimpostor.TheImpostor;
import com.cadri.theimpostor.game.ItemOptions;
import com.cadri.theimpostor.game.PlayerColor;
import com.cadri.theimpostor.game.VoteStartTimer;
import com.cadri.theimpostor.game.VoteSystem;
import com.cadri.theimpostor.game.VoteTimer;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
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
    private int timeToVote;
    private int voteTime;
    private int impostorsAlive;
    private int killTime;
    private List<Player> crew;
    private List<Player> impostors;
    private Map<Player,Boolean> aliveMap;
    private Map<Player,PlayerColor> playersColor = new HashMap<>();
    private Map<Player,Location> playerLocations = new HashMap<>();
    private Map<Player,ItemStack[]> invStore = new HashMap<>();
    private Map<Player,Boolean> impostorsKillFlags = new HashMap<>(); 
    private List<CorpseData> corpses;

    private File fileSettings;
    private FileConfiguration yamlSettings;
    private Scoreboard board;
    private Objective objective;
    private VoteSystem voteSystem = null;
    
    public Arena(String name, Location lobby) {
        this(name, 1, 10, lobby, null); 
        
    }
/*
    public Arena(String name, int maxPlayers, int minPlayers, Location lobby) {
        this(name, maxPlayers, minPlayers, lobby, null);
    }
*/
    public Arena(String name, int maxPlayers, int minPlayers, Location lobby, Location spawn) {
        this.name = name;
        this.maxPlayers = maxPlayers;
        this.minPlayers = minPlayers;
        this.players = new ArrayList<>();
        this.lobby = lobby;
        this.spawn = spawn;
        this.started = false;
        this.timeToVote = 30;
        this.voteTime = 30;
        this.killTime = 10;
        this.crew = new ArrayList<>();
        this.impostors = new ArrayList<>();
        this.impostorsAlive = 0;
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
        playerLocations.put(player, player.getLocation());
        objective.getScore("Players number: " + players.size()).setScore(2);
        invStore.put(player, player.getInventory().getContents());
        player.getInventory().clear();
        player.setScoreboard(board);
        player.getInventory().addItem(ItemOptions.CHOOSE_COLOR.getItem());
        
        if(players.size() == minPlayers)
            this.initCountDown();
    }

    public boolean removePlayer(Player player) {
        if (! players.remove(player))
            return false;
        
        GameUtils.setPlayerVisible(player, this);
        player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
        playersColor.remove(player);
        aliveMap.remove(player);
        if(this.isImpostor(player))
            impostors.remove(player);
        else
            crew.remove(player);
        player.teleport(playerLocations.get(player));
        playerLocations.remove(player);
        resetInventory(player);
        invStore.remove(player);
        return true;
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
        impostorsAlive = impostors.size();
        
        for(Player impostor: impostors){
            impostorsKillFlags.put(impostor, true);
        }
    }

    public void reportCorpse(Player reporter){
        PlayerColor reporterColor = getPlayerColor(reporter);
        for(Player player: players){
            player.sendMessage(reporterColor.getChatColor() + reporter.getDisplayName() + ChatColor.WHITE + " reported a Corpse");
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
        player.getInventory().setArmorContents(color.getArmor());
    }
    
    public boolean areColorsSelected(){
        for(Player player: players){
            if(playersColor.get(player) == null)
                return false;
        }
        return true;
    }
    
    public void startVoting(){
        voteSystem = new VoteSystem(this.getAlivePlayers(),this);
        
        try{
        for(Player player: players){
            if(isAlive(player))
                player.openInventory(voteSystem.getInventory());
            
        }
        }catch(NullPointerException e){
            TheImpostor.plugin.getLogger().log(Level.SEVERE, "Error");
        }
        
        BukkitTask task = new VoteTimer(this.voteTime, this).runTaskTimer(TheImpostor.plugin, 10L, 20L);
    }
    
    public void stopVote(){
        for(Player player: players)
            player.closeInventory();
        
        if(voteSystem == null){
            TheImpostor.plugin.getLogger().log(Level.SEVERE,"Vote System is null");
            return;
        }
        
        for(Player player: players){
            player.sendMessage(ChatColor.GREEN + "----------Votes----------");
            for(Player playerVoted: voteSystem.getPlayersInVote()){               
                player.sendMessage(voteSystem.getVotersString(playerVoted));
            }            
        }
        Player mostVoted = voteSystem.getMostVoted();
        if(mostVoted == null){
            for(Player player: this.getPlayers()){
                player.sendMessage("No one was ejected");
            }
            return;
        }
        String ejectMessage;
        if(isImpostor(mostVoted)){
            ejectMessage = "was the impostor";
            impostorsAlive--;
        }
        else
            ejectMessage = "was not the impostor";
        
        for(Player player: this.getPlayers()){
            player.sendMessage("The player " + mostVoted.getName() + " was the most voted and " + ejectMessage);
        }
        
        GameUtils.ejectPlayer(mostVoted, this);
        if(this.noImpostorsRemaining())
            endGame(false);
    }
    
    public boolean isImpostor(Player player){
        return impostors.contains(player);
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

    public VoteSystem getVoteSystem() {
        return voteSystem;
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
    
    /**
     * 
     * @param color The color of the player
     * @return The player found, if player not found returns null
     */
    public Player getPlayer(PlayerColor color){

        for(Player player: playersColor.keySet()){
            if(playersColor.get(player).equals(color))
                return player;
        }
        
        return null;
    }
    
    public boolean isColorSelected(PlayerColor color){
        if(getPlayer(color) == null)
            return false;
        return true;
    }
    
    public boolean noImpostorsRemaining(){
        return impostorsAlive == 0;
    }
    
    public void endGame(boolean impostorsWon){
  
        if(impostorsWon){
            for(Player player: crew){        
                player.sendTitle(ChatColor.RED + "Defeat", "", 20, 70, 20);
            }
            for(Player player: impostors){
                player.sendTitle(ChatColor.BLUE + "Winners", "", 20,70,20);
            }
            String impostorsString = getImpostorsString();
            for(Player player: players){
                player.sendMessage(ChatColor.GREEN + "------WINNERS------");
                player.sendMessage(impostorsString);
            }
            
        }else{
            for(Player player: crew){
                player.sendTitle(ChatColor.BLUE + "Winners", "", 20,70,20);
            }
            for(Player player: impostors){        
                player.sendTitle(ChatColor.RED + "Defeat", "", 20, 70, 20);
            }
            
            String crewString = getCrewString();
            for(Player player: players){
                player.sendMessage(ChatColor.GREEN + "------WINNERS------");
                player.sendMessage(crewString);
            }            
        }
        removeAllPlayers();
        started = false;
    }
    
    private String getImpostorsString(){
        String impostors = ChatColor.RED + "Impostors: ";
        for(Player impostor: this.impostors){
            PlayerColor color = getPlayerColor(impostor);
            impostors += color.getChatColor() + impostor.getName() + " ";
        }
        
        return impostors;
    }
    
    private String getCrewString(){
        String crew = ChatColor.BLUE + "Crew: ";
        for(Player crewmate: this.crew){
            PlayerColor color = getPlayerColor(crewmate);
            crew += color.getChatColor() + crewmate.getName() + " ";
        }
        
        return crew;
    }
    public int getAlivePlayersCount(){
        int alivePlayers = 0;
        for(Entry<Player,Boolean> entry: aliveMap.entrySet()){
            if(entry.getValue())
                alivePlayers++;
        }
        return alivePlayers;
    }
    
    public int getCrewAlive(){
        return getAlivePlayersCount() - impostorsAlive;
    }
    
    public boolean noenoughCrew(){
        return this.getCrewAlive() == impostorsAlive ;
    }
    
    public void removeAllPlayers(){
        teleportAllToEndLocation();
        resetAllInventories();
        stopAllScoreboards();
        setVisibleAllPlayers();
        resetAllDisplayColors();
        
        players.clear();
        playersColor.clear();
        impostors.clear();
        crew.clear();
        aliveMap.clear();
        playerLocations.clear();
        invStore.clear();
    }
    
    public void setVisibleAllPlayers(){
        for(Player player: players){
            GameUtils.setPlayerVisible(player, this);
        }
    }
    
    public void resetAllDisplayColors(){
        for(Player player: players){
            resetDisplayColor(player);
        }
    }
    
    public void resetDisplayColor(Player player){
        player.setDisplayName(ChatColor.RESET + player.getName());
    }
    
    public void stopAllScoreboards(){
        for(Player player: players)
            player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
            
    }
    
    public void teleportAllToEndLocation(){
        for(Player player: players){
            player.teleport(playerLocations.get(player));
        }
    }
    public Set<Player> getDeadPlayers(){
        Set<Player> deadPlayers = new HashSet<>();
        for(Player player: players){
            if(!isAlive(player)){
                deadPlayers.add(player);
            }
        }
        
        return deadPlayers;
    }
    
    private void resetInventory(Player player){
        ItemStack[] previousInventoryContent = invStore.get(player);
        player.getInventory().setContents(previousInventoryContent);
        
    }
    
    private void resetAllInventories(){
        for(Player player: players)
            resetInventory(player);
        
    }
    
    public boolean started(){
        return started;
    }
    
    public int getKillTime(){
        return killTime;
    }
    public boolean canKill(Player player){
        if(!isImpostor(player))
            return false;
        
        return impostorsKillFlags.get(player);
    }
    
    public void setKillFlag(Player impostor, boolean canKill){        
        Boolean previousValue = impostorsKillFlags.put(impostor, canKill);
        if(canKill)
            impostor.sendMessage("You're able to kill!");
        if(previousValue == null)
            TheImpostor.plugin.getLogger().log(Level.SEVERE, "Player is not in impostors map");
    }
}
