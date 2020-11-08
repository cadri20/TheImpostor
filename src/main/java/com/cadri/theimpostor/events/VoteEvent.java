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

import com.cadri.theimpostor.arena.Arena;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 *
 * @author cadri
 */
public class VoteEvent extends Event{
    private static final HandlerList handlers = new HandlerList();
    
    private Player voter;
    private Player voted;
    private Arena arena;

    public VoteEvent(Player voter, Player voted, Arena arena) {
        this.voter = voter;
        this.voted = voted;
        this.arena = arena;
    }
    
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public Player getVoter() {
        return voter;
    }

    public Player getVoted() {
        return voted;
    }
    
    public Arena getArena(){
        return arena;
    }
}