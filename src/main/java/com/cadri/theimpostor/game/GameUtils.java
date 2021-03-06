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
import com.sun.prism.paint.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Stack;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapCursor;
import org.bukkit.map.MapCursor.Type;
import org.bukkit.map.MapCursorCollection;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

/**
 *
 * @author cadri
 */
public class GameUtils {
    private static Random random = new Random();
    
    public static Player chooseImpostor(List<Player> players) {
        int index = random.nextInt(players.size());
        
        return players.remove(index);
    }

    public static void setInventoryImpostors(List<Player> impostors) {
        for (Player impostor : impostors) {
            PlayerInventory impostorInventory = impostor.getInventory();
            impostorInventory.setItem(0, ItemOptions.KILL_PLAYER.getItem());
            impostorInventory.setItem(1, ItemOptions.SABOTAGE.getItem());
        }
    }

    /**
     * 
     * @param player
     * @param arena
     * @return true if the game is over or false if it is not
     */
    public static boolean makePhantom(Player player, Arena arena) {
        Map<Player, Boolean> aliveMap = arena.getAliveMap();
        aliveMap.put(player, false);

        for (Player playerInArena : arena.getPlayers()) {
            Boolean isAlive = aliveMap.get(playerInArena);
            if (isAlive != null) {
                if (isAlive) {
                    playerInArena.hidePlayer(TheImpostor.plugin, player);
                } else {
                    player.showPlayer(TheImpostor.plugin, playerInArena);
                   
                }
            }else{
                playerInArena.sendMessage(LanguageManager.getTranslation(MessageKey.PLAYER_NOT_IN_ARENA));
            }

        }
        if(arena.noenoughCrew()){
            arena.endGame(true);
            return true;
        }else
            return false;
    }
    
    public static void setPlayerVisible(Player deadPlayer, Arena arena){
        for(Player player: arena.getPlayers()){
            player.showPlayer(TheImpostor.plugin, deadPlayer);
        }
    }
    
    public static void ejectPlayer(Player player, Arena arena){
        makePhantom(player,arena);
        player.sendTitle("You were ejected", "", 20, 70, 20);
    }
    
    public static Inventory getGUIChoiceColors(Arena arena){
        List<PlayerColor> availableColors = new ArrayList<>();        
        for(PlayerColor color: PlayerColor.values()){
            if(!isColorSelected(color, arena))
                availableColors.add(color);
        }

            
        Inventory guiColors = Bukkit.createInventory(null, getInventorySize(availableColors.size()), LanguageManager.getTranslation(MessageKey.CHOOSE_COLOR));     
        availableColors.forEach(availableColor -> {
            guiColors.addItem(availableColor.getItem());
        });
        
        return guiColors;
    }
    
    public static void selectRandomColors(Map<Player,PlayerColor> playersColor, List<Player> players, Arena arena){
        Stack<PlayerColor> availableColors = new Stack<>();
        for(PlayerColor color: PlayerColor.values()){
            if(! arena.isColorSelected(color))
                availableColors.push(color);
        }
 
        for(Player player: arena.getPlayers()){
            if(playersColor.get(player) == null){ //If it's not asigned
                //playersColor.put(player, availableColors.pop());
                arena.setPlayerColor(player, availableColors.pop());
            }
        }
    } 
    
    public static boolean isColorSelected(PlayerColor color, Arena arena){
        return arena.getPlayer(color) != null;
    }
    
    public static Map<Player,List<CrewTask>> assignTasks(List<Player> players, List<CrewTask> tasks, int tasksNumber){
        Map<Player,List<CrewTask>> tasksMap = new HashMap<>();
        for(Player player: players){
            List<CrewTask> playerTasks = copyTaskList(tasks);
            int tasksToRemove = tasks.size() - tasksNumber;
            for(int i = 1; i <= tasksToRemove; i++){
                playerTasks.remove(random.nextInt(playerTasks.size()));
            }
            
            tasksMap.put(player, playerTasks);
        }
        
        return tasksMap;
    }
    
    public static void putMarkers(MapView mapView, List<CrewTask> taskList, List<SabotageComponent> sabotages, int centerX, int centerY){
        mapView.addRenderer(new MapRenderer() {
            @Override
            public void render(MapView mv, MapCanvas mc, Player player) {
                MapCursorCollection cursors = new MapCursorCollection();
                
                for (CrewTask task : taskList) {
                    if (!task.isCompleted()) {
                        Location loc = task.getLocation();
                        MapCursor cursor = new MapCursor((byte) (loc.getBlockX() - centerX), (byte) (loc.getBlockZ() - centerY), (byte) 8, Type.GREEN_POINTER, true);
                        cursors.addCursor(cursor);
                        task.setMapCursor(cursor);
                    }
                }               
                                
                for(SabotageComponent sabotage: sabotages){
                    if(sabotage.isSabotaged()){
                        Block sabotageBlock = sabotage.getBlock();
                        MapCursor sabotageCursor = new MapCursor((byte) (sabotageBlock.getX() - centerX), (byte) (sabotageBlock.getZ() - centerY), (byte) 8, Type.RED_POINTER, true);
                        cursors.addCursor(sabotageCursor);             
                    }
                }
                
                mc.setCursors(cursors);
            }
        });
    }
    
    public static CrewTask getTaskByLocation(Location location, List<CrewTask> tasks){
        for(CrewTask task: tasks){
            Location loc = task.getLocation();
            if(areEquals(loc, location))
                return task;
        }
        return null;
    }
    
    public static boolean areEquals(Location loc1, Location loc2){
        return loc1.getBlockX() == loc2.getBlockX() && loc1.getBlockY() == loc2.getBlockY() && loc1.getBlockZ() == loc2.getBlockZ();
    }
    
    public static List<CrewTask> copyTaskList(List<CrewTask> taskList){
        List<CrewTask> newTaskList = new ArrayList<CrewTask>();
        for(CrewTask task: taskList){
            CrewTask taskCopy = new CrewTask(task.getName(), task.getLocation(), task.getTimeToComplete());
            newTaskList.add(taskCopy);
        }
        
        return newTaskList;
    }
    
    public static Inventory getSabotagesGUI(List<SabotageComponent> sabotages){
        Inventory sabotagesGUI = Bukkit.createInventory(null, 9, LanguageManager.getTranslation(MessageKey.SABOTAGE_GUI_TITLE));
        for(SabotageComponent sabotage: sabotages){
            sabotagesGUI.addItem(sabotage.getItem());
        }
        
        return sabotagesGUI;
    }
    
    public static int getInventorySize(int itemsNumber){
        if(itemsNumber % 9 == 0)
            return itemsNumber;
        else{
            int a = (itemsNumber / 9) + 1;
            return 9 * a;
        }
    }
}
