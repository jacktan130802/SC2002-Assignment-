package utility;

import entity.btoProject.BTOProject;
import entity.roles.HDBManager;

import java.util.List;
import java.util.stream.Collectors;

public class Filter {

    // Filter projects by manager
    public static List<BTOProject> filterByManager(List<BTOProject> projects, HDBManager manager) {
        if (projects == null || manager == null) {
            throw new IllegalArgumentException("Projects list or manager cannot be null.");
        }
        return projects.stream()
                .filter(p -> p.getManagerInCharge() != null && p.getManagerInCharge().equals(manager))
                .collect(Collectors.toList());
    }

    // Filter projects by neighborhood
    public static List<BTOProject> filterByNeighborhood(List<BTOProject> projects, String neighborhood) {
        if (projects == null || neighborhood == null) {
            throw new IllegalArgumentException("Projects list or neighborhood cannot be null.");
        }
        return projects.stream()
                .filter(p -> p.getNeighborhood() != null && p.getNeighborhood().equalsIgnoreCase(neighborhood))
                .collect(Collectors.toList());
    }

    // Filter projects by visibility
    public static List<BTOProject> filterByVisibility(List<BTOProject> projects, boolean isVisible) {
        if (projects == null) {
            throw new IllegalArgumentException("Projects list cannot be null.");
        }
        return projects.stream()
                .filter(p -> p.isVisible() == isVisible)
                .collect(Collectors.toList());
    }

    // Filter projects by status
    public static List<BTOProject> filterByStatus(List<BTOProject> projects, String status) {
        if (projects == null || status == null) {
            throw new IllegalArgumentException("Projects list or status cannot be null.");
        }
        return projects.stream()
                .filter(p -> p.getStatus() != null && p.getStatus().equalsIgnoreCase(status))
                .collect(Collectors.toList());
    }

    // Combine multiple filters (example: by manager and visibility)
    public static List<BTOProject> filterByManagerAndVisibility(List<BTOProject> projects, HDBManager manager, boolean isVisible) {
        if (projects == null || manager == null) {
            throw new IllegalArgumentException("Projects list or manager cannot be null.");
        }
        return projects.stream()
                .filter(p -> p.getManagerInCharge() != null && p.getManagerInCharge().equals(manager) && p.isVisible() == isVisible)
                .collect(Collectors.toList());
    }
}