package tsp.nexuslib.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URLConnection;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.PluginCommandYamlParser;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class PluginUtils {

    /**
     * Retrieve the latest release from spigot
     *
     * @param plugin The plugin
     * @param id     The resource id on spigot
     * @param latest Whether the plugin is on the latest version
     */
    public static void isLatestVersion(final JavaPlugin plugin, final int id, final Consumer<Boolean> latest) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, checkTask -> {
            try {
                final URLConnection connection = URI.create("https://api.spigotmc.org/legacy/update.php?resource=" + id).toURL()
                        .openConnection();
                connection.setConnectTimeout(5000);
                connection.setRequestProperty("User-Agent", plugin.getName() + "-VersionChecker");

                latest.accept(new BufferedReader(new InputStreamReader(connection.getInputStream())).readLine()
                        .equals(plugin.getPluginMeta().getVersion()));
            } catch (final IOException e) {
                latest.accept(true); // Assume the version is latest if checking fails as to avoid any confusion
                e.printStackTrace();
            }
        });
    }

    public static List<Command> getCommands(final Plugin plugin) { return PluginCommandYamlParser.parse(plugin); }

    public static Optional<Plugin> load(final File file) {
        try {
            return Optional.ofNullable(Bukkit.getPluginManager().loadPlugin(file));
        } catch (InvalidPluginException | InvalidDescriptionException e) {
            // throw new RuntimeException(e);
            return Optional.empty();
        }
    }

    public static void disable(final Plugin plugin) { Bukkit.getPluginManager().disablePlugin(plugin); }

    public static void enable(final Plugin plugin) { Bukkit.getPluginManager().enablePlugin(plugin); }

}
