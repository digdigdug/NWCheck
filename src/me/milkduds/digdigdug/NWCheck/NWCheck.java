package me.milkduds.digdigdug.NWCheck;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class NWCheck extends JavaPlugin{
	public static NWCheck plugin;
	
	public final Logger log = Logger.getLogger("Minecraft");
	private Set<Player> hasCheck = new HashSet<Player>();
	public final NWCheckListener nwCheck= new NWCheckListener(this);
	
	
	public void onDisable(){
		PluginDescriptionFile pdfFile = this.getDescription();
		log.info("[" + pdfFile.getName() + "] has been disabled.");
	}
	
	public void onEnable(){
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(nwCheck, this);
	}
		
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		if(commandLabel.equalsIgnoreCase("check")){
			if(sender instanceof Player){
				Player player = (Player) sender;
				if(!hasCheck(player)){
					player.sendMessage(ChatColor.AQUA + "[NWCheck] " + ChatColor.WHITE + "Sneak to mark the block NW of you");
					addCheck(player, !hasCheck(player));
				}else if(hasCheck(player)){
					player.sendMessage(ChatColor.AQUA + "[NWCheck] " + ChatColor.WHITE + "Disabled");
					addCheck(player, !hasCheck(player));
				}
			}
		}return true;
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
	
}
