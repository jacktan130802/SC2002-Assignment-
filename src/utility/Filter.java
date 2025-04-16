package utility;

import entity.btoProject.BTOProject;
import entity.roles.HDBManager;
import enums.FlatType;

import java.util.List;
import java.util.stream.Collectors;

public class Filter {


    public static List<BTOProject> dynamicFilter(List<BTOProject> projects, String neighborhood, FlatType flatType, Double minPrice, Double maxPrice) {
        if (projects == null) {
            throw new IllegalArgumentException("Projects list cannot be null.");
        }

        return projects.stream()
                .filter(p -> neighborhood == null || neighborhood.isEmpty() || p.getNeighborhood().equalsIgnoreCase(neighborhood))
                .filter(p -> {
                    if (flatType == null) {
                        return true; // No flat type filter
                    }
                    if (flatType == FlatType.TWO_ROOM) {
                        return p.getTwoRoomUnits() > 0; // Check if 2-Room units are available
                    } else if (flatType == FlatType.THREE_ROOM) {
                        return p.getThreeRoomUnits() > 0; // Check if 3-Room units are available
                    }
                    return false; // Invalid flat type
                })
                .filter(p -> {
                    if (minPrice == null && maxPrice == null) {
                        return true; // No price filter
                    }
                    if (flatType == null) {
                        return true; // No specific flat type, skip price filtering
                    }
                    if (flatType == FlatType.TWO_ROOM) {
                        return (minPrice == null || p.getPriceTwoRoom() >= minPrice) &&
                                (maxPrice == null || p.getPriceTwoRoom() <= maxPrice);
                    } else if (flatType == FlatType.THREE_ROOM) {
                        return (minPrice == null || p.getPriceThreeRoom() >= minPrice) &&
                                (maxPrice == null || p.getPriceThreeRoom() <= maxPrice);
                    }
                    return false; // Invalid flat type
                })
                .collect(Collectors.toList());
    }

    public static List<BTOProject> filterByManager(List<BTOProject> projects, HDBManager manager) {
        if (projects == null || manager == null) {
            throw new IllegalArgumentException("Projects list or manager cannot be null.");
        }
        return projects.stream()
                .filter(p -> p.getManagerInCharge() != null && p.getManagerInCharge().equals(manager))
                .collect(Collectors.toList());
    }

}