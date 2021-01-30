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
package com.cadri.theimpostor.commands;

import com.cadri.theimpostor.LanguageManager;
import com.cadri.theimpostor.MessageKey;
import com.cadri.theimpostor.TheImpostor;
import com.cadri.theimpostor.arena.Arena;
import com.cadri.theimpostor.arena.ArenaManager;
import com.cadri.theimpostor.game.CrewTask;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author cadri
 */
public class AddTask implements SetupArenaCommand{

    @Override
    public void onCommand(Player player, Arena arena, String[] args) {
        if(args.length != 2){
            player.sendMessage(LanguageManager.getTranslation(MessageKey.INVALID_ARGUMENTS_NUMBER));
            return;
        }
        
        int taskDuration = 0;
        try{
            taskDuration = Integer.parseInt(args[1]);
        }catch(NumberFormatException e){
            player.sendMessage(LanguageManager.getTranslation(MessageKey.ARGUMENT_NOT_NUMBER));
            return;
        }
        
        Location playerLoc = player.getLocation();
        CrewTask task = new CrewTask(args[0], playerLoc, taskDuration);
        Material materialInHand = player.getInventory().getItemInMainHand().getType();
       
        if(materialInHand == Material.AIR){
            player.sendMessage(LanguageManager.getTranslation(MessageKey.NO_BLOCK_IN_HAND));
            return;
        }else if(!materialInHand.isBlock()){
            player.sendMessage(LanguageManager.getTranslation(MessageKey.ITEM_ISNT_BLOCK));
            return;
        }
            
            
        
        try {
            arena.addTask(task);
            buildBlockUnderOf(playerLoc, player.getInventory().getItemInMainHand().getType());
            player.sendMessage(LanguageManager.getTranslation(MessageKey.TASK_CREATED));
        } catch (IllegalArgumentException e) {
            player.sendMessage(LanguageManager.getTranslation(MessageKey.TASK_SAME_LOCATION));
            e.printStackTrace();
        }
        
    }

    @Override
    public List<String> onTabComplete(String[] args, Arena arena) {
        if(args.length == 1){
            return Arrays.asList("<taskname>");
        }else if(args.length == 2)
            return Arrays.asList("<duration>");
        
        return Collections.emptyList();
    }
    
    private void buildBlockUnderOf(Location loc, Material type){
        Block block = loc.getWorld().getBlockAt(loc.getBlockX(), loc.getBlockY() - 1, loc.getBlockZ());
        block.setType(type);
        
    }
}
