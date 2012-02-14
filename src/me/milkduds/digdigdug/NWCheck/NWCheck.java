package me.milkduds.digdigdug.NWCheck;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class NWCheck extends JavaPlugin{
	public static NWCheck plugin;
	
	public final Logger log = Logger.getLogger("Minecraft");
	
	public File configFile = null;
	public FileConfiguration config;
	
	private Set<Player> hasCheck = new HashSet<Player>();
	public final NWCheckListener nwCheck = new NWCheckListener(this);
	
	private String[] directions = {"NORTH", "NORTH_EAST", "EAST", "SOUTH_EAST", "SOUTH", "SOUTH_WEST", 
			"WEST", "NORTH_WEST"};
	private String[] blocks = {"GLOWSTONE", "GOLD_BLOCK", "GOLD_ORE", "DIAMOND_BLOCK", "DIAMOND_ORE", 
			"IRON_BLOCK", "IRON_ORE", "COAL_ORE", "OBSIDIAN", "LAPIS_BLOCK", 
			"LAPIS_ORE", "LOG", "MELON", "NETHERRACK", "NETHER_BRICK", "PUMPKIN", 
			"REDSTONE_ORE", "SANDSTONE", "SMOOTH_BRICK", "SNOW_BLOCK"};
	
	public String dirList = (" ");
	public String blockList = (" ");
	
	public void onDisable(){
		PluginDescriptionFile pdfFile = this.getDescription();
		log.info("[" + pdfFile.getName() + "] Has been disabled.");
	}
	
	public void onEnable(){
		
		buildBlockList();
		buildDirList();
		
		config = new YamlConfiguration();
		
		PluginDescriptionFile pdfFile = this.getDescription();
		this.configFile = new File(getDataFolder(), "config.yml");
		
		if(!configFile.exists()){
			config = getConfig();
			config.options().copyHeader(true).copyDefaults(true);
			try {
				this.config.save(configFile);
			} catch (IOException e) {
				log.warning("[" + pdfFile.getName() + "] Failed to save default config.yml.");e.printStackTrace();
			}
			log.info("[" + pdfFile.getName() + "] Successfully saved /NWCheck/config.yml.");
		}
		
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(nwCheck, this);
		
		log.info("[" + pdfFile.getName() + "] v" + pdfFile.getVersion() + " has been enabled!");
	
		getCommand("check").setExecutor(new CommandExecutor(){
			public boolean onCommand(CommandSender sender, Command cmnd, String alias, String[] args){
				if(sender instanceof Player){
					Player player = (Player) sender;
					
					if(args.length == 0){
						if(!player.hasPermission("NWCheck.check")){
							player.sendMessage(ChatColor.RED + "[NWCheck] Sorry, you don't have permission.");
							return true;
						}
						if(!hasCheck(player)){
								player.sendMessage(ChatColor.AQUA + "[NWCheck] " + ChatColor.GREEN + "Sneak to mark the block " + config.getString("direction") + " of you.");
								addCheck(player, !hasCheck(player));
								return true;
							}else if(hasCheck(player)){
								player.sendMessage(ChatColor.AQUA + "[NWCheck] " + ChatColor.RED + "Disabled.");
								addCheck(player, !hasCheck(player));
								return true;
							}
					}
					
					if(args.length == 1){
						if(args[0].equals("block")){
							player.sendMessage(ChatColor.RED + "[NWCheck] You must specify a block.");
							return true;
						}
						if(args[0].equals("direction") || args[0].equals("dir")){
							player.sendMessage(ChatColor.RED + "[NWCheck] you must specify a direction.");
							return true;
						}
						else{
							player.sendMessage(ChatColor.AQUA + "[NWCheck] " + ChatColor.GREEN + "USAGE: " + ChatColor.AQUA + 
												"[check or ck] " + ChatColor.LIGHT_PURPLE + "[block, direction or dir] " +
												ChatColor.GOLD + " [choice of block or direction]");
							return true;
						}
						
					}
					
					if(args.length == 2){
						if(args[0].equals("block")){
							if(!player.hasPermission("NWCheck.block")){
								player.sendMessage(ChatColor.RED + "[NWCheck] Sorry, you don't have permission.");
								return true;
							}
							if(isBlock(args[1])){
								getConfig().set("block", args[1]);
								config = getConfig();
								saveConfigYaml();
								player.sendMessage(ChatColor.AQUA + "[NWCheck] The indicator block has been changed.");
								return true;
							}else{
								player.sendMessage(ChatColor.AQUA + "[NWCheck] Acceptable blocks are:" + blockList);
								return true;
							}
						}
						if(args[0].equals("direction") || args[0].equals("dir")){
							if(!player.hasPermission("NWCheck.direction")){
								player.sendMessage(ChatColor.RED + "[NWCheck] Sorry, you don't have permission.");
								return true;
							}
							if(dirOk(args[1])){
								getConfig().set("direction", args[1]);
								config = getConfig();
								saveConfigYaml();
								player.sendMessage(ChatColor.AQUA + "[NWCheck] The indicator direction has been changed.");
								return true;
							}else{
								player.sendMessage(ChatColor.AQUA + "[NWCheck] Acceptable directions are:" + dirList);
								return true;
							}
						}
						else{
							player.sendMessage(ChatColor.AQUA + "[NWCheck] " + ChatColor.GREEN + "USAGE: " + ChatColor.AQUA + 
									"[check or ck] " + ChatColor.LIGHT_PURPLE + "[block, direction or dir] " +
									ChatColor.GOLD + " [choice of block or direction]");
							return true;
						}
					}
					
					if(args.length > 2){
						player.sendMessage(ChatColor.AQUA + "[NWCheck] " + ChatColor.GREEN + "USAGE: " + ChatColor.AQUA + 
								"[check or ck] " + ChatColor.LIGHT_PURPLE + "[block, direction or dir] " +
								ChatColor.GOLD + " [choice of block or direction]");
						return true;
					}
				}return true;
			}

			private boolean dirOk(String dir){
				for(int i = 0; i < directions.length; i++){
					if(dir.equals(directions[i])){
						return true;
					}
				}return false;
			}
			
			private boolean isBlock(String block){
				for(int i = 0; i < blocks.length; i++){
					if(block.equals(blocks[i])){
						return true;
					}
				}return false;
			}
		});
	}

	private void buildDirList(){
		dirList = (dirList + directions[0]);
		for(int i = 1; i < directions.length; i++){
			dirList = (dirList + ", " + directions[i]);
		}
	}

	private void buildBlockList(){
		blockList = (blockList + blocks[0]);
		for(int i = 1; i < blocks.length; i++){
			blockList = (blockList + ", " + blocks[i]);
		}
	}

	public boolean hasCheck(Player player){
		return hasCheck.contains(player);
	}
	
	public void addCheck(Player player, boolean enabled){
		if(enabled){
			hasCheck.add(player);
		}else{
			hasCheck.remove(player);
		}
	}
	
	public void saveConfigYaml(){
		try{
			config.save(configFile);
		}catch(Exception e){
			this.log.warning(ChatColor.RED + "[NWCheck] Failed to save config.yml: " + e);
			}
	}
}
