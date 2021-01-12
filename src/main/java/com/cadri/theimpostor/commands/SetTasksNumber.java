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
import com.cadri.theimpostor.game.PlayerColor;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author cadri
 */
public class SetTasksNumber implements SetupArenaCommand{

    @Override
    public void onCommand(Player player, Arena arena, String[] args) {
        int tasksNumber = Integer.parseInt(args[0]);
        if(tasksNumber > 0 && tasksNumber <= arena.getTasks().size()){
            arena.setPlayerTasksNumber(tasksNumber);
            player.sendMessage(LanguageManager.getTranslation(MessageKey.TASKS_NUMBER_SETTED));
        }else
            player.sendMessage(LanguageManager.getTranslation(MessageKey.INVALID_TASKS_NUMBER));
    }

    @Override
    public List<String> onTabComplete(String[] args, Arena arena) {
        if(args.length == 1){
            String tasksNumberLimit = String.format("<[1-%d]>", arena.getTasks().size());
            return Arrays.asList(tasksNumberLimit);
        }
        
        return Collections.emptyList();
    }
    
}
