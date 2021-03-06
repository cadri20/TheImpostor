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
import com.cadri.theimpostor.TheImpostor;
import com.cadri.theimpostor.arena.Arena;
import com.cadri.theimpostor.arena.ArenaManager;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Collectors;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

/**
 *
 * @author cadri
 */
public class SetupArena implements SubCommand, AdminCommand{
    private Map<String, SetupArenaCommand> commands = new HashMap<>();
    private String usage = "&6/imp setup &b<arena>";
    
    public void loadSubCommands(){
        commands.put("addspawn", new AddSpawn());
        commands.put("addtask", new AddTask());
        commands.put("addsabotage", new AddSabotage());
        commands.put("emergencyblock", new SetEmergencyMeeetingBlock());
        commands.put("tasksnumber", new SetTasksNumber());
    }
    
    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if(!sender.hasPermission("theimpostor.admin.setup")){
            sender.sendMessage(LanguageManager.getTranslation(MessageKey.COMMAND_USE_NOT_ALLOWED));
            return;
        }
        
        if(args.length < 2){
            sender.sendMessage(LanguageManager.getTranslation(MessageKey.INVALID_ARGUMENTS_NUMBER));
            return;
        }
        if(sender instanceof Player){
            Player player = (Player) sender;
            Arena arena = ArenaManager.getArena(args[0]);
            if(arena == null){
                player.sendMessage(LanguageManager.getTranslation(MessageKey.ARENA_DOESNT_EXIST, args[0]));
            }else{
                SetupArenaCommand command = commands.get(args[1]);
                if(command == null)
                    player.sendMessage(LanguageManager.getTranslation(MessageKey.INVALID_COMMAND));
                else{
                    command.onCommand(player, arena, Arrays.copyOfRange(args, 2, args.length));
                }
            }
            
        }
    }

    @Override
    public List<String> onTabComplete(String[] args) {
        if(args.length == 1)
            return ArenaManager.getArenaNames().stream().filter(arenaName -> arenaName.startsWith(args[0])).collect(Collectors.toList());
        else if(args.length == 2){
            if(ArenaManager.getArenaNames().contains(args[0])){
                List<String> cmdsFiltered = commands.keySet().stream().filter(cmd -> cmd.startsWith(args[1])).collect(Collectors.toList());
                return cmdsFiltered;
            }
        }else if(args.length >= 3){
            SetupArenaCommand setupCommand = commands.get(args[1]);
            Arena arena = ArenaManager.getArena(args[0]);
            if(setupCommand != null && arena != null){
                return setupCommand.onTabComplete(Arrays.copyOfRange(args, 2, args.length), arena);
            }
        }

        return Collections.emptyList();
    }

    @Override
    public String getUsage() {
        return usage;
    }
    
    @Override
    public String getDescription(){
        return LanguageManager.getTranslation(MessageKey.SETUP_COMMAND_DESCRIPTION);
    }
    
    @Override
    public String getPermission(){
        return "theimpostor.arena.setup";
    }
}
