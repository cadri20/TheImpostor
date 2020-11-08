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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 *
 * @author cadri
 */
public class LanguageManager {
    public static YamlConfiguration language;
    
    public static void loadLanguage() {
        String languageChoosed = TheImpostor.plugin.getConfig().getString("language");
        if(languageChoosed == null)
            throw new NullPointerException("language property not defined");
        
    
        File languageFile = new File(TheImpostor.plugin.getDataFolder() + File.separator + languageChoosed);
        if(!languageFile.exists()){
            InputStream fileFromPlugin =  TheImpostor.plugin.getResource(languageChoosed);
            try {
                Files.copy(fileFromPlugin, languageFile.toPath());
            } catch (IOException ex) {
                TheImpostor.plugin.getLogger().log(Level.SEVERE,"Error reading file");
            }
        }
        try{
        language = YamlConfiguration.loadConfiguration(languageFile);
        
        language.save(new File(TheImpostor.plugin.getDataFolder() + File.separator + languageChoosed));
        }catch(IllegalArgumentException e){
            TheImpostor.plugin.getLogger().log(Level.SEVERE, ChatColor.RED +  languageChoosed + "file not found");
        } catch (IOException ex) {
            Logger.getLogger(LanguageManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if(!validateKeys()){
            TheImpostor.plugin.getLogger().log(Level.WARNING,"Keys from " + languageChoosed + " are not valid" );
        }
    }
    
    private static boolean validateKeys(){
        MessageKeys[] keysInEnum = MessageKeys.values();
        Set<String> keys = language.getKeys(false);
        if(keysInEnum.length != keys.size())
            return false;
        
        int i = 0;
        for(String key: keys){
            if(!key.equals(keysInEnum[i].key))
                return false;
            i++;
        }
        return true;
    }
    public static String getTranslation(String key){
        String translation = language.getString(key);
       
        return ChatColor.translateAlternateColorCodes('&', translation);
    }
}
