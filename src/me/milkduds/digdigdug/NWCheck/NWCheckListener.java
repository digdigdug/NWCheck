package me.milkduds.digdigdug.NWCheck;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;

public class NWCheckListener implements Listener{
	public NWCheck plugin;
	
	public NWCheckListener(NWCheck plugin){
		this.plugin = plugin;
	}
	
	int toggle = 0;
	
	@EventHandler
	public void inPlayerMove(PlayerToggleSneakEvent event){
		Player player = event.getPlayer();
		
		FileConfiguration config = plugin.getConfig();
		String block = config.getString("block");
		String direction = config.getString("direction");
		
		if(!player.hasPermission("NWCheck.check")){
			return;
		}
		
		if(!plugin.hasCheck(player)){
			return;
		}
		
		if(!player.isSneaking()){
			return;
		}
		
		Block blockCenter = (player.getLocation().getBlock().getRelative(BlockFace.DOWN));
		Block nwBlock = blockCenter.getRelative(BlockFace.valueOf(direction));
		
		if(toggle == 0){
			storeBlockState(nwBlock.getState());
			nwBlock.setType(Material.valueOf(block));
			player.sendMessage(ChatColor.AQUA + "[NWCheck] Has indicated " + direction);
			toggle = 1;
		}else if(toggle == 1){
			BlockState oldState = getOldBlockState();
			oldState.update(true);
			toggle = 0;
		}
		
	}
	
	private BlockState temp;

	private BlockState getOldBlockState() {
		return temp;
	}

	private void storeBlockState(BlockState state) {
		temp = state;
	}
}