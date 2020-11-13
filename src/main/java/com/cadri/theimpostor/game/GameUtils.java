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
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Stack;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.PlayerInventory;

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
    
    public static void ejectPlayer(Player player, Arena arena){
        makePhantom(player,arena);
        player.sendTitle("You were ejected", "", 20, 70, 20);
    }
    
    public static Inventory getGUIChoiceColors(){
        PlayerColor[] playerColors = PlayerColor.values();
        Inventory guiColors = Bukkit.createInventory(null, 9, "Choose a color");
        for(PlayerColor color: playerColors){
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
}
