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
package com.cadri.theimpostor.arena;

import java.util.StringTokenizer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

/**
 *
 * @author cadri
 */
public class Serializer {
    public static String serializeLocation(Location loc){
        StringBuilder sb = new StringBuilder();
        sb.append(loc.getWorld().getName()).append(",");
        sb.append(loc.getX()).append(",");
        sb.append(loc.getY()).append(",");
        sb.append(loc.getZ());
        
        return sb.toString();
    }
    
    public static Location getLocation(String locSerialized){
        StringTokenizer st = new StringTokenizer(locSerialized, ",");
        World world = Bukkit.getWorld(st.nextToken());
        double x = Double.parseDouble(st.nextToken());
        double y = Double.parseDouble(st.nextToken());
        double z = Double.parseDouble(st.nextToken());
        
        return new Location(world, x, y, z);
    }
}
