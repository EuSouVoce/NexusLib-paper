package tsp.nexuslib.config;

import com.google.common.annotations.Beta;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.util.List;
import java.util.Optional;

/**
 * Config expansion.
 *
 * @author TheSilentPro (Silent)
 */
@Beta
@SuppressWarnings("unused")
public class Config {

    private FileConfiguration data;

    public Config(final FileConfiguration data) { this.data = data; }

    public Config(final File file) { this.data = YamlConfiguration.loadConfiguration(file); }

    public void reload(final File file) { this.data = YamlConfiguration.loadConfiguration(file); }

    public void reload(final FileConfiguration data) { this.data = data; }

    @Nullable
    public Optional<Object> getOptional(@Nonnull final String path) { return Optional.ofNullable(this.data.get(path)); }

    @Nonnull
    public <T> Optional<T> getOptionalObject(@Nonnull final String path, @Nonnull final Class<T> clazz) {
        return Optional.ofNullable(this.data.getObject(path, clazz));
    }

    public Optional<Boolean> getOptionalBoolean(@Nonnull final String path) {
        return this.data.contains(path) ? Optional.of(this.data.getBoolean(path)) : Optional.empty();
    }

    public Optional<Color> getOptionalColor(@Nonnull final String path) { return Optional.ofNullable(this.data.getColor(path)); }

    public Optional<Double> getOptionalDouble(@Nonnull final String path) {
        if (this.data.get(path) instanceof final Double n) {
            return Optional.of(n);
        } else {
            return Optional.empty();
        }
    }

    public float getFloat(@Nonnull final String path) { return this.getFloat(path, 0); }

    public float getFloat(@Nonnull final String path, final float def) {
        if (this.data.get(path) instanceof final Float n) {
            return n;
        } else {
            return def;
        }
    }

    public Optional<Float> getOptionalFloat(@Nonnull final String path) {
        if (this.data.get(path) instanceof final Float n) {
            return Optional.of(n);
        } else {
            return Optional.empty();
        }
    }

    @Nonnull
    public Optional<Integer> getOptionalInt(@Nonnull final String path) {
        if (this.data.get(path) instanceof final Integer n) {
            return Optional.of(n);
        } else {
            return Optional.empty();
        }
    }

    @Nonnull
    public Optional<Long> getOptionalLong(@Nonnull final String path) {
        if (this.data.get(path) instanceof final Long n) {
            return Optional.of(n);
        } else {
            return Optional.empty();
        }
    }

    @Nullable
    public Number getNumber(@Nonnull final String path) {
        if (this.data.get(path) instanceof final Number number) {
            return number;
        } else {
            return null;
        }
    }

    @Nonnull
    public Optional<Number> getOptionalNumber(@Nonnull final String path) { return Optional.ofNullable(this.getNumber(path)); }

    @Nonnull
    public Optional<String> getOptionalString(@Nonnull final String path) { return Optional.ofNullable(this.data.getString(path)); }

    @SuppressWarnings("unchecked")
    @Nonnull
    public <T> Optional<List<T>> getOptionalList(@Nonnull final String path, final Class<T> clazz) {
        final List<?> list = this.data.getList(path);
        if (list != null && !list.isEmpty() && list.get(0).getClass().isInstance(clazz)) {
            // noinspection unchecked
            return Optional.of((List<T>) list);
        } else {
            return Optional.empty();
        }
    }

    @Nonnull
    public <T extends ConfigurationSerializable> Optional<ConfigurationSerializable> getOptionalSerializable(@Nonnull final String path,
            @Nonnull final Class<T> clazz) {
        return Optional.ofNullable(this.data.getSerializable(path, clazz));
    }

    @Nonnull
    public Optional<Vector> getOptionalVector(@Nonnull final String path) { return Optional.ofNullable(this.data.getVector(path)); }

    @Nonnull
    public Optional<ItemStack> getOptionalItemStack(@Nonnull final String path) {
        return Optional.ofNullable(this.data.getItemStack(path));
    }

    @Nonnull
    public Optional<Location> getOptionalLocation(@Nonnull final String path) { return Optional.ofNullable(this.data.getLocation(path)); }

    @Nonnull
    public Optional<OfflinePlayer> getOptionalOfflinePlayer(@Nonnull final String path) {
        return Optional.ofNullable(this.data.getOfflinePlayer(path));
    }

    @Nonnull
    public Optional<Configuration> getOptionalDefaults() { return Optional.ofNullable(this.data.getDefaults()); }

    @Nonnull
    public Optional<Configuration> getOptionalRoot() { return Optional.ofNullable(this.data.getRoot()); }

    @Nonnull
    public Optional<ConfigurationSection> getOptionalConfigurationSection(@Nonnull final String path) {
        return Optional.ofNullable(this.data.getConfigurationSection(path));
    }

    // Convenience methods

    public void setLocation(final String path, final Location location) { this.data.set(path, location); }

    public void setItemStack(final String path, final ItemStack itemStack) { this.data.set(path, itemStack); }

}
