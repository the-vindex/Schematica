package com.github.lunatrius.schematica.client.printer;

import com.github.lunatrius.schematica.handler.ConfigurationHandler;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.IMob;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import java.util.List;

/** Fork: pauses the printer while the player is hurt or hostile mobs are close, and resumes when it is quiet. */
public final class SafetyMonitor {
    public static final SafetyMonitor INSTANCE = new SafetyMonitor();

    private SafetyState state = new SafetyState(ConfigurationHandler.RESUME_AFTER_TICKS_DEFAULT);
    private int stateResumeTicks = ConfigurationHandler.RESUME_AFTER_TICKS_DEFAULT;

    private SafetyMonitor() {
    }

    /** Call every client tick while printing; returns true when placement must be skipped this tick. */
    public boolean blocksPrinting(final EntityPlayerSP player, final World world) {
        if (this.stateResumeTicks != ConfigurationHandler.resumeAfterTicks) {
            this.stateResumeTicks = ConfigurationHandler.resumeAfterTicks;
            this.state = new SafetyState(this.stateResumeTicks);
        }
        final SafetyState.Transition t = this.state.tick(danger(player, world));
        if (t == SafetyState.Transition.PAUSED) {
            notify(player, "printer paused: " + this.state.reason(), TextFormatting.GOLD);
        } else if (t == SafetyState.Transition.RESUMED) {
            notify(player, "printer resumed", TextFormatting.GREEN);
        }
        return this.state.isPaused();
    }

    /** Clears a pause, e.g. when the player toggles the printer. */
    public void reset() {
        this.state.reset();
    }

    public boolean isPaused() {
        return this.state.isPaused();
    }

    private static String danger(final EntityPlayerSP player, final World world) {
        if (ConfigurationHandler.pauseOnDamage && player.hurtTime > 0) {
            return "you took damage";
        }
        final int r = ConfigurationHandler.hostileRadius;
        if (r > 0) {
            final List<EntityLivingBase> near = world.getEntitiesWithinAABB(EntityLivingBase.class, player.getEntityBoundingBox().grow(r));
            for (final EntityLivingBase e : near) {
                if (e instanceof IMob && e.isEntityAlive() && e.getDistanceSq(player) <= (double) r * r) {
                    return e.getName() + " nearby";
                }
            }
        }
        return null;
    }

    private static void notify(final EntityPlayerSP player, final String text, final TextFormatting color) {
        final TextComponentString msg = new TextComponentString("[Schematica] " + text);
        msg.getStyle().setColor(color);
        player.sendMessage(msg);
    }
}
