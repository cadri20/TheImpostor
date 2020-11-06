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
import com.sun.javafx.scene.text.HitInfo;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
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
        
        if(itemInHand.equals(ItemOptions.KILL_PLAYER.getItem())){
            killed.sendTitle("You've been killed", "Make tasks", 20, 70, 20);
            GameUtils.makePhantom(killed, arena);
            CorpseData corpse = CorpseAPI.spawnCorpse(killed, killed.getLocation());
            arena.addCorpse(corpse);
        }

    }

    @EventHandler
    public void onCorpseClick(CorpseClickEvent evt){
        Arena arena = ArenaUtils.whereArenaIs(evt.getClicker());
        if(arena == null)
            return;
        
        arena.corpseReported();
    }
    
    @EventHandler
    public void onClickItem(PlayerInteractEvent evt){
        Player player = evt.getPlayer();
        Arena arena = ArenaUtils.whereArenaIs(player);
        if(arena == null)
            return;
        
        if(evt.getAction() == Action.RIGHT_CLICK_AIR){
            if( !evt.getItem().equals(ItemOptions.CHOOSE_COLOR.getItem()) )
                return;
            
            player.openInventory(GameUtils.getGUIChoiceColors());
            
        }
            
    }
    
    @EventHandler
    public void onClickInventory(InventoryClickEvent evt){
        Player player = (Player) evt.getWhoClicked();
        Arena arena = ArenaUtils.whereArenaIs(player);
        if(arena == null)
            return;
        
        ItemStack itemClicked = evt.getCurrentItem();
        if(itemClicked.getType() == Material.AIR)
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
        if(inventorySource.equals(vs.getInventory()) || inventorySource.equals(GameUtils.getGUIChoiceColors())){
            evt.setCancelled(true);
        }
    }
/*    
    @EventHandler
    public void onDrag(InventoryDragEvent evt){
        Player player = (Player) evt.getWhoClicked();
        Arena arena = ArenaUtils.whereArenaIs(player);
        if(arena == null)
            return;
        
        evt.setCancelled(true);
    }*/
/*    
    @EventHandler
    public void onCloseInventory(InventoryCloseEvent evt){
        Player player = (Player) evt.getPlayer();
        Arena arena = ArenaUtils.whereArenaIs(player);
        if (arena == null)
            return;
        
        Inventory inventoryClosed = evt.getInventory();
        VoteSystem vs = arena.getVoteSystem();
        if(vs == null)
            return;
        
        if(inventoryClosed.equals(vs.getInventory())){
            vs.skipVote(player);
            player.sendMessage("You've skipped your vote");
        }
    }
    */
    public void onVote(VoteEvent evt){
        Player voted = evt.getVoted();
        
        evt.getArena().getVoteSystem().vote(voted, evt.getVoter());
    }
}
