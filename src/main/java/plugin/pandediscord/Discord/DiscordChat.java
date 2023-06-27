package plugin.pandediscord.Discord;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import static net.md_5.bungee.api.ChatColor.of;

import java.util.List;
import java.util.Set;

public class DiscordChat extends ListenerAdapter {
    private final JavaPlugin plugin;
    public DiscordChat(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        //Event variables
        boolean haveAttachment = event.getMessage().getAttachments().size() > 0;
        boolean authorIsBot = event.getAuthor().isBot();
        boolean authorIsSys = event.getAuthor().isSystem();
        String authorName = event.getAuthor().getName();
        String currentChannel = event.getChannel().getId();
        String roleName = plugin.getConfig().getString("roleName");
        String roleColor = plugin.getConfig().getString("roleHex");

        //Checking if the author is a bot or system
        if (authorIsBot || authorIsSys) return;

        //Checking if the role Color is null
        if (roleName == null || roleName.isEmpty()) roleName = "User";
        if (roleColor == null || roleColor.isEmpty()) roleColor = "#FFFFFF";

        //Message that will be sent on the Minecraft Server
        String chatAuthorTag = authorName + " >> ";
        String chatContent = event.getMessage().getContentDisplay();
        String chatPrefix = "§r[§bDiscord §r| " + of(roleColor) + roleName + "§r] ";
        String chatRawPrefix = "[Discord | " + roleName + "] ";
        if (chatContent.length() > 180) chatContent = chatContent.substring(0, 180) + "...";
        String fullMessage = chatPrefix + chatAuthorTag + chatContent;
        String rawMessage = chatRawPrefix + chatAuthorTag + chatContent;

        //Getting the worlds, webhookUrl and channelId in the configuration
        ConfigurationSection worldGroups = plugin.getConfig().getConfigurationSection("worldGroups");
        if (worldGroups == null) return;
        Set<String> groupNames = worldGroups.getKeys(false);
        for (String groupName : groupNames) {
            String channelId = plugin.getConfig().getString("worldGroups." + groupName + ".channelId");

            //Checking which group the player's current world is
            if (!currentChannel.equals(channelId)) continue;
            List<String> worldNames = worldGroups.getStringList(groupName + ".worlds");
            for (Player player : Bukkit.getOnlinePlayers()) {
                String world = player.getWorld().getName();
                if (worldNames.contains(world)) {
                    if (!haveAttachment) {
                        player.sendMessage(fullMessage);
                        Bukkit.getLogger().info(rawMessage);
                    } else {
                        if (!chatContent.isEmpty()) {
                            player.sendMessage(fullMessage);
                            Bukkit.getLogger().info(rawMessage);
                        }
                        //Checking if the message have attachments
                        for (Message.Attachment attachment : event.getMessage().getAttachments()) {
                            String attachmentName = attachment.getFileName();
                            String attachmentMessage = chatPrefix + chatAuthorTag + attachmentName;
                            String attachmentRawMessage = chatRawPrefix + chatAuthorTag + attachmentName;
                            player.sendMessage(attachmentMessage);
                            Bukkit.getLogger().info(attachmentRawMessage);
                        }
                    }
                }
            }
        }
    }
}
