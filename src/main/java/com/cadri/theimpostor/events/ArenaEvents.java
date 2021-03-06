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
package com.cadri.theimpostor.events;

import com.cadri.theimpostor.LanguageManager;
import com.cadri.theimpostor.MessageKey;
import com.cadri.theimpostor.TheImpostor;
import com.cadri.theimpostor.arena.Arena;
import com.cadri.theimpostor.arena.ArenaState;
import com.cadri.theimpostor.arena.ArenaUtils;
import com.cadri.theimpostor.game.CrewTask;
import com.cadri.theimpostor.game.GameUtils;
import com.cadri.theimpostor.game.ItemOptions;
import com.cadri.theimpostor.game.PlayerColor;
import com.cadri.theimpostor.game.SabotageComponent;
import com.cadri.theimpostor.game.TaskTimer;
import com.cadri.theimpostor.game.VoteSystem;
import com.google.common.util.concurrent.AbstractScheduledService.Scheduler;
import java.util.List;
import java.util.logging.Level;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.golde.bukkit.corpsereborn.CorpseAPI.CorpseAPI;
import org.golde.bukkit.corpsereborn.CorpseAPI.events.CorpseClickEvent;
import org.golde.bukkit.corpsereborn.nms.Corpses.CorpseData;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.inventory.PlayerInventory;

/**
 *
 * @author cadri
 */
public class ArenaEvents implements Listener {

    @EventHandler
    public void onKill(PlayerInteractEntityEvent evt) {

        Player killer = (Player) evt.getPlayer();

        Arena arena = ArenaUtils.whereArenaIs(killer);
        if (arena == null) {
            return;
        }
        Entity entity = evt.getRightClicked();
        if(!(entity instanceof Player))
            return;
        Player killed = (Player) entity;
        ItemStack itemInHand = killer.getInventory().getItemInMainHand();
        
        if(itemInHand.equals(ItemOptions.KILL_PLAYER.getItem()) && arena.canKill(killer)){
            String title = LanguageManager.getTranslation(MessageKey.PLAYER_KILLED_TITLE);
            String subtitle = LanguageManager.getTranslation(MessageKey.PLAYER_KILLED_SUBTITLE);
            killed.sendTitle(title, subtitle, 20, 70, 20);
            PlayerInventory inventory = killed.getInventory();
            ItemStack[] armor = inventory.getArmorContents();
            
            boolean gameOver = GameUtils.makePhantom(killed, arena);
            if (!gameOver) {
                CorpseData corpse = CorpseAPI.spawnCorpse(killed, killed.getLocation(), inventory.getContents(), armor[3], armor[2], armor[1], armor[0]);
                arena.addCorpse(corpse);
                arena.setKillFlag(killer, false);
                BukkitScheduler scheduler = TheImpostor.plugin.getServer().getScheduler();
                int killTime = arena.getKillTime();
                scheduler.runTaskLater(TheImpostor.plugin, () -> {
                    if (arena.started()) {
                        arena.setKillFlag(killer, true);
                    }
                }, killTime * 20);
                killer.sendMessage(LanguageManager.getTranslation(MessageKey.IMPOSTOR_KILL_COOLDOWN, killTime));
            }
        }

    }

    @EventHandler
    public void onCorpseClick(CorpseClickEvent evt){
        Player whoClicked = evt.getClicker();
        Arena arena = ArenaUtils.whereArenaIs(whoClicked);
        if(arena == null)
            return;        
        
        if(arena.isAlive(whoClicked)){
            CorpseData corpse = evt.getCorpse();
            ItemStack itemInHand = whoClicked.getInventory().getItemInMainHand();
            if (!ItemOptions.isItemOption(itemInHand))
                arena.reportCorpse(whoClicked, corpse);        
        }
       
        evt.setCancelled(true);
    }
    
    @EventHandler
    public void onClickItem(PlayerInteractEvent evt){
        Player player = evt.getPlayer();
        Arena arena = ArenaUtils.whereArenaIs(player);
        if(arena == null)
            return;
        
        Action action = evt.getAction();
        if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
            ItemStack item = evt.getItem();
            Block clickedBlock = evt.getClickedBlock();

            if (item != null) {
                if (item.equals(ItemOptions.CHOOSE_COLOR.getItem())) {
                    player.openInventory(GameUtils.getGUIChoiceColors(arena));
                    evt.setCancelled(true);
                } else if (item != null && item.equals(ItemOptions.SABOTAGE.getItem())) {
                    if(arena.canSabotage(player)){
                        player.openInventory(GameUtils.getSabotagesGUI(arena.getSabotages()));
                    }else
                        player.sendMessage(LanguageManager.getTranslation(MessageKey.CANT_SABOTAGE));
                    evt.setCancelled(true);
                }
            } else if (clickedBlock != null) {
                SabotageComponent sabotage = arena.getSabotage(clickedBlock);
                if (arena.isEmergencyMeetingBlock(clickedBlock)) {
                    if (arena.started()) {
                        if(arena.isEmergencyMeetingEnabled())
                            arena.startEmergencyMeeting(player);
                        else
                            player.sendMessage(LanguageManager.getTranslation(MessageKey.EMERGENCY_BLOCK_ENABLED_IN, arena.getEnableCount()));
                    }
                } else if (sabotage != null) {
                    arena.fixSabotage(sabotage);
                }
            }
        }

    }
    
    @EventHandler
    public void onClickInventory(InventoryClickEvent evt){
        Player player = (Player) evt.getWhoClicked();
        Arena arena = ArenaUtils.whereArenaIs(player);
        if(arena == null)
            return;
        
        ItemStack itemClicked = evt.getCurrentItem();
            
        if(itemClicked == null || itemClicked.getType() == Material.AIR)
            return;
        
        Material material = itemClicked.getType();
        
        if(material.equals(Material.LEATHER_HELMET) 
                || material.equals(Material.LEATHER_CHESTPLATE)
                || material.equals(Material.LEATHER_LEGGINGS)
                || material.equals(Material.LEATHER_BOOTS))
            evt.setCancelled(true); // his prevents players from removing their armor        
        VoteSystem vs = arena.getVoteSystem();
        if(vs != null){
            if(evt.getInventory().equals(vs.getInventory())){ //If player selected skip vote
                if(itemClicked.getItemMeta().getDisplayName().equals(vs.getSkipVoteText())){
                    vs.skipVote(player);
                    player.sendMessage(LanguageManager.getTranslation(MessageKey.VOTE_SKIPPED));
                    player.closeInventory();
                    evt.setCancelled(true);
                    return;
                }
                PlayerColor colorSelected = PlayerColor.getPlayerColor(itemClicked.getType());
                if(colorSelected != null){
                    vs.vote(colorSelected,player);
                    player.closeInventory();    
                    evt.setCancelled(true);
                    return;
                }else{
                    TheImpostor.plugin.getLogger().log(Level.SEVERE, "colorSelected is null and type of item is " + itemClicked.getType().name());
                    return;
                }
            }
        }
             
        PlayerColor playerColor = PlayerColor.getPlayerColor(itemClicked);
        if(playerColor != null){

            arena.setPlayerColor(player, playerColor);
            player.sendMessage(LanguageManager.getTranslation(MessageKey.COLOR_CHOOSEN, playerColor.getChatColor() + playerColor.getName()));
            player.closeInventory();  
            evt.setCancelled(true);
            return;
        }
      
        SabotageComponent sabotageChoosen = arena.getSabotage(itemClicked);
        if(sabotageChoosen != null){
            if(!sabotageChoosen.isSabotaged()){
                arena.sabotage(sabotageChoosen);
                arena.setSabotageFlag(player, false);
                Bukkit.getScheduler().runTaskLater(TheImpostor.plugin, () -> {
                    arena.setSabotageFlag(player, true);
                }, arena.getSabotageTime() * 20);

            }
            player.closeInventory();
            evt.setCancelled(true);       

        }
    }
    
    @EventHandler
    public void onMoveItemFromInventory(InventoryMoveItemEvent evt){
        Inventory inventorySource = evt.getInitiator();
        Inventory inventoryDestiny = evt.getDestination();
        
        if(! (inventoryDestiny.getHolder() instanceof Player))
            return;
        
        Player player = (Player) inventoryDestiny.getHolder();
        Arena arena = ArenaUtils.whereArenaIs(player);
        
        if(arena == null)
            return;
        
        ItemStack item = evt.getItem();
        Material material = item.getType();
        if(material.equals(Material.LEATHER_HELMET) 
                || material.equals(Material.LEATHER_CHESTPLATE)
                || material.equals(Material.LEATHER_LEGGINGS)
                || material.equals(Material.LEATHER_BOOTS))
            evt.setCancelled(true); // his prevents players from removing their armor
        VoteSystem vs = arena.getVoteSystem();
        if(inventorySource.equals(vs.getInventory()) || inventorySource.equals(GameUtils.getGUIChoiceColors(arena))){
            evt.setCancelled(true);
        }
    }
    
    @EventHandler
    public void onDropItem(PlayerDropItemEvent evt){
        Player player = evt.getPlayer();
        if(ArenaUtils.whereArenaIs(player) == null)
            return;
        
        Item itemDropped = evt.getItemDrop();
        if(ItemOptions.isItemOption(itemDropped.getItemStack()))
            evt.setCancelled(true);
    }
    
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent evt){
        Player player = evt.getPlayer();
        Arena arena = ArenaUtils.whereArenaIs(player);
        if(arena == null || !arena.started())
            return;
        
        if(arena.state == ArenaState.VOTING){
            evt.setCancelled(true);
        }
        
        if(arena.isImpostor(player))
            return;
        
        List<CrewTask> playerTasks = arena.getPlayerTasks(player);
        Location locTo = evt.getTo();
        Location locFrom = evt.getFrom();
        if(GameUtils.areEquals(locTo, locFrom)) //This avoid run the event when the player does minimal moves
            return;
        
        CrewTask taskAtPlayerPositionTo = GameUtils.getTaskByLocation(evt.getTo(),playerTasks);
        CrewTask taskAtPlayerPositionFrom = GameUtils.getTaskByLocation(evt.getFrom(),playerTasks);
        
        if(taskAtPlayerPositionTo != null){ // Player is entering 
            if (!taskAtPlayerPositionTo.isCompleted()) {
                TaskTimer timer = new TaskTimer(taskAtPlayerPositionTo, player, arena);
                timer.runTaskTimer(TheImpostor.plugin, 10L, 20L);
                arena.addTaskTimer(player, timer);
            }
        }else{
            if(taskAtPlayerPositionFrom != null && !taskAtPlayerPositionFrom.isCompleted()){ //Player is going out
                arena.removeTaskTimer(player).cancel(); //Stop the timer
            }
        }
    }
    
    
}
