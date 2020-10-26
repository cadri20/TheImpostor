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

import java.util.Collections;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 *
 * @author cadri
 */
public enum ItemOptions {
    KILL_PLAYER("Kill Player", "Right click with this to a player to kill him",Material.RED_WOOL);
    
    private String name;
    private String description;
    private Material material;
    ItemStack item;

    private ItemOptions(String name, String description, Material material) {
        this.name = name;
        this.description = description;
        this.material = material;
        item = new ItemStack(material);
        setMeta();
    }
    
    public ItemStack getItem(){
        return item;
    }
    
    private void setMeta(){
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(name);
        itemMeta.setLore(Collections.singletonList(description));
        item.setItemMeta(itemMeta);
    }
}
