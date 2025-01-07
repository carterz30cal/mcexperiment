package com.carterz30cal.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.carterz30cal.main.Dungeons;

public class FileUtils 
{
	public static File getFile(String path)
	{
		File file = null;
		try
		{
			file = new File(Dungeons.instance.getDataFolder(), path);
			if (!file.exists()) 
			{
				file.getParentFile().mkdirs();
				file.createNewFile();
			}
		}
		catch (NullPointerException | IOException e) { return null;}
		return file;
	}
	
	public static FileConfiguration getExistingData(String path)
	{
		File file = getFile(path);

		FileConfiguration data = new YamlConfiguration();
		try 
		{
			data.load(file);
	    } 
		catch (IOException | InvalidConfigurationException e) 
		{
	        e.printStackTrace();
	    }
		return data;
	}
	public static FileConfiguration getData(String path)
	{
		File file = null;
		try
		{
			file = File.createTempFile("tempfile." + path, null);
		} catch (IOException e1)
		{
			e1.printStackTrace();
		}
		if (file == null) return null;
		copyToFile(Dungeons.instance.getResource(path + ".yml"),file);
		FileConfiguration data = new YamlConfiguration();
		try 
		{
			data.load(file);
	    } 
		catch (IOException | InvalidConfigurationException e) 
		{
	        e.printStackTrace();
	    }
		return data;
	}
	
	public static void copyToFile(InputStream inputStream, File file) {
	    try(OutputStream outputStream = new FileOutputStream(file)) {
	        copy(inputStream, outputStream);
	    }
	    catch (IOException e)
	    {
	    	e.printStackTrace();
	    }
	} 
	
	private static void copy(InputStream source, OutputStream target) throws IOException {
	    byte[] buf = new byte[8192];
	    int length;
	    while ((length = source.read(buf)) > 0) {
	        target.write(buf, 0, length);
	    }
	}
}