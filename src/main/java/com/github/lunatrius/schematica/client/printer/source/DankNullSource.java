package com.github.lunatrius.schematica.client.printer.source;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import p455w0rd.danknull.api.IDankNullHandler;
import p455w0rd.danknull.init.ModNetworking;
import p455w0rd.danknull.inventory.cap.CapabilityDankNull;
import p455w0rd.danknull.items.ItemDankNull;
import p455w0rd.danknull.network.PacketChangeMode;

/**
 * Blocks stored in a Dank/Null. Placement uses Dank/Null's own mechanics: the dank null is held, the wanted
 * stack is selected with its SELECTED packet, and the printer's normal right-click places the selected stack.
 * Only this class references Dank/Null; it is instantiated only when the mod is loaded.
 */
public class DankNullSource implements ItemSource {
    private static IDankNullHandler handler(final ItemStack stack) {
        if (stack.isEmpty() || !ItemDankNull.isDankNull(stack)) {
            return null;
        }
        return stack.getCapability(CapabilityDankNull.DANK_NULL_CAPABILITY, null);
    }

    /** Placeable count of {@code wanted} in one dank null, honouring its placement mode. */
    private static int placeable(final IDankNullHandler handler, final ItemStack wanted) {
        final int index = handler.findItemStack(wanted);
        if (index < 0) {
            return 0;
        }
        final ItemStack stored = handler.getFullStackInSlot(index);
        return Placeable.count(stored.getCount(), handler.getPlacementMode(stored).getNumberToKeep());
    }

    @Override
    public int count(final EntityPlayer player, final ItemStack wanted) {
        int total = 0;
        for (final ItemStack stack : player.inventory.mainInventory) {
            final IDankNullHandler handler = handler(stack);
            if (handler != null) {
                total += placeable(handler, wanted);
            }
        }
        return total;
    }

    @Override
    public boolean prepare(final EntityPlayerSP player, final ItemStack wanted, final HotbarSwapper swapper) {
        if (Minecraft.getMinecraft().currentScreen != null) {
            return false; // a screen (maybe Dank/Null's own) is open: inventory clicks would go to the wrong window
        }
        for (int slot = 0; slot < player.inventory.mainInventory.size(); slot++) {
            final IDankNullHandler found = handler(player.inventory.mainInventory.get(slot));
            if (found == null || placeable(found, wanted) <= 0) {
                continue;
            }

            final int hotbar = slot < 9 ? slot : swapper.toHotbar(slot);
            if (hotbar < 0) {
                return false;
            }
            player.inventory.currentItem = hotbar;
            // Tell the server which slot is held before Dank/Null's SELECTED packet, which acts on the main hand.
            Minecraft.getMinecraft().playerController.updateController();

            final IDankNullHandler held = handler(player.inventory.getStackInSlot(hotbar));
            if (held == null) {
                return false;
            }
            final int index = held.findItemStack(wanted);
            if (index < 0) {
                return false;
            }
            if (held.getSelected() != index) {
                held.setSelected(index);
                ModNetworking.getInstance().sendToServer(new PacketChangeMode(PacketChangeMode.ChangeType.SELECTED, index, false, EnumHand.MAIN_HAND));
            }
            return true;
        }
        return false;
    }
}
