package tsp.nexuslib.builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.data.BlockData;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockDataMeta;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import com.google.common.collect.Multimap;

import tsp.nexuslib.util.Pair;
import tsp.nexuslib.util.Validate;

/**
 * Class for building an {@link ItemStack}
 *
 * @author TheSilentPro
 */
public class ItemBuilder {

    private final ItemStack item;
    private ItemMeta meta;
    private boolean colorize = true;

    /**
     * Creates a new ItemBuilder for the given {@link Material}
     *
     * @param material The material used for creating the new Object
     */
    public ItemBuilder(@Nonnull final Material material) {
        Validate.notNull(material, "Material can not be null!");

        this.item = new ItemStack(material);
        this.meta = this.item.getItemMeta();
    }

    /**
     * Creates a new ItemBuilder for the given {@link ItemStack}
     *
     * @param item The item used for creating the new Object
     */
    public ItemBuilder(@Nonnull final ItemStack item) {
        Validate.notNull(item, "Item can not be null!");

        this.item = item;
        this.meta = item.getItemMeta();
    }

    /**
     * Creates a new ItemBuilder for the given {@link ItemStack} and
     * {@link ItemMeta}
     *
     * @param item The item used for creating the new Object
     * @param meta The ItemMeta used for creating the new Object
     */
    public ItemBuilder(@Nonnull final ItemStack item, @Nonnull final ItemMeta meta) {
        Validate.notNull(item, "Item can not be null!");
        Validate.notNull(meta, "Meta can not be null!");

        this.item = item;
        this.meta = meta;
    }

    /**
     * Creates a new ItemBuilder for the given {@link Item}
     *
     * @param item The item used for creating the new Object
     */
    public ItemBuilder(@Nonnull final Item item) {
        Validate.notNull(item, "Item can not be null!");

        this.item = item.getItemStack();
        this.meta = item.getItemStack().getItemMeta();
    }

    /**
     * Sets the {@link Material} of the item
     *
     * @param material The item material
     */
    public ItemBuilder material(final Material material) {
        this.item.withType(material);
        return this;
    }

    /**
     * Sets the name of the item
     *
     * @param name Name
     */
    public ItemBuilder name(@Nonnull final String name) {
        Validate.notNull(name, "Name can not be null!");

        this.meta.setDisplayName(this.colorize(name));
        return this;
    }

    /**
     * Sets the amount of the item
     *
     * @param amount Amount
     */
    public ItemBuilder amount(final int amount) {
        this.item.setAmount(amount);
        return this;
    }

    /**
     * Adds an enchantment
     *
     * @param enchantment            Enchantment to add
     * @param level                  Enchantment level
     * @param ignoreLevelRestriction Should restrictions be ignored
     */
    public ItemBuilder enchant(@Nonnull final Enchantment enchantment, final int level, final boolean ignoreLevelRestriction) {
        Validate.notNull(enchantment, "Enchantment can not be null!");

        this.meta.addEnchant(enchantment, level, ignoreLevelRestriction);
        return this;
    }

    /**
     * Adds an enchantment
     *
     * @param enchantment Enchantment to add
     * @param level       Enchantment level
     */
    public ItemBuilder enchant(@Nonnull final Enchantment enchantment, final int level) {
        Validate.notNull(enchantment, "Enchantment can not be null!");

        this.meta.addEnchant(enchantment, level, false);
        return this;
    }

    /**
     * Adds an enchantment
     *
     * @param enchantment        Enchantment to add
     * @param ignoreRestrictions Should restrictions be ignored
     */
    public ItemBuilder enchant(@Nonnull final Pair<Enchantment, Integer> enchantment, final boolean ignoreRestrictions) {
        Validate.notNull(enchantment, "Enchantment pair can not be null!");

        this.meta.addEnchant(enchantment.key(), enchantment.value(), ignoreRestrictions);
        return this;
    }

    /**
     * Adds an enchantment
     *
     * @param enchantment Enchantment to add
     */
    public ItemBuilder enchant(@Nonnull final Pair<Enchantment, Integer> enchantment) {
        Validate.notNull(enchantment, "Enchantment pair can not be null!");

        return this.enchant(enchantment.key(), enchantment.value(), false);
    }

    /**
     * Adds multiple enchantments at once
     *
     * @param enchantments The enchantments to add
     */
    public ItemBuilder enchant(@Nonnull final Map<Enchantment, Integer> enchantments, final boolean ignoreLevelRestriction) {
        Validate.notNull(enchantments, "Enchantments can not be null!");

        for (final Map.Entry<Enchantment, Integer> enchantment : enchantments.entrySet()) {
            this.meta.addEnchant(enchantment.getKey(), enchantment.getValue(), ignoreLevelRestriction);
        }
        return this;
    }

    /**
     * Removes an enchantment
     *
     * @param enchantment The enchantment to remove
     */
    public ItemBuilder disenchant(@Nonnull final Enchantment enchantment) {
        Validate.notNull(enchantment, "Enchantment can not be null!");

        this.meta.removeEnchant(enchantment);
        return this;
    }

    /**
     * Remove multiple enchantments
     *
     * @param enchantments Collection of enchantments to remove
     */
    public ItemBuilder disenchant(@Nonnull final Collection<Enchantment> enchantments) {
        Validate.notNull(enchantments, "Enchantments can not be null!");

        for (final Enchantment enchantment : enchantments) {
            this.meta.removeEnchant(enchantment);
        }
        return this;
    }

    /**
     * Adds the given string as a lore
     *
     * @param lore The string to add
     */
    public ItemBuilder addLore(@Nonnull final String lore) {
        Validate.notNull(lore, "Lore can not be null!");

        final List<String> loreList = this.meta.getLore() != null ? this.meta.getLore() : new ArrayList<>();
        loreList.add(this.colorize(lore));
        this.meta.setLore(loreList);
        return this;
    }

    /**
     * Adds the given strings as a lore
     *
     * @param lore The strings to add
     */
    public ItemBuilder setLore(@Nullable final String... lore) {
        if (lore == null) {
            this.meta.setLore(null);
        } else {
            this.meta.setLore(Arrays.stream(lore).map(this::colorize).collect(Collectors.toList()));
        }
        return this;
    }

    /**
     * Adds the given list as a lore
     *
     * @param lore The list to add
     */
    public ItemBuilder setLore(@Nullable final List<String> lore) {
        if (lore == null) {
            this.meta.setLore(null);
        } else {
            this.meta.setLore(lore.stream().map(this::colorize).collect(Collectors.toList()));
        }
        return this;
    }

    /**
     * Removes a line from the lore based on the index
     *
     * @param index The line to remove
     */
    public ItemBuilder removeLore(final int index) {
        if (this.meta.getLore() != null) {
            final List<String> loreList = this.meta.getLore();
            loreList.remove(index);
            this.meta.setLore(loreList);
        }
        return this;
    }

    /**
     * Removes a line from the lore based on the line string
     *
     * @param line The line to remove
     */
    public ItemBuilder removeLore(@Nonnull final String line) {
        Validate.notNull(line, "line can not be null!");

        if (this.meta.getLore() != null) {
            final List<String> loreList = this.meta.getLore();
            loreList.remove(line);
            this.meta.setLore(loreList);
        }
        return this;
    }

    /**
     * Adds an {@link ItemFlag}
     *
     * @param itemFlags The ItemFlag to add
     */
    public ItemBuilder addItemFlags(@Nonnull final ItemFlag... itemFlags) {
        Validate.notNull(this.item, "ItemFlag can not be null!");

        this.meta.addItemFlags(itemFlags);
        return this;
    }

    /**
     * Removes an ItemFlag
     *
     * @param itemFlags The ItemFlag to remove
     */
    public ItemBuilder removeItemFlags(@Nonnull final ItemFlag... itemFlags) {
        Validate.notNull(itemFlags, "ItemFlag can not be null!");

        this.meta.removeItemFlags(itemFlags);
        return this;
    }

    /**
     * Sets the material data
     *
     * @param materialData The material data to set
     * @deprecated
     */
    @Deprecated
    public ItemBuilder setMaterialData(@Nonnull final MaterialData materialData) {
        Validate.notNull(materialData, "MaterialData can not be null!");

        this.item.setData(materialData);
        return this;
    }

    /**
     * Sets the block data
     *
     * @param materialData The block data to set
     */
    @Deprecated
    public ItemBuilder setMaterialData(@Nonnull final BlockData materialData) {
        Validate.notNull(materialData, "BlockData can not be null!");
        final BlockDataMeta tmp = ((BlockDataMeta) this.item.getItemMeta());
        tmp.setBlockData(materialData);
        this.item.setItemMeta(tmp);
        return this;
    }

    /**
     * Set as unbreakable
     *
     * @param unbreakable Whether the item should be unbreakable
     */
    public ItemBuilder setUnbreakable(final boolean unbreakable) {
        this.meta.setUnbreakable(unbreakable);
        return this;
    }

    /**
     * Set the durability
     *
     * @param durability The amount of durability
     */
    public ItemBuilder setDurability(final int durability) {
        if (!(this.meta instanceof Damageable))
            return this;
        ((Damageable) this.meta).setDamage(durability);
        return this;
    }

    /**
     * Adds an {@link Attribute}
     *
     * @param attribute The attribute to add
     * @param modifier  The attribute modifier
     */
    public ItemBuilder addAttributeModifier(@Nonnull final Attribute attribute, @Nonnull final AttributeModifier modifier) {
        Validate.notNull(attribute, "Attribute can not be null!");
        Validate.notNull(modifier, "Modifier can not be null!");

        this.meta.addAttributeModifier(attribute, modifier);
        return this;
    }

    /**
     * Adds multiple Attributes at once
     *
     * @param attributes The attributes to add
     */
    public ItemBuilder setAttributeModifiers(@Nonnull final Multimap<Attribute, AttributeModifier> attributes) {
        Validate.notNull(attributes, "Attributes can not be null!");

        this.meta.setAttributeModifiers(attributes);
        return this;
    }

    /**
     * Set glowing
     *
     * @param glow Whether the item should glow
     */
    public ItemBuilder setGlow(final boolean glow) {
        if (glow) {
            return this.enchant(this.item.getType() != Material.BOW ? Enchantment.INFINITY : Enchantment.LUCK_OF_THE_SEA, 1, true);
        } else {
            return this.disenchant(this.item.getType() != Material.BOW ? Enchantment.INFINITY : Enchantment.LUCK_OF_THE_SEA);
        }
    }

    /**
     * Set the owner as an {@link OfflinePlayer}
     *
     * @param owner The owner
     */
    public ItemBuilder setOwner(@Nonnull final OfflinePlayer owner) {
        Validate.notNull(owner, "Owner can not be null!");

        if (this.meta instanceof SkullMeta) {
            ((SkullMeta) this.meta).setOwningPlayer(owner);
        }
        return this;
    }

    /**
     * Set custom model data
     *
     * @param i Model data
     */
    public ItemBuilder setCustomModelData(final int i) {
        this.meta.setCustomModelData(i);
        return this;
    }

    /**
     * Set item meta
     *
     * @param meta The item meta
     */
    public ItemBuilder setItemMeta(@Nonnull final ItemMeta meta) {
        Validate.notNull(meta, "Meta can not be null!");

        this.meta = meta;
        return this;
    }

    /**
     * Set persistent data
     *
     * @param key   The namespace for this data
     * @param type  The type of data
     * @param value The data value
     * @param <T>   Primitive
     * @param <Z>   Complex
     */
    public <T, Z> ItemBuilder set(final NamespacedKey key, final PersistentDataType<T, Z> type, final Z value) {
        this.getContainer().set(key, type, value);
        return this;
    }

    /**
     * Set persistent data if absent
     *
     * @param key   The namespace for this data
     * @param type  The type of data
     * @param value The data value
     * @param <T>   Primitive
     * @param <Z>   Complex
     */
    public <T, Z> ItemBuilder setIfAbsent(final NamespacedKey key, final PersistentDataType<T, Z> type, final Z value) {
        if (!this.getContainer().has(key, type)) {
            this.getContainer().set(key, type, value);
        }
        return this;
    }

    /**
     * Build the item
     *
     * @return The finished item
     */
    public ItemStack build() {
        this.item.setItemMeta(this.meta);
        return this.item.clone();
    }

    /**
     * Retrieve the {@link PersistentDataContainer} of the item
     *
     * @return The container
     */
    public PersistentDataContainer getContainer() { return this.meta.getPersistentDataContainer(); }

    /**
     * Set if strings should be colorized by the builder
     *
     * @param b Whether to colorize strings
     */
    public ItemBuilder colorize(final boolean b) {
        this.colorize = b;
        return this;
    }

    /**
     * Colorize a string
     *
     * @param string The string to colorize
     * @return Colorized string
     */
    private String colorize(final String string) {
        if (!this.colorize)
            return string;
        return ChatColor.translateAlternateColorCodes('&', string);
    }

}