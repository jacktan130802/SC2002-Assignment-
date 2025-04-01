package entity.btoProject;

import entity.roles.HDBOfficer;
import entity.roles.HDBManager;
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

    public boolean isVisible() {
        return visibility;
    }

    public void setVisibility(boolean visibility) {
        this.visibility = visibility;
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
        if (!registeredOfficers.contains(officer)) {
            registeredOfficers.add(officer);
            return true;
        }
        return false;
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
