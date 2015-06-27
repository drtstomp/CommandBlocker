package net.drtstunn.commandblocker;

import java.util.HashSet;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;


public class Main extends JavaPlugin implements Listener, CommandExecutor {

    @Getter
    private static Main plugin;
    @Getter
    private PluginDescriptionFile pdf = null;

    private String deny_message = null;
    private String allow_permission = null;
    private HashSet<String> blocked_commands = null;
    private HashSet<String> whitelisted_commands = null;

    @Override
    public void onEnable() {
        plugin = this;
        pdf = getDescription();

        log("==[ Plugin version " + pdf.getVersion() + " starting ]==");

        reload();
        getServer().getPluginManager().registerEvents(this, this);

        log("==[ Plugin version " + pdf.getVersion() + " started ]==");
    }

    @Override
    public void onDisable() {
        log("[CommandBlock] Plugin successfully enabled.");

        log("[CommandBlock] Plugin successfully disabled.");
    }

    public void log(String message) {
        getLogger().info(message);
    }

    public void reload() {
        saveDefaultConfig();
        reloadConfig();

        this.deny_message = ChatColor.translateAlternateColorCodes('&', getConfig().getString("deny_message"));
        this.allow_permission = getConfig().getString("allow_permission");
        this.blocked_commands = new HashSet<>();
        this.blocked_commands.addAll(getConfig().getStringList("blocked_commands"));
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission(this.allow_permission)) {
            return;
        }

        String attemptedCommand = event.getMessage().toLowerCase();

        for (String whitelistedCommand : this.whitelisted_commands) {
            if (attemptedCommand.contains(whitelistedCommand.toLowerCase())) {
                return;
            }
        }

        for (String blockedCommand : this.blocked_commands) {
            if (attemptedCommand.contains(blockedCommand.toLowerCase())) {
                event.setCancelled(true);
                player.sendMessage(this.deny_message);
                break;
            }
        }
    }
}
