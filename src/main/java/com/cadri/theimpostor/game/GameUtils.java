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
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Stack;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapCursor;
import org.bukkit.map.MapCursor.Type;
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
        }
    }

    public static void makePhantom(Player player, Arena arena) {
        if(arena.noenoughCrew()){
            arena.endGame(true);
            return;
        }
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
                playerInArena.sendMessage("You're not in arena");
            }

        }
        if(arena.noenoughCrew()){
            arena.endGame(true);
        }
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
        PlayerColor[] playerColors = PlayerColor.values();
        Inventory guiColors = Bukkit.createInventory(null, 9, "Choose a color");
        for(PlayerColor color: playerColors){
            if(!isColorSelected(color, arena))
                guiColors.addItem(color.getItem());
        }
        
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
            List<CrewTask> playerTasks = new ArrayList<>(tasks);
            int tasksToRemove = tasks.size() - tasksNumber;
            for(int i = 1; i <= tasksToRemove; i++){
                playerTasks.remove(random.nextInt(playerTasks.size()));
            }
            
            tasksMap.put(player, playerTasks);
        }
        
        return tasksMap;
    }
    
    public static void putTaskMarkers(MapView mapView, List<CrewTask> taskList, int centerX, int centerY){
        mapView.addRenderer(new MapRenderer() {
            boolean done = false;
            @Override
            public void render(MapView mv, MapCanvas mc, Player player) {
                
                if (!done) {
                    for (CrewTask task : taskList) {
                        Location loc = task.getLocation();
                        MapCursor cursor = new MapCursor((byte) (loc.getBlockX() - centerX), (byte) (loc.getBlockZ() - centerY), (byte) 8, Type.GREEN_POINTER, true);
                        mc.getCursors().addCursor(cursor);
                    }
                    done = true;
                }
            }
        });
    }
}
