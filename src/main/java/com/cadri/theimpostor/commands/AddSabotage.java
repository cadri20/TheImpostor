/*
 * Copyright (C) 2021 cadri
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
import com.cadri.theimpostor.arena.Arena;
import com.cadri.theimpostor.arena.ArenaManager;
import com.cadri.theimpostor.game.SabotageComponent;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author cadri
 */
public class AddSabotage implements SetupArenaCommand{

    @Override
    public void onCommand(Player player, Arena arena, String[] args) {
        if(args.length != 2){
            player.sendMessage(LanguageManager.getTranslation(MessageKey.INVALID_ARGUMENTS_NUMBER));
            return;
        }
        String sabotageName = args[0];
        int cooldown = 0;
        try{
            cooldown = Integer.parseInt(args[1]);
        }catch(NumberFormatException e){
            player.sendMessage(LanguageManager.getTranslation(MessageKey.ARGUMENT_NOT_NUMBER));
            return;
        }
        Block sabotageBlock = player.getTargetBlockExact(4);
        arena.addSabotageComponent(new SabotageComponent(sabotageName, sabotageBlock, cooldown));
        player.sendMessage(LanguageManager.getTranslation(MessageKey.SABOTAGE_CREATED));
        
    }

    @Override
    public List<String> onTabComplete(String[] args, Arena arena) {
        if(args.length == 1)
            return Arrays.asList("<name>");
        else if(args.length == 2)
            return Arrays.asList("<cooldown>");
        
        return Collections.emptyList();
    }
    
}
