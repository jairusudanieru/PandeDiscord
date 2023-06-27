package plugin.pandediscord.Events;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class EventPlayerAdvancement implements Listener {

    private final JavaPlugin plugin;
    private final JDA jda;
    public EventPlayerAdvancement(JavaPlugin plugin, JDA jda) {
        this.plugin = plugin;
        this.jda = jda;
    }

    @EventHandler
    public void onAdvancement(@NotNull PlayerAdvancementDoneEvent event) {
        //Event variables
        Player player = event.getPlayer();
        World world = player.getWorld();
        String playerName = player.getName();
        String playerWorld = world.getName();
        String hexColor = "#FFFF00";

        //Checking if /gamerule announceAdvancements is set to false
        boolean showAdvancements = world.getGameRuleValue(GameRule.ANNOUNCE_ADVANCEMENTS);
        if (!showAdvancements) return;

        //Checking if the player name contains a floodgate prefix and checking the message in the config
        playerName = playerName.replace("*","").replace(".","");
        String playerAvatar = "https://cravatar.eu/helmavatar/"+playerName+"/64.png";

        //Checking if the key have minecraft:recipe/s
        String key = event.getAdvancement().getKey().getKey();
        String namespace = event.getAdvancement().getKey().getNamespace();
        if (!namespace.contains("minecraft")) return;
        if (key.contains("recipe/") || key.contains("recipes/")) return;

        //Getting the Title of the Advancement
        Component advancementTitle = event.getAdvancement().getDisplay().title();
        String advancementName = PlainTextComponentSerializer.plainText().serialize(advancementTitle);
        List<String> list = Arrays.asList("Adventure", "Husbandry", "Minecraft", "Nether", "The End");
        if (list.contains(advancementName)) return;
        String advancement = playerName + " has made the advancement " + advancementName;

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
                    embed.setAuthor(advancement, null, playerAvatar);
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
