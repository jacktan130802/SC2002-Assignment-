package entity.btoProject;

import enums.FlatType;
import entity.roles.HDBManager;
import entity.roles.HDBOfficer;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Build-To-Order (BTO) project with details about flats, officers, and application periods.
 */
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
    private String status;

    private List<HDBOfficer> registeredOfficers = new ArrayList<>();
    private List<HDBOfficer> approvedOfficers = new ArrayList<>();

    /**
     * Constructs a BTOProject instance.
     *
     * @param projectName      The name of the project.
     * @param neighborhood     The neighborhood where the project is located.
     * @param twoRoomUnits     The number of 2-room units available.
     * @param priceTwoRoom     The price of a 2-room unit.
     * @param threeRoomUnits   The number of 3-room units available.
     * @param priceThreeRoom   The price of a 3-room unit.
     * @param openingDate      The application opening date.
     * @param closingDate      The application closing date.
     * @param managerInCharge  The manager in charge of the project.
     * @param maxOfficerSlots  The maximum number of officer slots.
     */
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

    /**
     * Checks if the given date is within the application's opening and closing period.
     *
     * @param date The date to check.
     * @return True if the date is within the application period, false otherwise.
     */
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

    /**
     * Updates the flat count for the specified flat type.
     *
     * @param flatType The type of flat to update.
     */
    public void updateFlatCount(FlatType flatType) {
        if (flatType == FlatType.TWO_ROOM) {
            twoRoomUnits--;
            System.out.println("[DEBUG] 2-Room units decremented. Remaining: " + twoRoomUnits);
        } else if (flatType == FlatType.THREE_ROOM) {
            threeRoomUnits--;
            System.out.println("[DEBUG] 3-Room units decremented. Remaining: " + threeRoomUnits);
        } else {
            System.out.println("[DEBUG] Unknown flat type passed to updateFlatCount: " + flatType);
        }
    }

    /**
     * Checks if there are available 2-room units.
     *
     * @return True if there are 2-room units available, false otherwise.
     */
    public boolean hasTwoRoom() {
        return twoRoomUnits > 0;
    }

    /**
     * Checks if there are available 3-room units.
     *
     * @return True if there are 3-room units available, false otherwise.
     */
    public boolean hasThreeRoom() {
        return threeRoomUnits > 0;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setPriceTwoRoom(double priceTwoRoom) {
        this.priceTwoRoom = priceTwoRoom;
    }

    public void setPriceThreeRoom(double priceThreeRoom) {
        this.priceThreeRoom = priceThreeRoom;
    }

    public void setOpeningDate(LocalDate openingDate) {
        this.openingDate = openingDate;
    }

    public void setClosingDate(LocalDate closingDate) {
        this.closingDate = closingDate;
    }
}
