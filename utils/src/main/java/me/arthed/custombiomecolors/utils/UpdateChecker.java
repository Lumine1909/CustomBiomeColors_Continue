package me.arthed.custombiomecolors.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class UpdateChecker implements Listener {

    private static final String GITHUB_REPO = "Lumine1909/CustomBiomeColors_Continue";

    private final String currentVersion;
    private final String message;
    private final JavaPlugin plugin;

    public boolean needUpdate;

    public UpdateChecker(JavaPlugin plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);

        this.plugin = plugin;
        this.currentVersion = plugin.getDescription().getVersion();
        this.message = ChatColor.translateAlternateColorCodes('&', "&7[CustomBiomeColors] There is an update available! Download it from: https://github.com/Lumine1909/CustomBiomeColors_Continue/releases/latest");
        this.checkUpdates();
    }

    private static String cleanVersion(String version) {
        if (version.startsWith("v")) version = version.substring(1);
        int dashIndex = version.indexOf("-");
        return (dashIndex >= 0) ? version.substring(0, dashIndex) : version;
    }

    public void checkUpdates() {
        Thread thread = new Thread(() -> {
            try {
                HttpClient client = HttpClient.newHttpClient();

                HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.github.com/repos/" + GITHUB_REPO + "/releases/latest"))
                    .header("User-Agent", "Update-Checker")
                    .build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() != 200) {
                    plugin.getLogger().warning("Failed to check update");
                    return;
                }

                JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
                String tag = json.get("tag_name").getAsString();
                String latestVersion = cleanVersion(tag);

                if (!latestVersion.equalsIgnoreCase(currentVersion)) {
                    plugin.getLogger().warning("A new version is available: " + tag +
                        " (current: " + plugin.getDescription().getVersion() + ")");
                    needUpdate = true;
                } else {
                    plugin.getLogger().info("You are using the latest version.");
                }
            } catch (Exception e) {
                plugin.getLogger().warning("Update check failed: " + e.getMessage());
            }
        });

        thread.start();

    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        if (p.isOp() && needUpdate) {
            p.sendMessage(this.message);
        }
    }
}
