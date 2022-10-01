package com.scally.serverutils.distribution;

import org.bukkit.Material;

import java.util.*;

public final class DistributionParser {

    private DistributionParser() {}

    /**
     * @param distributionStr arg from command
     * @return Distribution object if valid
     * @throws InvalidDistributionException if arg is invalid
     */
    public static Distribution parse(String distributionStr) {
        final String[] materials = distributionStr.split(",");

        final boolean allHavePercentage = doAllMaterialsHavePercentage(materials);
        final boolean noneHavePercentage = doNoMaterialsHavePercentage(materials);

        if (!allHavePercentage && !noneHavePercentage)
            throw new InvalidDistributionException("Materials must either all have percentages, or none!");

        final Set<Material> materialSet = matchAndValidateMaterials(materials);

        if (noneHavePercentage)
            return new Distribution(materialSet);

        final List<DistributionMaterial> distMaterials = parseRatios(materials);
        return new Distribution(distMaterials);
    }

    private static boolean doAllMaterialsHavePercentage(String[] materials) {
        return Arrays.stream(materials).allMatch(m -> m.contains("%"));
    }

    private static boolean doNoMaterialsHavePercentage(String[] materials) {
        return Arrays.stream(materials).noneMatch(m -> m.contains("%"));
    }

    private static Set<Material> matchAndValidateMaterials(String[] materials) {
        final Set<Material> materialSet = new HashSet<>();
        for (String materialStr : materials) {
            final String[] parts = materialStr.split("%");
            final Material material = Material.matchMaterial(parts[parts.length - 1]);
            if (material == null)
                throw new InvalidDistributionException(String.format("Material %s not found!", materialStr));
            if (materialSet.contains(material))
                throw new InvalidDistributionException(String.format("Material %s was found twice!", materialStr));
            materialSet.add(material);
        }
        return materialSet;
    }

    private static List<DistributionMaterial> parseRatios(String[] materials) {
        final List<DistributionMaterial> distMaterials = new ArrayList<>();
        double sum = 0D;
        double previous;

        for (String materialStr : materials) {
            final String[] parts = materialStr.split("%");
            if (parts.length != 2)
                throw new InvalidDistributionException(
                        String.format("Found multiple percentage signs: %s", materialStr));

            double ratio;
            try {
                ratio = Double.parseDouble(parts[0]);
            } catch (NumberFormatException exception) {
                throw new InvalidDistributionException(String.format("Found invalid number: %s", parts[0]));
            }

            final Material material = Material.matchMaterial(parts[1]);
            previous = sum;
            sum += ratio;

            distMaterials.add(new DistributionMaterial(material, previous, sum));
        }
        return distMaterials;
    }
}
