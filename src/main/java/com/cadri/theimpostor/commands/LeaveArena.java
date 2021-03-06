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
import com.cadri.theimpostor.arena.Arena;
import com.cadri.theimpostor.arena.ArenaManager;
import com.cadri.theimpostor.arena.ArenaUtils;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author cadri
 */
public class LeaveArena implements SubCommand{
    private String usage = "&6/imp leave &b<arena>";
    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if(!sender.hasPermission(getPermission())){
            sender.sendMessage(LanguageManager.getTranslation(MessageKey.COMMAND_USE_NOT_ALLOWED));
            return;
        }
        
        if(sender instanceof Player){
            Player player = (Player) sender;
            Arena wherePlayerIs = ArenaUtils.whereArenaIs(player);
            if(wherePlayerIs != null){
                wherePlayerIs.removePlayer(player);
            }else{
                player.sendMessage(LanguageManager.getTranslation(MessageKey.PLAYER_NOT_IN_ARENA));
            }
        }
    }

    @Override
    public List<String> onTabComplete(String[] args) {        
        return Collections.emptyList();
    }

    @Override
    public String getUsage() {
        return usage;
    }
    
    @Override
    public String getDescription(){
        return LanguageManager.getTranslation(MessageKey.LEAVE_COMMAND_DESCRIPTION);
    }
    
    @Override
    public String getPermission(){
        return "theimpostor.arena.leave";
    }
}
