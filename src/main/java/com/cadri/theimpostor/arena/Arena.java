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
import com.cadri.theimpostor.MessageKey;
import com.cadri.theimpostor.TheImpostor;
import com.cadri.theimpostor.game.CrewTask;
import com.cadri.theimpostor.game.GameScoreboard;
import com.cadri.theimpostor.game.ItemOptions;
import com.cadri.theimpostor.game.PlayerColor;
import com.cadri.theimpostor.game.TaskTimer;
import com.cadri.theimpostor.game.VoteStartTimer;
import com.cadri.theimpostor.game.VoteSystem;
import com.cadri.theimpostor.game.VoteTimer;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
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
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.golde.bukkit.corpsereborn.CorpseAPI.CorpseAPI;
import org.golde.bukkit.corpsereborn.nms.Corpses.CorpseData;
import org.bukkit.map.MapView;

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
    private List<Location> playerSpawnPoints;
    public ArenaState state;
    private int timeToVote;
    private int voteTime;
    private int impostorsAlive;
    private int killTime;
    private int impostorsNumber;
    private int playerTasksNumber;
    private boolean enabled;
    private List<Player> crew;
    private List<Player> impostors;
    private Map<Player,Boolean> aliveMap;
    private Map<Player,PlayerColor> playersColor = new HashMap<>();
    private Map<Player,Location> playerLocations = new HashMap<>();
    private Map<Player,ItemStack[]> invStore = new HashMap<>();
    private Map<Player,Boolean> impostorsKillFlags = new HashMap<>(); 
    private List<CorpseData> corpses;
    private List<CrewTask> tasks;
    private Map<Player, List<CrewTask>> playerTasks;
    private File fileSettings;
    private FileConfiguration yamlSettings;
    //private Scoreboard board;
    private VoteSystem voteSystem = null;
    private Map<Player,TaskTimer> taskTimers = new HashMap<>();
    private Block emergencyMeetingBlock;
    private GameScoreboard board;
    private BossBar taskProgressBar = Bukkit.createBossBar(LanguageManager.getTranslation(MessageKey.TASK_PROGRESS_BAR), BarColor.GREEN, BarStyle.SOLID); 
    
    public Arena(String name, int maxPlayers, int minPlayers, Location lobby){
        this(name, maxPlayers, minPlayers, lobby, new ArrayList<>(), new ArrayList<>(), null, false, 0);
    }
    public Arena(String name, int maxPlayers, int minPlayers, Location lobby, List<Location> spawnLocations, List<CrewTask> tasks, Block emergencyMeetingBlock, boolean enabled, int playerTasksNumber) {
        this.name = name;
        this.maxPlayers = maxPlayers;
        this.minPlayers = minPlayers;
        this.players = new ArrayList<>();
        this.lobby = lobby;
        this.playerSpawnPoints = spawnLocations;
        this.state = ArenaState.WAITING_FOR_PLAYERS;
        this.timeToVote = 30;
        this.voteTime = 30;
        this.killTime = 10;
        this.impostorsNumber = 1;
        this.enabled = enabled;
        this.crew = new ArrayList<>();
        this.impostors = new ArrayList<>();
        this.impostorsAlive = 0;
        this.playerTasksNumber = playerTasksNumber;
        this.aliveMap = new HashMap<>();
        this.corpses = new ArrayList<>();
        this.tasks = tasks;
        this.emergencyMeetingBlock = emergencyMeetingBlock;
        this.playerTasks = new HashMap<>();
        this.board = new GameScoreboard(TheImpostor.pluginTitle, ChatColor.WHITE);
        this.fileSettings = new File(TheImpostor.plugin.getDataFolder() + File.separator + name + File.separator + "arena_settings.yml");
        this.yamlSettings = YamlConfiguration.loadConfiguration(fileSettings);   
        makeScoreBoard();
        taskProgressBar.setProgress(0);
    }

    public void initCountDown() {
        for (Player player : players) {
            player.sendMessage(LanguageManager.getTranslation(MessageKey.ARENA_READY));
        }
        BukkitTask countdown = new ArenaTimer(10, this).runTaskTimer(TheImpostor.plugin, 10L, 20L);

    }

    private void makeScoreBoard(){
        board.put(LanguageManager.getTranslation(MessageKey.ARENA), name);
        board.put(LanguageManager.getTranslation(MessageKey.MIN_PLAYERS), minPlayers);
        board.put(LanguageManager.getTranslation(MessageKey.MAX_PLAYERS), maxPlayers);
        board.put(LanguageManager.getTranslation(MessageKey.PLAYERS_NUMBER), players.size());
    }
    public Scoreboard getBoard() {
        return board.getScoreboard();
    }

    public void addPlayer(Player player) {
        players.add(player);
        playerLocations.put(player, player.getLocation());
        board.put(LanguageManager.getTranslation(MessageKey.PLAYERS_NUMBER), players.size());
        invStore.put(player, player.getInventory().getContents());
        player.getInventory().clear();
        player.setScoreboard(board.getScoreboard());
        player.getInventory().addItem(ItemOptions.CHOOSE_COLOR.getItem());
        
        if(players.size() == minPlayers)
            this.initCountDown();
    }

    public boolean removePlayer(Player player) {
        if (! players.remove(player))
            return false;
        
        board.put(LanguageManager.getTranslation(MessageKey.PLAYERS_NUMBER), players.size());
        GameUtils.setPlayerVisible(player, this);
        player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
        playersColor.remove(player);
        resetDisplayColor(player);
        aliveMap.remove(player);
        if(this.isImpostor(player))
            impostors.remove(player);
        else
            crew.remove(player);
        player.teleport(playerLocations.get(player));
        playerLocations.remove(player);
        resetInventory(player);
        invStore.remove(player);
        taskProgressBar.removePlayer(player);
        return true;
    }

    public void addCorpse(CorpseData corpse){
        corpses.add(corpse);
    }
    public void startGame() {
        for (Player player : this.getPlayers()) {
            player.sendMessage(LanguageManager.getTranslation(MessageKey.ARENA_GAME_START));
        }
        setRoles();

        for (Player player : players) {
            teleportToSpawnPoint(player);
            player.getInventory().remove(ItemOptions.CHOOSE_COLOR.getItem());
        }

        for (Player impostor : impostors) {
            String title = LanguageManager.getTranslation(MessageKey.IMPOSTOR_TITLE);
            String subtitle = LanguageManager.getTranslation(MessageKey.IMPOSTOR_SUBTITLE);
            impostor.sendTitle(title, subtitle, 4, 40, 5);
        }

        for (Player crewmate : crew) {
            String title = LanguageManager.getTranslation(MessageKey.CREWMATE_TITLE);
            String subtitle = LanguageManager.getTranslation(MessageKey.CREWMATE_SUBTITLE);
            crewmate.sendTitle(title, subtitle, 5, 40, 5);
            taskProgressBar.addPlayer(crewmate);
        }
        
        GameUtils.setInventoryImpostors(impostors);
        setAliveAll();
        
        if(! areColorsSelected())
            GameUtils.selectRandomColors(playersColor, players, this);
        
        playerTasks = GameUtils.assignTasks(players, tasks, playerTasksNumber);
        putMapsToPlayers();
        //showColorsOnBoard();
        state = ArenaState.IN_GAME;
    }

    private void setAliveAll(){
        for(Player player: players){
            aliveMap.put(player, true);
        }
    }
    
    public void setRoles() {
        crew.addAll(players);
        for(int i = 1; i <= impostorsNumber; i++)
            impostors.add(GameUtils.chooseImpostor(crew));
        impostorsAlive = impostors.size();
        
        for(Player impostor: impostors){
            impostorsKillFlags.put(impostor, true);
        }
    }

    public void reportCorpse(Player reporter, CorpseData corpseReported){
        PlayerColor reporterColor = getPlayerColor(reporter);
        for(Player player: players){
            String corpseName = corpseReported.getCorpseName();
            player.sendMessage(LanguageManager.getTranslation(MessageKey.PLAYER_REPORT_CORPSE, reporterColor.getChatColor() + reporter.getDisplayName() + ChatColor.WHITE,getPlayerColor(corpseName).getChatColor() + corpseName));
            player.sendTitle(LanguageManager.getTranslation(MessageKey.DEAD_BODY_REPORTED), LanguageManager.getTranslation(MessageKey.VOTE_STARTED), 20, 70, 20);
            teleportToSpawnPoint(player);
            player.sendMessage(LanguageManager.getTranslation(MessageKey.VOTE_START_TIME, timeToVote));
        }
        for(CorpseData corpse: corpses){
            CorpseAPI.removeCorpse(corpse);
        }
        
        BukkitTask countdown = new VoteStartTimer(timeToVote, this).runTaskTimer(TheImpostor.plugin, 10L, 20L);
        state = ArenaState.VOTING;
    }
    
    public void setPlayerColor(Player player, PlayerColor color){
        playersColor.put(player,color);
        player.getInventory().setArmorContents(color.getArmor());
        player.setDisplayName(color.getChatColor() + player.getDisplayName() + ChatColor.RESET);
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
        state = ArenaState.IN_GAME;
        for(Player player: players)
            player.closeInventory();
        
        if(voteSystem == null){
            TheImpostor.plugin.getLogger().log(Level.SEVERE,"Vote System is null");
            return;
        }
        
        for(Player player: players){
            player.sendMessage(LanguageManager.getTranslation(MessageKey.VOTES));
            for(Player playerVoted: voteSystem.getPlayersInVote()){               
                player.sendMessage(voteSystem.getVotersString(playerVoted));
            }            
        }
        Player mostVoted = voteSystem.getMostVoted();
        if(mostVoted == null){
            for(Player player: this.getPlayers()){
                player.sendMessage(LanguageManager.getTranslation(MessageKey.NOBODY_EJECTED));
            }
            return;
        }
        String ejectMessage;
        if(isImpostor(mostVoted)){
            ejectMessage = LanguageManager.getTranslation(MessageKey.IMPOSTOR_EJECTED_MESSAGE, mostVoted.getDisplayName());
            impostorsAlive--;
        }
        else
            ejectMessage = LanguageManager.getTranslation(MessageKey.CREWMATE_EJECTED_MESSAGE, mostVoted.getDisplayName());
        
        for(Player player: this.getPlayers()){
            player.sendMessage(ejectMessage);
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
    
    public PlayerColor getPlayerColor(String playerName){
        Player player = null;
        for(Player playerInGame: players){
            if(playerInGame.getName().equals(playerName)){
                player = playerInGame;
                break;
            }
        }
        
        if(player == null)
            return null;
        
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
        String winnersMessage = LanguageManager.getTranslation(MessageKey.WINNERS);
        String defeatMessage = LanguageManager.getTranslation(MessageKey.DEFEAT);
        String winnersListTitle = LanguageManager.getTranslation(MessageKey.WINNERS_LIST_TITLE);
        if(impostorsWon){
            for(Player player: crew){        
                player.sendTitle(defeatMessage, "", 20, 70, 20);
            }
            for(Player player: impostors){
                player.sendTitle(winnersMessage, "", 20,70,20);
            }
            String impostorsString = getImpostorsString();
            for(Player player: players){
                player.sendMessage(winnersListTitle);
                player.sendMessage(impostorsString);
            }
            
        }else{
            for(Player player: crew){
                player.sendTitle(winnersMessage, "", 20,70,20);
            }
            for(Player player: impostors){        
                player.sendTitle(defeatMessage, "", 20, 70, 20);
            }
            
            String crewString = getCrewString();
            for(Player player: players){
                player.sendMessage(winnersListTitle);
                player.sendMessage(crewString);
            }            
        }
        removeAllPlayers();
        taskProgressBar.setProgress(0);
        state = ArenaState.WAITING_FOR_PLAYERS;
    }
    
    private String getImpostorsString(){
        String impostors = LanguageManager.getTranslation(MessageKey.IMPOSTORS);
        for(Player impostor: this.impostors){
            PlayerColor color = getPlayerColor(impostor);
            impostors += color.getChatColor() + impostor.getName() + " ";
        }
        
        return impostors;
    }
    
    private String getCrewString(){
        String crew = LanguageManager.getTranslation(MessageKey.CREW);
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
        taskProgressBar.removeAll();
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
        player.setDisplayName(player.getName());
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
        return state != ArenaState.WAITING_FOR_PLAYERS;
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
            impostor.sendMessage(LanguageManager.getTranslation(MessageKey.IMPOSTOR_ABLE_TO_KILL));
        if(previousValue == null)
            TheImpostor.plugin.getLogger().log(Level.SEVERE, "Player is not in impostors map");
    }
    
    public void addTask(CrewTask task){
        if(!isTaskValid(task))
            throw new IllegalArgumentException("Task invalid");
        tasks.add(task);
        
        try{
        saveConfig();
        }catch(IOException e){
            TheImpostor.plugin.getLogger().log(Level.SEVERE, e.getMessage());
        }
    }
    
    
    public boolean isTaskValid(CrewTask taskToAdd){
        for(CrewTask arenaTask: tasks){
            if(GameUtils.areEquals(taskToAdd.getLocation(), arenaTask.getLocation()))
                return false;
        }
        
        return true;
            
    }
    
    public List<CrewTask> getTasks(){
        return tasks;
    }
    
    public void putMapsToPlayers(){
        for (Player player : players) {
            ItemStack mapItem = new ItemStack(Material.FILLED_MAP);
            MapView view = Bukkit.createMap(lobby.getWorld());
            int centerX = lobby.getBlockX();
            int centerZ = lobby.getBlockZ();
            GameUtils.putTaskMarkers(view, getPlayerTasks(player), centerX, centerZ);
            view.setScale(MapView.Scale.CLOSE);
            view.setCenterX(centerX);
            view.setCenterZ(centerZ);
            view.setTrackingPosition(true);
            MapMeta mapMeta = (MapMeta) mapItem.getItemMeta();
            mapMeta.setMapView(view);
            mapItem.setItemMeta(mapMeta);
            player.getInventory().addItem(mapItem);
        }
        
    }
    
    public List<CrewTask> getPlayerTasks(Player player){
        return playerTasks.get(player);
    }
    
    public void saveConfig() throws IOException{
        yamlSettings.set("Name", name);
        yamlSettings.set("minPlayers", minPlayers);
        yamlSettings.set("maxPlayers", maxPlayers);
        yamlSettings.set("Lobby" + ".world", lobby.getWorld().getName());
        yamlSettings.set("Lobby" + ".x", lobby.getX());
        yamlSettings.set("Lobby" + ".y", lobby.getY());
        yamlSettings.set("Lobby" + ".z", lobby.getZ());
        yamlSettings.set("enabled", enabled);
        yamlSettings.set("player_tasks_number", playerTasksNumber);
        List<String> spawnLocations = new ArrayList<>();
        for(Location spawn: playerSpawnPoints){
            spawnLocations.add(Serializer.serializeLocation(spawn));
        }
        yamlSettings.set("player_spawn_points", spawnLocations);
        
        if(emergencyMeetingBlock != null){
            Location embLoc = emergencyMeetingBlock.getLocation();
            yamlSettings.set("emergency_meeting_block_location.world", embLoc.getWorld().getName());
            yamlSettings.set("emergency_meeting_block_location.x", embLoc.getX());
            yamlSettings.set("emergency_meeting_block_location.y", embLoc.getY());
            yamlSettings.set("emergency_meeting_block_location.z", embLoc.getZ());
        }
        for(CrewTask task: tasks){
            String taskName = task.getName();
            String key = "tasks." + taskName;
            
            Location loc = task.getLocation();
            yamlSettings.set(key + ".location.world", loc.getWorld().getName());
            yamlSettings.set(key + ".location.x", loc.getX());
            yamlSettings.set(key + ".location.y", loc.getY());
            yamlSettings.set(key + ".location.z", loc.getZ());
            
            yamlSettings.set(key + ".time_to_complete", task.getTimeToComplete());
        }
        
        yamlSettings.save(fileSettings);
    }
    
    public void addTaskTimer(Player player, TaskTimer timer){
        taskTimers.put(player, timer);
    }
    
    public TaskTimer removeTaskTimer(Player player){
        return taskTimers.remove(player);
    }
    
    public TaskTimer getCurrentTaskTimer(Player player){
        return taskTimers.get(player);
    }
    
    public boolean isCompletingATask(Player player){
        return taskTimers.containsKey(player);
    }
    
    public boolean tasksAreCompleted(){
        for(Entry<Player,List<CrewTask>> entry: playerTasks.entrySet()){
            if(isImpostor(entry.getKey()))
                continue;
            for(CrewTask task: entry.getValue()){
                if(!task.isCompleted())
                    return false;
            }
        }
        
        return true;
    }
    
    public void addSpawnLocation(Location spawn){
        try {
            playerSpawnPoints.add(spawn);
            saveConfig();
        } catch (IOException ex) {
            Logger.getLogger(Arena.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public List<Location> getPlayerSpawnPoints() {
        return playerSpawnPoints;
    }
    
    private void teleportToSpawnPoint(Player player){
        player.teleport(playerSpawnPoints.get(players.indexOf(player)));
    }

    public void setEmergencyMeetingBlock(Block emergencyMeetingBlock) {
        try {
            this.emergencyMeetingBlock = emergencyMeetingBlock;
            saveConfig();
        } catch (IOException ex) {
            Logger.getLogger(Arena.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public boolean isEmergencyMeetingBlock(Block block){
        if(emergencyMeetingBlock == null)
            return false;
        
        return block.equals(emergencyMeetingBlock);
    }
    
    public World getWorld(){
        return playerSpawnPoints.get(0).getWorld();
    }
    
    public void startEmergencyMeeting(Player whoStartedMeeting){
        for(Player player: players){
            player.sendMessage(LanguageManager.getTranslation(MessageKey.EMERGENCY_MEETING_START, whoStartedMeeting.getName()));
            String emTitle = LanguageManager.getTranslation(MessageKey.EMERGENCY_MEETING_TITLE);
            String emSubtitle = LanguageManager.getTranslation(MessageKey.EMERGENCY_MEETING_SUBTITLE);
            player.sendTitle(emTitle, emSubtitle, 20, 70, 20);
            teleportToSpawnPoint(player);
            player.sendMessage(LanguageManager.getTranslation(MessageKey.VOTE_START_TIME, timeToVote));
        }
        for(CorpseData corpse: corpses){
            CorpseAPI.removeCorpse(corpse);
        }
        
        BukkitTask countdown = new VoteStartTimer(timeToVote, this).runTaskTimer(TheImpostor.plugin, 10L, 20L);
        state = ArenaState.VOTING;        
    }
    
    public boolean areComponentsSetted(){
        return areAllTasksSetted() && areSpawnPointsSetted() && lobby != null && this.emergencyMeetingBlock != null && playerTasksNumber != 0;
    }
    
    public boolean areAllTasksSetted(){
        return tasks.size() >= maxPlayers;
    }
    
    public boolean areSpawnPointsSetted(){
        return playerSpawnPoints.size() == maxPlayers;
    }
    
    public boolean isEnabled(){
        return enabled;
    }
    
    public void enable() throws ArenaNotReadyException{
        if(areComponentsSetted())
            this.enabled = true;
        else
            throw new ArenaNotReadyException(this);
    }
    
    public double getPercentofTasksCompleted(){
        double totalTasks = 0;
        double tasksCompleted = 0;
        
        for(Player crewmate: crew){
            tasksCompleted += getTasksCompletedNumber(crewmate);
            totalTasks += getPlayerTasks(crewmate).size();
        }
        
        return tasksCompleted / totalTasks; 
    }
    
    public void updateTasksProgressBar(){
        taskProgressBar.setProgress(getPercentofTasksCompleted());
    }
    
    public int getTasksCompletedNumber(Player crewmate){
        int tasksCompleted = 0;
        for(CrewTask task: getPlayerTasks(crewmate)){
            if(task.isCompleted())
                tasksCompleted++;
        }
        
        return tasksCompleted;
    }

    public void setPlayerTasksNumber(int playerTasksNumber) {
        try {
            this.playerTasksNumber = playerTasksNumber;
            saveConfig();
        } catch (IOException ex) {
            Logger.getLogger(Arena.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
