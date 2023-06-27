package plugin.pandediscord.Events;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import plugin.pandediscord.Discord.DiscordWebhook;

import java.util.List;
import java.util.Set;

public class EventPlayerChat implements Listener {
    private final JavaPlugin plugin;
    private final JDA jda;
    public EventPlayerChat(JavaPlugin plugin, JDA jda) {
        this.plugin = plugin;
        this.jda = jda;
    }

    @EventHandler
    public void onPlayerChat(@NotNull AsyncPlayerChatEvent event) {
        //Event variables
        Player player = event.getPlayer();
        String playerName = player.getName();
        String playerWorld = player.getWorld().getName();
        String playerMessage = event.getMessage();

        //Checking if the player name contains a floodgate prefix
        playerName = playerName.replace("*","").replace(".","");
        String fullMessage = playerName + " >> " + playerMessage;
        String playerAvatar = "https://cravatar.eu/helmavatar/"+playerName+"/64.png";

        //Getting the worlds, webhookUrl and channelId in the configuration
        ConfigurationSection worldGroups = plugin.getConfig().getConfigurationSection("worldGroups");
        if (worldGroups == null) return;
        Set<String> groupNames = worldGroups.getKeys(false);
        for (String groupName : groupNames) {
            String webhookUrl = plugin.getConfig().getString("worldGroups." + groupName + ".webhookUrl");
            String channelId = plugin.getConfig().getString("worldGroups." + groupName + ".channelId");
            List<String> worldNames = worldGroups.getStringList(groupName + ".worlds");

            //Checking which group the player's current world is
            if (worldNames.contains(playerWorld)) {
                try {
                    if (webhookUrl == null || webhookUrl.contains("webhookUrl") || webhookUrl.isEmpty()) {
                        if (channelId == null || channelId.isEmpty()) return;
                        TextChannel textChannel = jda.getTextChannelById(channelId);
                        if (textChannel == null) return;
                        textChannel.sendMessage(fullMessage).queue();
                    } else {
                        DiscordWebhook webhook = new DiscordWebhook(webhookUrl);
                        webhook.setUsername(playerName);
                        webhook.setAvatarUrl(playerAvatar);
                        webhook.setContent(playerMessage);
                        webhook.execute();
                    }
                } catch (Exception error) {
                    error.printStackTrace();
                    Bukkit.getLogger().info("Error in ChatEvent");
                }
            }
        }
    }
}
