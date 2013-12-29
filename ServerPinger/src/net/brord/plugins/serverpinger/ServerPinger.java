/**
 * 
 */
package net.brord.plugins.serverpinger;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerOptions;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedServerPing;

/**
 * @author Brord
 *
 */
public class ServerPinger extends JavaPlugin{
	
	/* (non-Javadoc)
	 * @see org.bukkit.plugin.java.JavaPlugin#onEnable()
	 */
	@Override
	public void onEnable() {
		ProtocolLibrary.getProtocolManager().addPacketListener(
				new PacketAdapter(this, ListenerPriority.NORMAL, Arrays.asList(PacketType.Status.Server.OUT_SERVER_INFO), ListenerOptions.ASYNC){
					@Override
					public void onPacketSending(PacketEvent e){
						handlePing(e.getPacket().getServerPings().read(0));
					}
				}
		);
	}

	/**
	 * @param read
	 */
	protected void handlePing(WrappedServerPing ping) {
		ping.setPlayers(transform(getText()));
	}
	
	private List<WrappedGameProfile> transform(List<String> list){
		List<WrappedGameProfile> profiles = new LinkedList<>();
		
		String onlineplayers = Arrays.toString(getServer().getOfflinePlayers()).replace("[", "").replace("]", "");
		String worlds = Arrays.toString(getServer().getWorlds().toArray()).replace("[", "").replace("]", "");
		int numplayers = getServer().getOnlinePlayers().length;
		
		for (int i = 0; i<list.size(); i++){
			String s = list.get(i);
			s = ChatColor.translateAlternateColorCodes('&', s);
			s = s.replaceAll("<players>", onlineplayers);
			s = s.replaceAll("<numplayer>", numplayers + "");
			s = s.replaceAll("<motd>", getServer().getMotd());
			s = s.replaceAll("<ip>", getServer().getIp());
			s = s.replaceAll("<port>", getServer().getPort() + "");
			s = s.replaceAll("<worlds>", worlds);
			profiles.add(new WrappedGameProfile("id" + i, s));
		}
		
		return profiles;
	}
	
	private List<String> getText(){
		return getConfig().getStringList("text");
	}

}
