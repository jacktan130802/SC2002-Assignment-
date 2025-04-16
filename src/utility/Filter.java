package utility;

import entity.btoProject.BTOProject;
import entity.roles.HDBManager;
import enums.FlatType;

import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Filter {

    // === Your existing dynamicFilter logic ===
    public static List<BTOProject> dynamicFilter(List<BTOProject> projects, String neighborhood, FlatType flatType, Double minPrice, Double maxPrice) {
        if (projects == null) {
            throw new IllegalArgumentException("Projects list cannot be null.");
        }

        return projects.stream()
                .filter(p -> neighborhood == null || neighborhood.isEmpty() || p.getNeighborhood().equalsIgnoreCase(neighborhood))
                .filter(p -> {
                    if (flatType == null) {
                        return p.getTwoRoomUnits() > 0 || p.getThreeRoomUnits() > 0;
                    }
                    return (flatType == FlatType.TWO_ROOM && p.getTwoRoomUnits() > 0)
                            || (flatType == FlatType.THREE_ROOM && p.getThreeRoomUnits() > 0);
                })
                .filter(p -> {
                    if (flatType == null) {
                        boolean matchesTwo = (minPrice == null || p.getPriceTwoRoom() >= minPrice) &&
                                             (maxPrice == null || p.getPriceTwoRoom() <= maxPrice);
                        boolean matchesThree = (minPrice == null || p.getPriceThreeRoom() >= minPrice) &&
                                               (maxPrice == null || p.getPriceThreeRoom() <= maxPrice);
                        return matchesTwo || matchesThree;
                    }

                    if (flatType == FlatType.TWO_ROOM) {
                        return (minPrice == null || p.getPriceTwoRoom() >= minPrice) &&
                               (maxPrice == null || p.getPriceTwoRoom() <= maxPrice);
                    } else if (flatType == FlatType.THREE_ROOM) {
                        return (minPrice == null || p.getPriceThreeRoom() >= minPrice) &&
                               (maxPrice == null || p.getPriceThreeRoom() <= maxPrice);
                    }

                    return false;
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

    // === Embedded FilterSettings static inner class ===
    public static class FilterSettings {
        private String neighborhood;
        private FlatType flatType;
        private Double minPrice;
        private Double maxPrice;

        public FilterSettings(String neighborhood, FlatType flatType, Double minPrice, Double maxPrice) {
            this.neighborhood = neighborhood;
            this.flatType = flatType;
            this.minPrice = minPrice;
            this.maxPrice = maxPrice;
        }

        public String getNeighborhood() { return neighborhood; }
        public FlatType getFlatType() { return flatType; }
        public Double getMinPrice() { return minPrice; }
        public Double getMaxPrice() { return maxPrice; }

        public String toCSVLine() {
            return String.format("%s,%s,%s,%s",
                    neighborhood == null ? "" : neighborhood,
                    flatType == null ? "" : (flatType == FlatType.TWO_ROOM ? "2" : "3"),
                    minPrice == null ? "" : minPrice,
                    maxPrice == null ? "" : maxPrice);
        }

        public static FilterSettings fromCSVLine(String line) {
            String[] parts = line.split(",", -1);
            String neighborhood = parts[0].isEmpty() ? null : parts[0];
            FlatType flatType = parts[1].equals("2") ? FlatType.TWO_ROOM :
                                parts[1].equals("3") ? FlatType.THREE_ROOM : null;
            Double minPrice = parts[2].isEmpty() ? null : Double.parseDouble(parts[2]);
            Double maxPrice = parts[3].isEmpty() ? null : Double.parseDouble(parts[3]);
            return new FilterSettings(neighborhood, flatType, minPrice, maxPrice);
        }
    }

    public static FilterSettings promptAndSaveFilterSettings(Scanner sc) {
    System.out.print("Enter neighborhood to filter by (or leave blank for any): ");
    String neighborhood = sc.nextLine().trim();

    System.out.print("Enter flat type to filter by (2 for 2-Room, 3 for 3-Room, or leave blank for any): ");
    String flatTypeInput = sc.nextLine().trim();
    FlatType flatType = null;
    if (!flatTypeInput.isEmpty()) {
        flatType = flatTypeInput.equals("2") ? FlatType.TWO_ROOM :
                   flatTypeInput.equals("3") ? FlatType.THREE_ROOM : null;
    }

    System.out.print("Enter minimum price (or leave blank for no minimum): ");
    String minPriceInput = sc.nextLine().trim();
    Double minPrice = minPriceInput.isEmpty() ? null : Double.parseDouble(minPriceInput);

    System.out.print("Enter maximum price (or leave blank for no maximum): ");
    String maxPriceInput = sc.nextLine().trim();
    Double maxPrice = maxPriceInput.isEmpty() ? null : Double.parseDouble(maxPriceInput);

    FilterSettings settings = new FilterSettings(neighborhood, flatType, minPrice, maxPrice);
    controller.Database.saveFilterSettings(settings); // call save from Database

    return settings;
}

}
