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
package com.cadri.theimpostor;

/**
 *
 * @author cadri
 */
public enum MessageKeys {
    ARENA_CREATED_SUCCESSFULLY("arena_created_successfully"),
    ARENA_ALREADY_EXISTS("arena_already_exists"),
    ARENA_READY("arena_ready"),
    ARENA_NOT_EXISTS("arena_not_exists"),
    ARENA_COUNTDOWN("arena_countdown"),
    ARENA_GAME_START("arena_game_start");

    public String key;
    
    private MessageKeys(String key){
        this.key = key;
    }
    
}
