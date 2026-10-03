package com.github.lunatrius.schematica.client.printer.source;

import com.github.lunatrius.schematica.reference.Reference;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;

import java.util.ArrayList;
import java.util.List;

/** Optional item sources, registered only when the providing mod is installed. */
public final class ItemSources {
    private static List<ItemSource> sources;

    private ItemSources() {
    }

    private static List<ItemSource> sources() {
        if (sources == null) {
            final List<ItemSource> list = new ArrayList<ItemSource>();
            if (Loader.isModLoaded("danknull")) {
                // Loaded reflectively so Dank/Null classes are never touched when the mod is absent.
                try {
                    list.add((ItemSource) Class.forName("com.github.lunatrius.schematica.client.printer.source.DankNullSource").newInstance());
                    Reference.logger.info("Printer: Dank/Null support enabled");
                } catch (final Throwable t) {
                    Reference.logger.error("Printer: could not enable Dank/Null support", t);
                }
            }
            sources = list;
        }
        return sources;
    }

    public static int count(final EntityPlayer player, final ItemStack wanted) {
        int total = 0;
        for (final ItemSource source : sources()) {
            total += source.count(player, wanted);
        }
        return total;
    }

    public static boolean prepare(final EntityPlayerSP player, final ItemStack wanted, final ItemSource.HotbarSwapper swapper) {
        for (final ItemSource source : sources()) {
            if (source.prepare(player, wanted, swapper)) {
                return true;
            }
        }
        return false;
    }
}
