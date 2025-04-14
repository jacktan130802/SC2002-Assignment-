package entity.btoProject;

import entity.roles.HDBManager;
import entity.roles.HDBOfficer;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BTOProject {
    private String projectName;
    private String neighborhood;
    private int twoRoomUnits;
    private int threeRoomUnits;
    private double priceTwoRoom;
    private double priceThreeRoom;
    private LocalDate openingDate;
    private LocalDate closingDate;
    private HDBManager managerInCharge;
    private boolean visibility;
    private int maxOfficerSlots;
    private int officerSlot;

    private List<HDBOfficer> registeredOfficers = new ArrayList<>();
    private List<HDBOfficer> approvedOfficers = new ArrayList<>();

    public BTOProject(String projectName, String neighborhood, int twoRoomUnits, double priceTwoRoom,
                      int threeRoomUnits, double priceThreeRoom, LocalDate openingDate, LocalDate closingDate,
                      HDBManager managerInCharge, int maxOfficerSlots) {
        this.projectName = projectName;
        this.neighborhood = neighborhood;
        this.twoRoomUnits = twoRoomUnits;
        this.priceTwoRoom = priceTwoRoom;
        this.threeRoomUnits = threeRoomUnits;
        this.priceThreeRoom = priceThreeRoom;
        this.openingDate = openingDate;
        this.closingDate = closingDate;
        this.managerInCharge = managerInCharge;
        this.maxOfficerSlots = maxOfficerSlots;
        this.visibility = false; // default
    }

    // getters
    public String getProjectName() {
        return projectName;
    }

    public String getNeighborhood() {
        return neighborhood;
    }

    public int getTwoRoomUnits() {
        return twoRoomUnits;
    }

    public int getThreeRoomUnits() {
        return threeRoomUnits;
    }

    public double getPriceTwoRoom() {
        return priceTwoRoom;
    }

    public double getPriceThreeRoom() {
        return priceThreeRoom;
    }

    public LocalDate getOpeningDate() {
        return openingDate;
    }

    public LocalDate getClosingDate() {
        return closingDate;
    }

    public HDBManager getManagerInCharge() {
        return managerInCharge;
    }

    public int getOfficerSlot() {
        return officerSlot;
    }

    public boolean isVisible() {
        return visibility;
    }

    public List<String> getRegisteredOfficersNRICs() {
        List<String> officerNRICs = new ArrayList<>();
        for (HDBOfficer officer : registeredOfficers) {
            officerNRICs.add(officer.getNRIC());
        }
        return officerNRICs;
    }
    public List<String> getApprovedOfficersNRICs() {
        List<String> nrics = new ArrayList<>();
        for (HDBOfficer officer : approvedOfficers) {
            nrics.add(officer.getNRIC());
        }
        return nrics;
    }
    

    // setters 
    public void setVisibility(boolean visibility) {
        this.visibility = visibility;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
    
    public void setNeighborhood(String neighborhood) {
        this.neighborhood = neighborhood;
    }
    
    public void setTwoRoomUnits(int twoRoomUnits) {
        this.twoRoomUnits = twoRoomUnits;
    }
    
    public void setThreeRoomUnits(int threeRoomUnits) {
        this.threeRoomUnits = threeRoomUnits;
    }
    

    public boolean isWithinApplicationPeriod(LocalDate date) {
        return !(date.isBefore(openingDate) || date.isAfter(closingDate));
    }

    public List<HDBOfficer> getRegisteredOfficers() {
        return registeredOfficers;
    }

    public List<HDBOfficer> getApprovedOfficers() {
        return approvedOfficers;
    }

    public boolean registerOfficer(HDBOfficer officer) {
        System.out.println("Attempting to register officer: " + officer.getNRIC());
        System.out.println("Current registered count: " + registeredOfficers.size() + "/" + officerSlot);
        
    
        if (registeredOfficers.contains(officer)) {
            System.out.println("Officer already registered (duplicate NRIC).");
            return false;
        }
    
        registeredOfficers.add(officer);
        return true;
    }
    
    public boolean approveOfficer(HDBOfficer officer) {
        if (registeredOfficers.contains(officer) && !approvedOfficers.contains(officer)) {
            if (approvedOfficers.size() < maxOfficerSlots) {
                approvedOfficers.add(officer);
                return true;
            }
        }
        return false;
    }

    public boolean isOfficerApproved(HDBOfficer officer) {
        return approvedOfficers.contains(officer);
    }

    public void updateFlatCount(String flatType) {
        if (flatType.equalsIgnoreCase("2-Room")) {
            twoRoomUnits--;
        } else if (flatType.equalsIgnoreCase("3-Room")) {
            threeRoomUnits--;
        }
    }

    public boolean hasTwoRoom() {
        return twoRoomUnits > 0;
    }

    public boolean hasThreeRoom() {
        return threeRoomUnits > 0;
    }

}
