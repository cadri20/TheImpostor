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

import com.cadri.theimpostor.TheImpostor;
import com.cadri.theimpostor.arena.Arena;
import com.cadri.theimpostor.arena.ArenaUtils;
import com.cadri.theimpostor.game.GameUtils;
import com.cadri.theimpostor.game.ItemOptions;
import com.cadri.theimpostor.game.PlayerColor;
import com.cadri.theimpostor.game.VoteSystem;
import com.google.common.util.concurrent.AbstractScheduledService.Scheduler;
import java.util.logging.Level;
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
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.golde.bukkit.corpsereborn.CorpseAPI.CorpseAPI;
import org.golde.bukkit.corpsereborn.CorpseAPI.events.CorpseClickEvent;
import org.golde.bukkit.corpsereborn.nms.Corpses.CorpseData;

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
            killed.sendTitle("You've been killed", "Make tasks", 20, 70, 20);
            GameUtils.makePhantom(killed, arena);
            CorpseData corpse = CorpseAPI.spawnCorpse(killed, killed.getLocation());
            arena.addCorpse(corpse);
            arena.setKillFlag(killer, false);
            BukkitScheduler scheduler = TheImpostor.plugin.getServer().getScheduler();
            int killTime = arena.getKillTime();
            scheduler.runTaskLater(TheImpostor.plugin, () -> {
                arena.setKillFlag(killer, true);                                
            }, killTime * 20);
            killer.sendMessage("Now you have to wait " + killTime + " seconds before killing again");
        }

    }

    @EventHandler
    public void onCorpseClick(CorpseClickEvent evt){
        Player whoClicked = evt.getClicker();
        Arena arena = ArenaUtils.whereArenaIs(whoClicked);
        if(arena == null)
            return;        

        ItemStack itemInHand = whoClicked.getInventory().getItemInMainHand();
        if(!ItemOptions.isItemOption(itemInHand))            
            arena.reportCorpse(whoClicked);
        
        evt.setCancelled(true);
    }
    
    @EventHandler
    public void onClickItem(PlayerInteractEvent evt){
        Player player = evt.getPlayer();
        Arena arena = ArenaUtils.whereArenaIs(player);
        if(arena == null)
            return;
        
        Action action = evt.getAction();
        if(action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK){
            if( !evt.getItem().equals(ItemOptions.CHOOSE_COLOR.getItem()) )
                return;
            
            player.openInventory(GameUtils.getGUIChoiceColors(arena));
            evt.setCancelled(true);
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
        
        VoteSystem vs = arena.getVoteSystem();
        if(vs != null){
            if(evt.getInventory().equals(vs.getInventory())){ //If player selected skip vote
                if(itemClicked.getItemMeta().getDisplayName().equals(vs.getSkipVoteText())){
                    vs.skipVote(player);
                    player.sendMessage("You've skipped the vote");
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
            player.sendMessage("You chose " + playerColor.getName());
            player.closeInventory();  
            evt.setCancelled(true);
            return;
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
    
}
