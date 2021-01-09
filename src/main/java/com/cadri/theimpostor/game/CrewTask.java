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
package com.cadri.theimpostor.game;

import org.bukkit.Location;
import org.bukkit.map.MapCursor;

/**
 *
 * @author cadri
 */
public class CrewTask {
    private String name;
    private Location location;
    private boolean completed;
    private int timeToComplete;
    private MapCursor mapCursor = null;

    public CrewTask(String name, Location location, int timeToComplete) {
        this.name = name;
        this.location = location;
        this.timeToComplete = timeToComplete;
        this.completed = false;
    }
    
    public void complete(){
        this.completed = true;
    }

    public String getName() {
        return name;
    }

    public Location getLocation() {
        return location;
    }

    public boolean isCompleted() {
        return completed;
    }

    public int getTimeToComplete() {
        return timeToComplete;
    }

    public MapCursor getMapCursor() {
        return mapCursor;
    }

    public void setMapCursor(MapCursor mapCursor) {
        this.mapCursor = mapCursor;
    }
    
    
}
