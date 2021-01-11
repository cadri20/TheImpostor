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

import java.util.Arrays;
import java.util.HashMap;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 *
 * @author cadri
 */
public class CommandManager implements CommandExecutor{
    public static String mainCommand = "theimpostor";
    public static HashMap<String,SubCommand> commands = new HashMap<>();
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String string, String[] args) {

        if(cmd.getName().equalsIgnoreCase(mainCommand)){
            commands.get(args[0]).onCommand(sender, Arrays.copyOfRange(args, 1, args.length));
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
    
}
