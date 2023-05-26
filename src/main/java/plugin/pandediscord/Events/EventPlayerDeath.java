package plugin.pandediscord.Events;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.List;
import java.util.Set;

public class EventPlayerDeath implements Listener {
    private final JavaPlugin plugin;
    private final JDA jda;
    public EventPlayerDeath(JavaPlugin plugin, JDA jda) {
        this.plugin = plugin;
        this.jda = jda;
    }

    @EventHandler
    public void onPlayerDeath(@NotNull PlayerDeathEvent event) {
        //Event variables
        Player player = event.getPlayer();
        String playerName = player.getName();
        World world = player.getWorld();
        String playerWorld = world.getName();
        String deathMessage = event.getDeathMessage();
        String hexColor = "#000000";

        //Checking if /gamerule showDeathMessage is set to false
        boolean showDeathMessages = world.getGameRuleValue(GameRule.SHOW_DEATH_MESSAGES);
        if (!showDeathMessages) return;

        //Checking if the player name contains a floodgate prefix
        playerName = playerName.replace("*","").replace(".","");
        String playerAvatar = "https://cravatar.eu/helmavatar/"+playerName+"/64.png";

        //Getting the worlds and channelId in the configuration
        ConfigurationSection worldGroups = plugin.getConfig().getConfigurationSection("worldGroups");
        if (worldGroups == null) return;
        Set<String> groupNames = worldGroups.getKeys(false);
        for (String groupName : groupNames) {
            String channelId = plugin.getConfig().getString("worldGroups." + groupName + ".channelId");
            List<String> worldNames = worldGroups.getStringList(groupName + ".worlds");

            //Checking which group the player's current world is
            if (worldNames.contains(playerWorld)) {
                try {
                    if (channelId == null || channelId.isEmpty()) return;
                    TextChannel textChannel = jda.getTextChannelById(channelId);
                    EmbedBuilder embed = new EmbedBuilder();
                    embed.setAuthor(deathMessage, null, playerAvatar);
                    embed.setColor(Color.decode(hexColor));
                    if (textChannel == null) return;
                    textChannel.sendMessageEmbeds(embed.build()).queue();
                } catch (Exception error) {
                    error.printStackTrace();
                }
            }
        }
    }
}
