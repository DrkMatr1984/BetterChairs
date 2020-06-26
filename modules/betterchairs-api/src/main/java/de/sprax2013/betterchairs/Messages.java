package de.sprax2013.betterchairs;

import de.sprax2013.advanced_dev_utils.spigot.files.yaml.YAMLFile;
import de.sprax2013.advanced_dev_utils.spigot.files.yaml.YAMLFileManager;
import org.bukkit.ChatColor;

import java.io.File;
import java.util.Objects;

// TODO: Comments inside messages.yml
public class Messages {
    public static final String PREFIX_CONSOLE = ChatColor.stripColor("[BetterChairs] ");
    private static final int CURR_VERSION = 1;

    public static String getPrefix() {
        return ChatColor.translateAlternateColorCodes('&',
                getMessages().getCfg().getString("General.Prefix"));
    }

    public static String noPermission() {
        return getString("General.NoPermission");
    }

    public static String toggleChairsEnabled() {
        return getString("ToggleChairs.Enabled");
    }

    public static String toggleChairsDisabled() {
        return getString("ToggleChairs.Disabled");
    }

    public static String chairUseOccupied() {
        return getString("ChairUse.AlreadyOccupied");
    }

    public static String chairUseNeedsSigns() {
        return getString("ChairUse.NeedsSignsOnBothSides");
    }

    private static String getString(String key) {
        return ChatColor.translateAlternateColorCodes('&',
                getMessages().getCfg().getString(key))
                .replace("${Prefix}", getPrefix());
    }

    private static YAMLFile getMessages() {
        YAMLFile yamlFile = YAMLFileManager.getFile(ChairManager.getPlugin(), "messages.yml");

        boolean save = false;

        // Convert from old format or delete when invalid version
        if (yamlFile.getCfg().getKeys(false).size() > 0) {
            if (!yamlFile.getCfg().contains("version")) {
                System.out.println(PREFIX_CONSOLE + "Found old BetterChairs messages.yml - Converting into new format...");

                Object noPermission = yamlFile.getCfg().get("Cant use message"),
                        toggleChairsDisabled = yamlFile.getCfg().get("Message to send when player toggle chairs to off"),
                        toggleChairsEnabled = yamlFile.getCfg().get("Message to send when player toggle chairs to on"),
                        chairOccupied = yamlFile.getCfg().get("Message to send if the chairs is occupied"),
                        needsSign = yamlFile.getCfg().get("Message to send if the chairs need sign or chair");

                backupConfig(yamlFile);

                // Generate new file
                yamlFile = YAMLFileManager.getFile(ChairManager.getPlugin(), "messages.yml");

                yamlFile.getCfg().set("version", CURR_VERSION);

                // General.*
                if (noPermission instanceof String) {
                    yamlFile.getCfg().set("General.NoPermission", noPermission);
                }

                // ToggleChairs.*
                if (toggleChairsDisabled instanceof String) {
                    yamlFile.getCfg().set("ToggleChairs.Enabled", toggleChairsDisabled);
                }
                if (toggleChairsEnabled instanceof String) {
                    yamlFile.getCfg().set("ToggleChairs.Disabled", toggleChairsEnabled);
                }

                // ChairUse.*
                if (chairOccupied instanceof String) {
                    yamlFile.getCfg().set("ChairUse.AlreadyOccupied", chairOccupied);
                }
                if (needsSign instanceof String) {
                    yamlFile.getCfg().set("ChairUse.NeedsSignsOnBothSides", needsSign);
                }

                save = true;
            } else {
                String verStr = yamlFile.getCfg().getString("version");

                try {
                    int ver = Integer.parseInt(verStr);

                    if (ver != 1) {
                        throw new Exception("Invalid version (=" + ver + ") provided inside messages.yml");
                    }
                } catch (Exception ex) {
                    backupConfig(yamlFile);

                    // Generate new file
                    yamlFile = YAMLFileManager.getFile(ChairManager.getPlugin(), "messages.yml");
                }
            }
        }

        // Insert default values
        if (yamlFile.getCountOfDefaultValues() == 0) {
            yamlFile.addDefault("version", CURR_VERSION);

            yamlFile.addDefault("General.Prefix", "&7[&2" + Objects.requireNonNull(ChairManager.getPlugin()).getName() + "&7]");
            yamlFile.addDefault("General.NoPermission", "${Prefix} &cYou do not have permission to use this command!");

            yamlFile.addDefault("ToggleChairs.Enabled", "${Prefix} &eYou now can use chairs again");
            yamlFile.addDefault("ToggleChairs.Disabled", "${Prefix} &eChairs are now disabled until you leave the server or run the command again");

            yamlFile.addDefault("ChairUse.AlreadyOccupied", "${Prefix} &cThis chair is already occupied");
            yamlFile.addDefault("ChairUse.NeedsSignsOnBothSides", "${Prefix} &cA chair needs a sign attached to it on both sides");

            save = true;
        }

        if (save) {
            yamlFile.save();
        }

        return yamlFile;
    }

    protected static boolean reload() {
        return getMessages().refresh();
    }

    protected static void reset() {
        YAMLFileManager.removeFileFromCache(ChairManager.getPlugin(), getMessages());
    }

    // TODO: Duplicate method in Settings
    private static void backupConfig(YAMLFile yamlFile) {
        File file = yamlFile.getFile();

        YAMLFileManager.removeFileFromCache(ChairManager.getPlugin(), yamlFile);

        // Backup file
        File newFile = new File(file.getParentFile(), "messages-" + System.currentTimeMillis() + ".yml");
        if (file.renameTo(newFile)) {
            // TODO: Store console prefix in Messages.java (static/final)
            System.out.println(Messages.PREFIX_CONSOLE +
                    "Created backup of " + file.getName() + ": " + newFile.getName());
        } else {
            System.err.println(Messages.PREFIX_CONSOLE + "Could not create a backup of " + file.getName());
        }
    }
}