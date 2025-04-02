package entity.flat;

import entity.btoProject.BTOProject;

public class Flat {
    private String flatType;
    private String projectName;
    private String neighborhood;
    private boolean availabilityStatus;
    private int unitsTotal;
    private double price;

    public Flat(BTOProject project, String flatType, boolean availabilityStatus, int unitsTotal, double price){
        this.projectName = project.projectName;
        this.neighbourhood = project.neighborhood;
        this.availabilityStatus = availabilityStatus;
        this.price = price;
    }

    }

    
}
