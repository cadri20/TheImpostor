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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.stream.Collectors;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

/**
 *
 * @author cadri
 */
public class CommandManager implements CommandExecutor, TabCompleter{
    public static String mainCommand = "theimpostor";
    public static HashMap<String,SubCommand> commands = new HashMap<>();
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String string, String[] args) {

        if(cmd.getName().equalsIgnoreCase(mainCommand)){
            SubCommand command = commands.get(args[0]);
            if(command == null){
                sender.sendMessage(LanguageManager.getTranslation(MessageKey.INVALID_COMMAND));
                return false;
            }
            command.onCommand(sender, Arrays.copyOfRange(args, 1, args.length));
            return true;
        }
        return false;
        
    }
    
    public static void loadCommands(){
        commands.put("join", new JoinArena());
        commands.put("create", new CreateArena());
        commands.put("leave", new LeaveArena());
        commands.put("enable", new Enable());
        
        SetupArena setupArena = new SetupArena();
        setupArena.loadSubCommands();
        commands.put("setup", setupArena);
    }

    @Override
    public List<String> onTabComplete(CommandSender cs, Command cmnd, String string, String[] args) {
        if(args.length == 1){
            List<String> commandsFiltered = commands.keySet().stream().filter(command -> command.startsWith(args[0])).collect(Collectors.toList());
            return commandsFiltered;
        }else if(args.length >= 2){
            SubCommand subCommand = commands.get(args[0]);
            if(subCommand != null)
                return commands.get(args[0]).onTabComplete(Arrays.copyOfRange(args, 1, args.length));
            
        }
        
        
        return Collections.emptyList();
    }
    
    
}
