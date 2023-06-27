package plugin.pandediscord.Events;

import fr.xephi.authme.api.v3.AuthMeApi;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.List;
import java.util.Set;

public class EventPlayerChangeWorld implements Listener {

    private final JavaPlugin plugin;
    private final JDA jda;
    public EventPlayerChangeWorld(JavaPlugin plugin, JDA jda) {
        this.plugin = plugin;
        this.jda = jda;
    }

    @EventHandler
    public void onChangeWorld(@NotNull PlayerChangedWorldEvent event) {
        //Event variables
        Player player = event.getPlayer();
        String playerName = player.getName();
        String playerWorld = player.getWorld().getName();
        String playerOldWorld = event.getFrom().getName();
        String joinMessage = plugin.getConfig().getString("joinMessage");
        String leaveMessage = plugin.getConfig().getString("leaveMessage");
        String joinHexColor = "#00FF00";
        String leaveHexColor = "#FF0000";

        //Checking if the player is not logged in
        Plugin authMe = Bukkit.getServer().getPluginManager().getPlugin("AuthMe");
        if (authMe != null) if (!AuthMeApi.getInstance().isAuthenticated(player)) return;

        //Checking if the player name contains a floodgate prefix
        playerName = playerName.replace("*","").replace(".","");
        if (joinMessage != null) joinMessage = joinMessage.replace("%player%",playerName);
        if (leaveMessage != null) leaveMessage = leaveMessage.replace("%player%",playerName);
        String playerAvatar = "https://cravatar.eu/helmavatar/"+playerName+"/64.png";

        //Getting the worlds and channelId in the configuration
        ConfigurationSection worldGroups = plugin.getConfig().getConfigurationSection("worldGroups");
        if (worldGroups == null) return;
        Set<String> groupNames = worldGroups.getKeys(false);
        for (String groupName : groupNames) {
            String channelId = plugin.getConfig().getString("worldGroups." + groupName + ".channelId");
            List<String> worldNames = worldGroups.getStringList(groupName + ".worlds");

            //Sending the leave message to the player's old World
            if (worldNames.contains(playerOldWorld) && !worldNames.contains(playerWorld)) {
                try {
                    if (channelId == null || channelId.isEmpty()) return;
                    TextChannel textChannel = jda.getTextChannelById(channelId);
                    EmbedBuilder embed = new EmbedBuilder();
                    embed.setAuthor(leaveMessage, null, playerAvatar);
                    embed.setColor(Color.decode(leaveHexColor));
                    if (textChannel == null) return;
                    textChannel.sendMessageEmbeds(embed.build()).queue();
                } catch (Exception error) {
                    error.printStackTrace();
                }
            }

            //Sending the join message to the player's new World
            if (worldNames.contains(playerWorld) && !worldNames.contains(playerOldWorld)) {
                try {
                    if (channelId == null || channelId.isEmpty()) return;
                    TextChannel textChannel = jda.getTextChannelById(channelId);
                    EmbedBuilder embed = new EmbedBuilder();
                    embed.setAuthor(joinMessage, null, playerAvatar);
                    embed.setColor(Color.decode(joinHexColor));
                    if (textChannel == null) return;
                    textChannel.sendMessageEmbeds(embed.build()).queue();
                } catch (Exception error) {
                    error.printStackTrace();
                }
            }

        }
    }

}
