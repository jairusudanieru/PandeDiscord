package plugin.pandediscord.Discord;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class DiscordBot {
    public JDA jda;
    private final JavaPlugin plugin;
    public DiscordBot(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void enableBot() {
        //DiscordBot variables
        String botToken = plugin.getConfig().getString("botToken");
        if (botToken == null || botToken.equals("botToken") || botToken.isEmpty()) return;
        String activityName = plugin.getConfig().getString("activityName");
        if (activityName == null || activityName.isEmpty()) activityName = "Minecraft";

        //Enabling the bot
        try {
            this.jda = JDABuilder.createDefault(botToken, GatewayIntent.GUILD_PRESENCES, GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MEMBERS)
                    .setActivity(Activity.playing(activityName))
                    .addEventListeners(new DiscordChat(plugin))
                    .build();
            this.jda.awaitReady();
            this.setJDA(jda);
            Bukkit.getLogger().info("[PandeDiscord] Discord bot successfully enabled!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void disableBot() {
        //DiscordBot variables
        String botToken = plugin.getConfig().getString("botToken");
        if (botToken == null || botToken.equals("botToken") || botToken.isEmpty()) return;

        //Disabling the bot
        try {
            this.jda.shutdown();
            Bukkit.getLogger().info("[PandeDiscord] Discord bot successfully disabled!");
        } catch (Exception error) {
            error.printStackTrace();
        }
    }

    public void setJDA(JDA jda) {
        this.jda = jda;
    }
}
