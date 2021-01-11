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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author cadri
 */
public class SetupArena implements SubCommand{
    private Map<String, SetupArenaCommand> commands = new HashMap<>();
    
    public void loadSubCommands(){
        commands.put("addspawn", new AddSpawn());
        commands.put("addtask", new AddTask());
        commands.put("addsabotage", new AddSabotage());
        commands.put("emergencyblock", new SetEmergencyMeeetingBlock());
        commands.put("tasksnumber", new SetTasksNumber());
    }
    
    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if(sender instanceof Player){
            Player player = (Player) sender;
            Arena arena = ArenaManager.getArena(args[0]);
            if(arena == null){
                player.sendMessage(LanguageManager.getTranslation(MessageKey.ARENA_DOESNT_EXIST, args[0]));
            }else{
                SetupArenaCommand command = commands.get(args[1]);
                if(command == null)
                    player.sendMessage(LanguageManager.getTranslation(MessageKey.INVALID_COMMAND, args[1]));
                else{
                    command.onCommand(player, arena, Arrays.copyOfRange(args, 2, args.length));
                }
            }
            
        }
    }
    
    
}
