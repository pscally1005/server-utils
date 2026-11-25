package com.scally.serverutils.distribution;

import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class DistributionParser {

    private DistributionParser() {}

    public static Distribution parse(String distributionStr) {
        final String[] materials = distributionStr.split(",");

        final boolean allHavePercentage = doAllMaterialsHavePercentage(materials);
        final boolean noneHavePercentage = doNoMaterialsHavePercentage(materials);

        if (!allHavePercentage && !noneHavePercentage)
            throw new InvalidDistributionException("Materials must either all have percentages, or none!");

        matchAndValidateMaterials(materials);

        if (noneHavePercentage && !anyHasStackSize(materials)) {
            final Set<Material> materialSet = new HashSet<>();
            for (String materialStr : materials) {
                materialSet.add(parseMaterial(materialStr));
            }
            return new Distribution(materialSet);
        }

        final List<DistributionItem> distMaterials = parseRatios(materials, !noneHavePercentage);
        return new Distribution(distMaterials);
    }

    private static boolean doAllMaterialsHavePercentage(String[] materials) {
        return Arrays.stream(materials).allMatch(m -> m.contains("%"));
    }

    private static boolean doNoMaterialsHavePercentage(String[] materials) {
        return Arrays.stream(materials).noneMatch(m -> m.contains("%"));
    }

    private static boolean anyHasStackSize(String[] materials) {
        return Arrays.stream(materials).anyMatch(m -> extractStackSpec(m) != null);
    }

    private static String extractStackSpec(String materialStr) {
        String rest = materialStr;
        final int pct = rest.indexOf('%');
        if (pct >= 0) rest = rest.substring(pct + 1);
        final int colon = rest.indexOf(':');
        if (colon < 0) return null;
        final String spec = rest.substring(colon + 1).trim();
        return spec.isEmpty() ? null : spec;
    }

    private static void matchAndValidateMaterials(String[] materials) {
        final Set<Material> materialSet = new HashSet<>();
        for (String materialStr : materials) {
            final Material material = parseMaterial(materialStr);
            if (material == null)
                throw new InvalidDistributionException(String.format("Material %s not found!", materialStr));
            if (materialSet.contains(material))
                throw new InvalidDistributionException(String.format("Material %s was found twice!", materialStr));
            materialSet.add(material);
        }
    }

    private static Material parseMaterial(String materialStr) {
        String rest = materialStr;
        final int pct = rest.indexOf('%');
        if (pct >= 0) rest = rest.substring(pct + 1);
        final int colon = rest.indexOf(':');
        if (colon >= 0) rest = rest.substring(0, colon);
        rest = rest.trim();
        Material material = Material.matchMaterial(rest);
        if (material == null) {
            try {
                material = Material.valueOf(rest.toUpperCase());
            } catch (IllegalArgumentException ignored) {
            }
        }
        return material;
    }

    private static List<DistributionItem> parseRatios(String[] materials, boolean hasPercentages) {
        final List<DistributionItem> distMaterials = new ArrayList<>();
        double sum = 0D;

        for (String materialStr : materials) {
            double ratio;
            String materialAndStack;
            if (hasPercentages) {
                final String[] parts = materialStr.split("%", 2);
                if (parts.length != 2)
                    throw new InvalidDistributionException(
                            String.format("Found multiple percentage signs: %s", materialStr));
                try {
                    ratio = Double.parseDouble(parts[0]);
                } catch (NumberFormatException exception) {
                    throw new InvalidDistributionException(String.format("Found invalid number: %s", parts[0]));
                }
                materialAndStack = parts[1];
            } else {
                ratio = 1D;
                materialAndStack = materialStr;
            }

            final Material material = parseMaterial(materialAndStack);
            if (material == null)
                throw new InvalidDistributionException(String.format("Material %s not found!", materialStr));

            final int[] stack = parseStackSize(extractStackSpec(materialAndStack), materialStr);

            sum += ratio;
            distMaterials.add(new DistributionItem(material, sum - ratio, sum, stack[0], stack[1]));
        }
        return distMaterials;
    }

    private static int[] parseStackSize(String stackSpec, String original) {
        if (stackSpec == null || stackSpec.isEmpty()) {
            return new int[]{1, 1};
        }
        if (stackSpec.contains("-")) {
            final String[] range = stackSpec.split("-", 2);
            int min, max;
            try {
                min = Integer.parseInt(range[0].trim());
                max = Integer.parseInt(range[1].trim());
            } catch (NumberFormatException exception) {
                throw new InvalidDistributionException(String.format("Found invalid stack size: %s", stackSpec));
            }
            if (min < 1 || max < 1 || min > max) {
                throw new InvalidDistributionException(String.format("Invalid stack size range: %s", stackSpec));
            }
            if (max > 64) {
                throw new InvalidDistributionException(String.format("Stack size exceeds 64: %s", stackSpec));
            }
            return new int[]{min, max};
        }
        int amount;
        try {
            amount = Integer.parseInt(stackSpec.trim());
        } catch (NumberFormatException exception) {
            throw new InvalidDistributionException(String.format("Found invalid stack size: %s", stackSpec));
        }
        if (amount < 1 || amount > 64) {
            throw new InvalidDistributionException(String.format("Invalid stack size: %s", stackSpec));
        }
        return new int[]{amount, amount};
    }
}
