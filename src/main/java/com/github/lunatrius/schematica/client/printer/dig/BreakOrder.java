package com.github.lunatrius.schematica.client.printer.dig;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.Collection;

/** Fork: which planned block to dig next. */
public final class BreakOrder {
    private BreakOrder() {
    }

    /**
     * Picks the next block to dig: only blocks within {@code reach} of the eyes (to the block centre), never the block
     * under the player's feet, the topmost remaining block of each (x,z) column first, nearest column first; ties are
     * broken by x, then z, then y so the choice is deterministic. Returns null when nothing is in reach.
     */
    public static BlockPos next(final Collection<BlockPos> toDig, final BlockPos playerFeet, final double reach, final Vec3d eye) {
        final BlockPos underFeet = playerFeet.down();
        final double reachSq = reach * reach;
        BlockPos best = null;
        double bestDist = Double.MAX_VALUE;
        for (final BlockPos p : toDig) {
            if (p.equals(underFeet) || distSq(p, eye) > reachSq || hasCandidateAbove(toDig, p, underFeet)) {
                continue;
            }
            final double d = distSq(p, eye);
            if (best == null || d < bestDist - 1e-9 || (Math.abs(d - bestDist) <= 1e-9 && tieBefore(p, best))) {
                best = p;
                bestDist = d;
            }
        }
        return best;
    }

    private static boolean hasCandidateAbove(final Collection<BlockPos> toDig, final BlockPos p, final BlockPos underFeet) {
        for (final BlockPos q : toDig) {
            if (q.getX() == p.getX() && q.getZ() == p.getZ() && q.getY() > p.getY() && !q.equals(underFeet)) {
                return true;
            }
        }
        return false;
    }

    private static boolean tieBefore(final BlockPos a, final BlockPos b) {
        if (a.getX() != b.getX()) {
            return a.getX() < b.getX();
        }
        if (a.getZ() != b.getZ()) {
            return a.getZ() < b.getZ();
        }
        return a.getY() > b.getY();
    }

    private static double distSq(final BlockPos p, final Vec3d eye) {
        final double dx = p.getX() + 0.5 - eye.x;
        final double dy = p.getY() + 0.5 - eye.y;
        final double dz = p.getZ() + 0.5 - eye.z;
        return dx * dx + dy * dy + dz * dz;
    }
}
