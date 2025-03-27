package entity.user;

import java.util.ArrayList;
import java.util.List;

import entity.btoProject.BTOProject;

import java.time.LocalDate;

import enums.UserRole;
public class Manager extends User {
	private List<BTOProject> projects;	// List of projects owned by manager

	public Manager(String nric, String password, UserRole role) {
		super(nric, password, role);
		this.projects = new ArrayList<>();
	}

	public BTOProject createProject(String projectName, String neighbourhood, String flatType, LocalDate openingDate, LocalDate closingDate) {
		
		BTOProject project = new BTOProject(projectName, neighbourhood, flatType, openingDate, closingDate, this);
		
		projects.add(project);
		return project;

	}

	public void editProject() {
		
	}

	public void deleteProject() {

	}
	
	public void toggleProjectVisibility() {
		
	}
	
	public void viewAllProjects() {
		
	}
	
	public void filterAllProjects() {
		
	}
	
	public void viewOwnProjects(DefaultHistory.projects) {
		
	}
	
	public void viewRegistrations() {
		
	}
	
	public void getApplicantList() {
		
	}
	
	public void approveOfficerRegistration() {
		
	}
	
	public void rejectOfficerRegistration() {
		
	}
	
	public void approveWithdrawal () {
		
	}
	
	public void getCreatedProjects(this.projects) {
		
	}
	
	public void viewAllEnquiries() {
		
	}
	
	public void replyToEnquiry() {
		
	}
	
}
