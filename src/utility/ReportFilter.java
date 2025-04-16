package utility;

import entity.Application;
import enums.FlatType;
import enums.MaritalStatus;

import java.util.List;
import java.util.stream.Collectors;

public class ReportFilter {

    public static List<Application> filterByMaritalStatus(List<Application> apps, MaritalStatus status) {
        return apps.stream()
                .filter(app -> app.getApplicant().getMaritalStatus() == status)
                .collect(Collectors.toList());
    }

    public static List<Application> filterByFlatType(List<Application> apps, FlatType flatType) {
        return apps.stream()
                .filter(app -> app.getFlatType() == flatType)
                .collect(Collectors.toList());
    }

    public static List<Application> filterByProjectName(List<Application> apps, String projectName) {
        return apps.stream()
                .filter(app -> app.getProject().getProjectName().equalsIgnoreCase(projectName))
                .collect(Collectors.toList());
    }

    public static List<Application> filterByAgeRange(List<Application> apps, int minAge, int maxAge) {
        return apps.stream()
                .filter(app -> {
                    int age = app.getApplicant().getAge();
                    return age >= minAge && age <= maxAge;
                })
                .collect(Collectors.toList());
    }
}