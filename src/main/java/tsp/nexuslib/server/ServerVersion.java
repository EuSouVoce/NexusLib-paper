package tsp.nexuslib.server;

import java.util.Optional;

import javax.annotation.Nonnull;

import org.bukkit.Bukkit;

/**
 * Server Version. This is a nice wrapper over minecraft's protocol versions.
 * You won't have to memorize the protocol version, just memorize the server
 * version you see in the launcher.
 *
 * @author retrooper
 * @see <a href=
 *      "https://wiki.vg/Protocol_version_numbers">https://wiki.vg/Protocol_version_numbers</a>
 * @since 1.6.9
 */
public enum ServerVersion {

    v_1_7_10(5), v_1_8(47), v_1_8_3(47), v_1_8_8(47), v_1_9(107), v_1_9_2(109), v_1_9_4(110),
    // 1.10 and 1.10.1 are redundant
    v_1_10(210), v_1_10_1(210), v_1_10_2(210), v_1_11(315), v_1_11_2(316), v_1_12(335), v_1_12_1(338), v_1_12_2(340), v_1_13(393),
    v_1_13_1(401), v_1_13_2(404), v_1_14(477), v_1_14_1(480), v_1_14_2(485), v_1_14_3(490), v_1_14_4(498), v_1_15(573), v_1_15_1(575),
    v_1_15_2(578), v_1_16(735), v_1_16_1(736), v_1_16_2(751), v_1_16_3(753), v_1_16_4(754), v_1_16_5(754), v_1_17(755), v_1_17_1(756),
    v_1_18(757), v_1_18_1(757), v_1_18_2(758), v_1_19(759), v_1_19_1(760), v_1_19_2(760), v_1_20(763), v_1_20_1(763), v_1_20_2(764),
    v_1_20_3(765), v_1_20_4(765);

    private static String NMS_VERSION_SUFFIX = "";
    // private static String NMS_VERSION_SUFFIX =
    // Bukkit.getServer().getClass().getPackage().getName().replace(".",
    // ",").split(",")[3];
    private static final ServerVersion[] VALUES = ServerVersion.values();
    public static ServerVersion[] reversedValues = new ServerVersion[ServerVersion.VALUES.length];
    private static ServerVersion cachedVersion;
    private final int protocolVersion;

    ServerVersion(final int protocolId) { this.protocolVersion = protocolId; }

    private static ServerVersion getVersionNoCache() {
        if (ServerVersion.reversedValues[0] == null) {
            ServerVersion.reversedValues = ServerVersion.reverse();
        }
        for (final ServerVersion val : ServerVersion.reversedValues) {
            final String valName = val.name().substring(2).replace("_", ".");
            if (Bukkit.getBukkitVersion().contains(valName)) {
                return val;
            }
        }

        return null;
    }

    /**
     * Get the server version. If PacketEvents has already attempted resolving,
     * return the cached version. If PacketEvents hasn't already attempted
     * resolving, it will resolve it, cache it and return the version.
     *
     * @return Server Version. (always cached)
     */
    @Nonnull
    public static Optional<ServerVersion> getVersion() {
        if (ServerVersion.cachedVersion == null) {
            ServerVersion.cachedVersion = ServerVersion.getVersionNoCache();
        }

        return Optional.ofNullable(ServerVersion.cachedVersion);
    }

    /**
     * The values in this enum in reverse.
     *
     * @return Reversed server version enum values.
     */
    private static ServerVersion[] reverse() {
        final ServerVersion[] array = ServerVersion.values();
        int i = 0;
        int j = array.length - 1;
        ServerVersion tmp;
        while (j > i) {
            tmp = array[j];
            array[j--] = array[i];
            array[i++] = tmp;
        }
        return array;
    }

    public static String getNMSSuffix() { return ServerVersion.NMS_VERSION_SUFFIX; }

    public static String getNMSDirectory() { return "net.minecraft.server" /* + "." */ + ServerVersion.getNMSSuffix(); }

    public static String getOBCDirectory() { return "org.bukkit.craftbukkit" /* + "." */ + (ServerVersion.getNMSSuffix()); }

    public static ServerVersion getLatest() { return ServerVersion.reversedValues[1]; }

    public static ServerVersion getOldest() { return ServerVersion.values()[0]; }

    /**
     * Get this server version's protocol version.
     *
     * @return Protocol version.
     */
    public int getProtocolVersion() { return this.protocolVersion; }

    /**
     * Is this server version newer than the compared server version? This method
     * simply checks if this server version's protocol version is greater than the
     * compared server version's protocol version.
     *
     * @param target Compared server version.
     * @return Is this server version newer than the compared server version.
     */
    public boolean isNewerThan(final ServerVersion target) {
        /*
         * Some server versions have the same protocol version in the minecraft
         * protocol. We still need this method to work in such cases. We first check if
         * this is the case, if the protocol versions aren't the same, we can just use
         * the protocol versions to compare the server versions.
         */
        if (target.protocolVersion != this.protocolVersion || this == target) {
            return this.protocolVersion > target.protocolVersion;
        }

        /*
         * The server versions unfortunately have the same protocol version. We need to
         * look at this "reversedValues" variable. The reversed values variable is an
         * array containing all enum constants in this enum but in a reversed order. I
         * already made this variable a while ago for a different usage, you can check
         * that out. The first one we find in the array is the newer version.
         */
        for (final ServerVersion version : ServerVersion.reversedValues) {
            if (version == target) {
                return false;
            }
            if (version == this)
                return true;
        }

        return false;
    }

    /**
     * Is this server version older than the compared server version? This method
     * simply checks if this server version's protocol version is less than the
     * compared server version's protocol version.
     *
     * @param target Compared server version.
     * @return Is this server version older than the compared server version.
     */
    public boolean isOlderThan(final ServerVersion target) {
        /*
         * Some server versions have the same protocol version in the minecraft
         * protocol. We still need this method to work in such cases. We first check if
         * this is the case, if the protocol versions aren't the same, we can just use
         * the protocol versions to compare the server versions.
         */
        if (target.protocolVersion != this.protocolVersion || this == target) {
            return this.protocolVersion < target.protocolVersion;
        }
        /*
         * The server versions unfortunately have the same protocol version. We look at
         * all enum constants in the ServerVersion enum in the order they have been
         * defined in. The first one we find in the array is the newer version.
         */
        for (final ServerVersion version : ServerVersion.VALUES) {
            if (version == this) {
                return true;
            } else if (version == target) {
                return false;
            }
        }
        return false;
    }

    /**
     * Is this server version newer than or equal to the compared server version?
     * This method simply checks if this server version's protocol version is
     * greater than or equal to the compared server version's protocol version.
     *
     * @param target Compared server version.
     * @return Is this server version newer than or equal to the compared server
     *         version.
     */
    public boolean isNewerThanOrEquals(final ServerVersion target) { return this == target || this.isNewerThan(target); }

    /**
     * Is this server version older than or equal to the compared server version?
     * This method simply checks if this server version's protocol version is older
     * than or equal to the compared server version's protocol version.
     *
     * @param target Compared server version.
     * @return Is this server version older than or equal to the compared server
     *         version.
     */
    public boolean isOlderThanOrEquals(final ServerVersion target) { return this == target || this.isOlderThan(target); }

    /**
     * Deprecated, please use {@link #isNewerThan(ServerVersion)}
     *
     * @param target Compared version.
     * @return Is this server version newer than the compared server version.
     * @deprecated Rename...
     **/
    @Deprecated
    public boolean isHigherThan(final ServerVersion target) { return this.isNewerThan(target); }

    /**
     * Deprecated, Please use {@link #isNewerThanOrEquals(ServerVersion)}
     *
     * @param target Compared server version.
     * @return Is this server version newer than or equal to the compared server
     *         version.
     * @deprecated Rename...
     */
    @Deprecated
    public boolean isHigherThanOrEquals(final ServerVersion target) { return this.isNewerThanOrEquals(target); }

    /**
     * Deprecated... Please use {@link #isOlderThan(ServerVersion)}
     *
     * @param target Compared server version.
     * @return Is this server version older than the compared server version.
     * @deprecated Rename....
     */
    @Deprecated
    public boolean isLowerThan(final ServerVersion target) { return this.isOlderThan(target); }

    /**
     * Deprecated, please use {@link #isOlderThanOrEquals(ServerVersion)}
     *
     * @param target Compared server version.
     * @return Is this server version older than or equal to the compared server
     *         version.
     * @deprecated Rename...
     */
    @Deprecated
    public boolean isLowerThanOrEquals(final ServerVersion target) { return this.isOlderThanOrEquals(target); }
}