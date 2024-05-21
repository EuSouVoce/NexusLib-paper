package tsp.nexuslib.mojang;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;

import org.bukkit.plugin.java.JavaPlugin;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import tsp.nexuslib.player.info.NameHistory;
import tsp.nexuslib.player.info.SkinInfo;

/**
 * Class for async fetching non-authenticated info from mojang.
 *
 * @author TheSilentPro
 */
public class MojangAPI {

    private final JavaPlugin plugin;
    private final Executor executor;

    public MojangAPI(final JavaPlugin plugin, final Executor executor) {
        this.plugin = plugin;
        this.executor = executor;
    }

    /**
     * Retrieve the unique id of a player based on their name
     *
     * @param name    The player name
     * @param timeout Connection timeout
     */
    public CompletableFuture<UUID> getUniqueId(final String name, final int timeout) {
        return this.getUniqueIdJson(name, timeout).thenApply(json -> UUID.fromString(json.get("id").toString()));
    }

    public CompletableFuture<UUID> getUniqueId(final String name) { return this.getUniqueId(name, 5000); }

    /**
     * Retrieve skin information about a {@link UUID}
     *
     * @param uuid    The unique id to check
     * @param timeout Connection timeout
     */
    public CompletableFuture<SkinInfo> getSkinInfo(final UUID uuid, final int timeout) {
        return this.getSkinInfoJson(uuid, timeout).thenApply(json -> {
            final JsonArray properties = json.get("properties").getAsJsonArray();
            final JsonObject textures = properties.get(0).getAsJsonObject();

            final String id = json.get("id").toString();
            final String name = json.get("name").toString();
            final String value = textures.get("value").toString();
            final String signature = textures.get("signature").toString();
            return new SkinInfo(id, name, value, signature);
        });
    }

    public CompletableFuture<SkinInfo> getSkinInfo(final UUID uuid) { return this.getSkinInfo(uuid, 5000); }

    /**
     * Retrieve name history of a {@link UUID}
     *
     * @param uuid    The unique id
     * @param timeout Connection timeout
     */
    public CompletableFuture<NameHistory> getNameHistory(final UUID uuid, final int timeout) {
        return this.getNameHistoryJson(uuid, timeout).thenApply(json -> {
            final Map<String, Long> history = new HashMap<>();
            for (final JsonElement e : json) {
                final JsonObject obj = e.getAsJsonObject();
                final String name = obj.get("name").toString();
                final long timestamp = obj.get("changedToAt") != null ? obj.get("changedToAt").getAsLong() : -1;
                history.put(name, timestamp);
            }

            return new NameHistory(uuid, history);
        });
    }

    public CompletableFuture<NameHistory> getNameHistory(final UUID uuid) { return this.getNameHistory(uuid, 5000); }

    // Json
    public CompletableFuture<JsonObject> getUniqueIdJson(final String name, final int timeout) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                final String req = "https://api.mojang.com/users/profiles/minecraft/" + name;

                final URLConnection connection = URI.create(req).toURL().openConnection();
                connection.setConnectTimeout(timeout);
                connection.setRequestProperty("User-Agent", this.plugin.getName() + "-UUIDFetcher");
                connection.setRequestProperty("Accept", "application/json");

                final StringBuilder response = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                }

                return (JsonParser.parseString(response.toString())).getAsJsonObject();
            } catch (final IOException ex) {
                throw new CompletionException(ex);
            }
        });
    }

    public CompletableFuture<JsonObject> getUniqueIdJson(final String name) { return this.getUniqueIdJson(name, 5000); }

    public CompletableFuture<JsonObject> getSkinInfoJson(final UUID uuid, final int timeout) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                final String req = "https://sessionserver.mojang.com/session/minecraft/profile/" + uuid.toString().replace("-", "")
                        + "?unsigned=false";
                final URLConnection connection = URI.create(req).toURL().openConnection();
                connection.setConnectTimeout(timeout);
                connection.setRequestProperty("User-Agent", this.plugin.getName() + "-SkinFetcher");
                connection.setRequestProperty("Accept", "application/json");

                final StringBuilder response = new StringBuilder();
                try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    String line;
                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }
                }

                return JsonParser.parseString(response.toString()).getAsJsonObject();
            } catch (final IOException ex) {
                throw new CompletionException(ex);
            }
        }, this.executor);
    }

    public CompletableFuture<JsonObject> getSkinInfoJson(final UUID uuid) { return this.getSkinInfoJson(uuid, 5000); }

    public CompletableFuture<JsonArray> getNameHistoryJson(final UUID uuid, final int timeout) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                final String req = "https://api.mojang.com/user/profiles/" + uuid.toString().replace("-", "") + "/names";
                final URLConnection connection = URI.create(req).toURL().openConnection();
                connection.setConnectTimeout(timeout);
                connection.setRequestProperty("User-Agent", this.plugin.getName() + "-NameHistoryFetcher");
                connection.setRequestProperty("Accept", "application/json");

                final StringBuilder response = new StringBuilder();
                try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    String line;
                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }
                }

                return JsonParser.parseString(response.toString()).getAsJsonArray();
            } catch (final IOException ex) {
                throw new CompletionException(ex);
            }
        }, this.executor);
    }

    public CompletableFuture<JsonArray> getNameHistoryJson(final UUID uuid) { return this.getNameHistoryJson(uuid, 5000); }

    public CompletableFuture<List<String>> getBlockedServers(final int timeout) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                final String req = "https://sessionserver.mojang.com/blockedservers";
                final List<String> blockedServers = new ArrayList<>();
                final URLConnection connection = URI.create(req).toURL().openConnection();
                connection.setConnectTimeout(timeout);
                connection.setRequestProperty("User-Agent", this.plugin.getName() + "-BlockedServersFetcher");
                connection.setRequestProperty("Accept", "application/json");

                try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    String line;
                    while ((line = in.readLine()) != null) {
                        blockedServers.add(line);
                    }
                }

                return blockedServers;
            } catch (final IOException ex) {
                throw new CompletionException(ex);
            }
        }, this.executor);
    }

    public CompletableFuture<List<String>> getBlockedServers() { return this.getBlockedServers(5000); }

}
