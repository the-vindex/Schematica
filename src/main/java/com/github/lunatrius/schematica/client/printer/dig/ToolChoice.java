package com.github.lunatrius.schematica.client.printer.dig;

import java.util.List;

/** Fork: which inventory tool to mine a block with. */
public final class ToolChoice {
    /** One inventory stack, described for the block being mined. */
    public static final class Candidate {
        public final int slot;
        public final String name;
        /** Harvest level for {@link #toolClass}, or -1 when the stack is not that kind of tool. */
        public final int harvestLevel;
        public final String toolClass;
        public final float speed;
        /** Uses left, or -1 when the stack cannot be damaged. */
        public final int durabilityLeft;
        public final int fortune;
        public final boolean silk;

        public Candidate(final int slot, final String name, final int harvestLevel, final String toolClass, final float speed, final int durabilityLeft, final int fortune, final boolean silk) {
            this.slot = slot;
            this.name = name;
            this.harvestLevel = harvestLevel;
            this.toolClass = toolClass;
            this.speed = speed;
            this.durabilityLeft = durabilityLeft;
            this.fortune = fortune;
            this.silk = silk;
        }
    }

    /** What the block asks for: its harvest tool class (null = any) and level, and whether Fortune multiplies its drops. */
    public static final class Need {
        public final String toolClass;
        public final int harvestLevel;
        public final boolean fortuneUseful;

        public Need(final String toolClass, final int harvestLevel, final boolean fortuneUseful) {
            this.toolClass = toolClass;
            this.harvestLevel = harvestLevel;
            this.fortuneUseful = fortuneUseful;
        }
    }

    private ToolChoice() {
    }

    /**
     * Best capable, not-worn, non-silk tool: with {@code preferFortune} and a block whose drops Fortune multiplies, the
     * highest Fortune wins (then speed); otherwise the fastest; ties go to the lower slot. Null when nothing qualifies.
     */
    public static Candidate pick(final List<Candidate> tools, final Need need, final boolean preferFortune, final int minDurability) {
        final boolean fortuneFirst = preferFortune && need.fortuneUseful;
        Candidate best = null;
        for (final Candidate c : tools) {
            if (!capable(c, need) || c.silk || isWorn(c, minDurability)) {
                continue;
            }
            if (best == null || better(c, best, fortuneFirst)) {
                best = c;
            }
        }
        return best;
    }

    /** Name of a capable tool that was skipped only because it is worn (best by speed), or null. */
    public static String worn(final List<Candidate> tools, final Need need, final int minDurability) {
        Candidate best = null;
        for (final Candidate c : tools) {
            if (capable(c, need) && !c.silk && isWorn(c, minDurability) && (best == null || better(c, best, false))) {
                best = c;
            }
        }
        return best == null ? null : best.name;
    }

    private static boolean capable(final Candidate c, final Need need) {
        if (need.toolClass == null) {
            return true;
        }
        return need.toolClass.equals(c.toolClass) && c.harvestLevel >= need.harvestLevel;
    }

    private static boolean isWorn(final Candidate c, final int minDurability) {
        return c.durabilityLeft != -1 && c.durabilityLeft <= minDurability;
    }

    private static boolean better(final Candidate a, final Candidate b, final boolean fortuneFirst) {
        if (fortuneFirst && a.fortune != b.fortune) {
            return a.fortune > b.fortune;
        }
        if (a.speed != b.speed) {
            return a.speed > b.speed;
        }
        return a.slot < b.slot;
    }
}
