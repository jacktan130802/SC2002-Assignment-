package utility;

import entity.btoProject.BTOProject;

import java.util.List;
import java.util.stream.Collectors;

public class Filter {

    public static List<BTOProject> dynamicFilter(List<BTOProject> projects, String neighborhood, String flatType, Double minPrice, Double maxPrice) {
        if (projects == null) {
            throw new IllegalArgumentException("Projects list cannot be null.");
        }

        return projects.stream()
                .filter(p -> (neighborhood == null || neighborhood.isEmpty() || p.getNeighborhood().equalsIgnoreCase(neighborhood)))
                .filter(p -> (flatType == null || flatType.isEmpty() || p.getFlatType().equalsIgnoreCase(flatType)))
                .filter(p -> (minPrice == null || p.getPrice() >= minPrice))
                .filter(p -> (maxPrice == null || p.getPrice() <= maxPrice))
                .collect(Collectors.toList());
    }
}