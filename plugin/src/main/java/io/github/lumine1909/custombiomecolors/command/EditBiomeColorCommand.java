package io.github.lumine1909.custombiomecolors.command;

import io.github.lumine1909.custombiomecolors.CustomBiomeColors;
import io.github.lumine1909.custombiomecolors.nms.BiomeAccessor;
import io.github.lumine1909.custombiomecolors.nms.ServerDataHandler;
import io.github.lumine1909.custombiomecolors.object.BiomeData;
import io.github.lumine1909.custombiomecolors.object.BiomeKey;
import io.github.lumine1909.custombiomecolors.object.ColorData;
import io.github.lumine1909.custombiomecolors.object.ColorType;
import io.github.lumine1909.custombiomecolors.util.Reflection;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class EditBiomeColorCommand implements TabExecutor {

    private static final ServerDataHandler<?, ?, ?> BIOME_DATA_HANDLER = CustomBiomeColors.getInstance().getServerDataHandler();

    private final Map<String, ColorType> supportedColors = new HashMap<>();

    public EditBiomeColorCommand() {
        Objects.requireNonNull(Bukkit.getPluginCommand("/editbiomecolor")).setExecutor(this);
        Objects.requireNonNull(Bukkit.getPluginCommand("/editbiomecolor")).setTabCompleter(this);
        for (Map.Entry<String, ColorType> entry : ColorType.BY_SERIALIZED_NAME.entrySet()) {
            if (entry.getValue().isSupported()) {
                supportedColors.put(entry.getKey(), entry.getValue());
            }
        }
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String @NotNull [] args) {
        if (args.length < 2) {
            return true;
        }

        BiomeKey key = BiomeKey.fromString(args[0]);
        if (!CustomBiomeColors.getInstance().getDataManager().hasBiome(key)) {
            sender.sendMessage(Component.text("[CustomBiomeColors] Invalid biome, it does not exist or is not a CBC generated biome!", NamedTextColor.RED));
            return true;
        }
        BiomeAccessor<?, ?, ?> oldBiome = BIOME_DATA_HANDLER.getBiomeFromKey(key);
        ColorData.Builder dataBuilder = oldBiome.getBiomeData().colorData().mutable();

        try {
            for (int i = 1; i < args.length; i++) {
                String[] currentArg = args[i].split("=");
                ColorType colorType = supportedColors.get(currentArg[0]);
                Integer color = args[0].equals("default") ? null : Integer.parseUnsignedInt(currentArg[1].replace("#", ""), 16);
                dataBuilder.set(colorType, color);
            }
        } catch (Exception e) {
            sender.sendMessage(Component.text("[CustomBiomeColors] Invalid color, please use valid color types and hex color codes!", NamedTextColor.RED));
            return true;
        }
        BiomeData.clearBiome(oldBiome);
        BiomeData newData = new BiomeData(oldBiome.getBiomeData().biomeKey(), oldBiome.getBiomeData().baseBiomeKey(), dataBuilder.build());
        BiomeAccessor<?, ?, ?> newBiome = BIOME_DATA_HANDLER.createCustomBiome(newData, false);Reflection.shallowCopy(oldBiome.getBiome(), newBiome.getBiome());
        BiomeData.updateBiome(newData.colorData(), newBiome);
        CustomBiomeColors.getInstance().getDataManager().saveBiome(newData.biomeKey(), newData);
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, String[] args) {
        if (!(sender instanceof Player)) {
            return Collections.emptyList();
        }
        if (args.length == 1) {
            return CustomBiomeColors.getInstance().getDataManager().getAllBiomeId();
        } else if (args.length >= 2) {
            Set<String> existingColors = new HashSet<>();
            for (int i = 1; i < args.length; i++) {
                String color = args[i].split("=")[0];
                if (supportedColors.containsKey(color)) {
                    existingColors.add(color);
                }
            }
            List<String> suggestion = new ArrayList<>();
            for (Map.Entry<String, ColorType> entry : supportedColors.entrySet()) {
                if (!existingColors.contains(entry.getKey())) {
                    suggestion.add(entry.getKey() + "=");
                }
            }
        }
        return Collections.emptyList();
    }
}
