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

import com.cadri.theimpostor.LanguageManager;
import com.cadri.theimpostor.MessageKey;
import com.cadri.theimpostor.arena.Arena;
import com.cadri.theimpostor.arena.ArenaUtils;
import java.util.Set;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

/**
 *
 * @author cadri
 */
public class ChatEvents implements Listener{
    String deadChatPrefix = LanguageManager.getTranslation(MessageKey.DEADCHAT_PREFIX);
    
    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent evt){
        Player player = evt.getPlayer();
        Arena arena = ArenaUtils.whereArenaIs(player);
        if(arena == null || !arena.started())
            return;

        if(!arena.isAlive(player)){
            Set<Player> recipients = evt.getRecipients();
            recipients.clear();
            recipients.addAll(arena.getDeadPlayers());
            evt.setFormat(deadChatPrefix + " %s: " + ChatColor.DARK_GRAY + "%s");
            
        }
        
        
        
    }
}
