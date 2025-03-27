package entity.btoProject;

import java.time.LocalDate;

import enums.FlatType;

public class BTOProject {
    private String projectName;
    private String neighbourhood;
    private FlatType flatType;
    private int numUnits;
    private LocalDate openingDate;
    private LocalDate closingDate;
    private Manager managerInCharge;
    private int availableOfficerSlots;

    public BTOProject(String projectName, String neighbourhood, FlatType flatType, LocalDate openingDate, LocalDate closingDate, Manager managerInCharge) {
        this.projectName = projectName;
        this.neighbourhood = neighbourhood;
        this.flatType = flatType;
        this.numUnits = 0;
        this.openingDate = openingDate;
        this.closingDate = closingDate;
        this.managerInCharge = managerInCharge;
        this.availableOfficerSlots = 10;    // Max 10
    }
}
