package com.raphfrk.craftproxy;

import java.io.File;
import java.util.logging.Logger;

import org.bukkit.Server;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class CraftProxy extends JavaPlugin {
	
	static final String slash = System.getProperty("file.separator");
	
	final Server server = getServer();
	
	PluginManager pm = server.getPluginManager();

	static Logger log;
	
	File folder;

	public CraftProxy(PluginLoader pluginLoader, Server instance, PluginDescriptionFile desc, File folder, File plugin, ClassLoader cLoader) {
		super(pluginLoader, instance, desc, folder, plugin, cLoader);
		this.folder = folder;				
	}
	
	public void onEnable() {
	
		
		log = Logger.getLogger("Minecraft");
		
		if(!folder.exists()) {
			folder.mkdirs();
		}
		
		MyPropertiesFile pf = new MyPropertiesFile(folder + slash + "craftproxy.txt");
		pf.load();
		
		String argsString = pf.getString("command_line"," ");
		final String[] args = argsString.trim().split(" ");
		
		pf.save();
		
		log.info("Starting CraftProxy as a plugin using command line: " + argsString.replace(',', ' '));
		
		Thread t = new Thread(new Runnable() {
			
			public void run() {
				Main.main(args, false);
			}
			
		});
		
		t.start();
		

	}
	
	public void onDisable() {

		Main.killServer();
		
	}
	
	
}