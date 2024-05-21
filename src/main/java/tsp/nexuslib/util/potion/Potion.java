package tsp.nexuslib.util.potion;

import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import tsp.nexuslib.util.Validate;

import java.util.Optional;

/**
 * @author TheSilentPro (Silent)
 */
public enum Potion {

    SPEED(PotionEffectType.SPEED), SLOWNESS(PotionEffectType.SLOWNESS), HASTE(PotionEffectType.HASTE),
    MINING_FATIGUE(PotionEffectType.MINING_FATIGUE), STRENGTH(PotionEffectType.STRENGTH), INSTANT_HEALTH(PotionEffectType.INSTANT_HEALTH),
    INSTANT_DAMAGE(PotionEffectType.INSTANT_DAMAGE), JUMP_BOOST(PotionEffectType.JUMP_BOOST), NAUSEA(PotionEffectType.NAUSEA),
    REGENERATION(PotionEffectType.REGENERATION), RESISTANCE(PotionEffectType.RESISTANCE), FIRE_RESISTANCE(PotionEffectType.FIRE_RESISTANCE),
    WATER_BREATHING(PotionEffectType.WATER_BREATHING), INVISIBILITY(PotionEffectType.INVISIBILITY), BLINDNESS(PotionEffectType.BLINDNESS),
    NIGHT_VISION(PotionEffectType.NIGHT_VISION), HUNGER(PotionEffectType.HUNGER), WEAKNESS(PotionEffectType.WEAKNESS),
    POISON(PotionEffectType.POISON), WITHER(PotionEffectType.WITHER), HEALTH_BOOST(PotionEffectType.HEALTH_BOOST),
    ABSORPTION(PotionEffectType.ABSORPTION), SATURATION(PotionEffectType.SATURATION), GLOWING(PotionEffectType.GLOWING),
    LEVITATION(PotionEffectType.LEVITATION), LUCK(PotionEffectType.LUCK), UNLUCK(PotionEffectType.UNLUCK),
    SLOW_FALLING(PotionEffectType.SLOW_FALLING), CONDUIT_POWER(PotionEffectType.CONDUIT_POWER),
    DOLPHINS_GRACE(PotionEffectType.DOLPHINS_GRACE), BAD_OMEN(PotionEffectType.BAD_OMEN),
    HERO_OF_THE_VILLAGE(PotionEffectType.HERO_OF_THE_VILLAGE), DARKNESS(PotionEffectType.DARKNESS);

    private final PotionEffectType type;

    Potion(final PotionEffectType type) { this.type = type; }

    public PotionEffectType getType() { return this.type; }

    public boolean apply(final LivingEntity entity, final int seconds, final int amplifier) {
        Validate.notNull(entity, "Entity can not be null!");
        return entity.addPotionEffect(new PotionEffect(this.type, seconds * 20, amplifier - 1));
    }

    public boolean apply(final LivingEntity entity, final int seconds) {
        Validate.notNull(entity, "Entity can not be null!");
        return this.apply(entity, seconds, 1);
    }

    public boolean applyRaw(final LivingEntity entity, final int ticks, final int amplifier) {
        Validate.notNull(entity, "Entity can not be null!");
        return entity.addPotionEffect(new PotionEffect(this.type, ticks, amplifier));
    }

    public boolean applyRaw(final LivingEntity entity, final int ticks) {
        Validate.notNull(entity, "Entity can not be null!");
        return entity.addPotionEffect(new PotionEffect(this.type, ticks, 0));
    }

    public boolean apply(final ItemStack item, final int seconds, final int amplifier) {
        Validate.notNull(item, "Item can not be null!");
        if (!(item.getItemMeta() instanceof final PotionMeta meta)) {
            throw new IllegalArgumentException("Item can not contain potion meta!");
        }

        meta.addCustomEffect(new PotionEffect(this.type, seconds * 20, amplifier - 1), true);
        return item.setItemMeta(meta);
    }

    public boolean apply(final ItemStack item, final int seconds) {
        Validate.notNull(item, "Item can not be null!");
        return this.apply(item, seconds, 1);
    }

    public boolean applyRaw(final ItemStack item, final int ticks, final int amplifier) {
        Validate.notNull(item, "Item can not be null!");
        if (!(item.getItemMeta() instanceof final PotionMeta meta)) {
            throw new IllegalArgumentException("Item can not contain potion meta!");
        }

        meta.addCustomEffect(new PotionEffect(this.type, ticks, amplifier), true);
        return item.setItemMeta(meta);
    }

    public boolean applyRaw(final ItemStack item, final int ticks) {
        Validate.notNull(item, "Item can not be null!");
        return this.applyRaw(item, ticks, 0);
    }

    public Optional<ItemStack> asItem(final Material potionItem, final int ticks, final int amplifier) {
        Validate.notNull(potionItem, "Material can not be null!");
        if (!this.isPotion(potionItem))
            throw new IllegalArgumentException("Material must be of potion type!");

        final ItemStack item = new ItemStack(potionItem);
        if (!(item.getItemMeta() instanceof final PotionMeta meta)) {
            return Optional.empty();
        }

        meta.addCustomEffect(new PotionEffect(this.type, ticks, amplifier), true);
        item.setItemMeta(meta);
        return Optional.of(item);
    }

    public boolean isPotion(final Material material) {
        return switch (material) {
        case POTION, SPLASH_POTION, LINGERING_POTION -> true;
        default -> false;
        };
    }

}