package utility;

import entity.Application;
import enums.FlatType;
import enums.MaritalStatus;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class for filtering reports based on various criteria.
 */
public class ReportFilter {

    /**
     * Filters applications by marital status.
     *
     * @param apps   The list of applications to filter.
     * @param status The marital status to filter by.
     * @return A list of applications matching the specified marital status.
     */
    public static List<Application> filterByMaritalStatus(List<Application> apps, MaritalStatus status) {
        return apps.stream()
                .filter(app -> app.getApplicant().getMaritalStatus() == status)
                .collect(Collectors.toList());
    }

    /**
     * Filters applications by flat type.
     *
     * @param apps     The list of applications to filter.
     * @param flatType The flat type to filter by.
     * @return A list of applications matching the specified flat type.
     */
    public static List<Application> filterByFlatType(List<Application> apps, FlatType flatType) {
        return apps.stream()
                .filter(app -> app.getFlatType() == flatType)
                .collect(Collectors.toList());
    }

    /**
     * Filters applications by project name.
     *
     * @param apps        The list of applications to filter.
     * @param projectName The project name to filter by.
     * @return A list of applications matching the specified project name.
     */
    public static List<Application> filterByProjectName(List<Application> apps, String projectName) {
        return apps.stream()
                .filter(app -> app.getProject().getProjectName().equalsIgnoreCase(projectName))
                .collect(Collectors.toList());
    }

    /**
     * Filters applications by an age range.
     *
     * @param apps   The list of applications to filter.
     * @param minAge The minimum age (inclusive).
     * @param maxAge The maximum age (inclusive).
     * @return A list of applications where the applicant's age is within the specified range.
     */
    public static List<Application> filterByAgeRange(List<Application> apps, int minAge, int maxAge) {
        return apps.stream()
                .filter(app -> {
                    int age = app.getApplicant().getAge();
                    return age >= minAge && age <= maxAge;
                })
                .collect(Collectors.toList());
    }
}
