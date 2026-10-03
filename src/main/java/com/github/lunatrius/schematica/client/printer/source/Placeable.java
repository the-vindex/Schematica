package com.github.lunatrius.schematica.client.printer.source;

/** How many of a stored stack may be placed, given how many the container insists on keeping. */
public final class Placeable {
    private Placeable() {
    }

    public static int count(final int stored, final int keep) {
        if (keep <= 0) {
            return Math.max(0, stored);
        }
        return stored > keep ? stored - keep : 0;
    }
}
