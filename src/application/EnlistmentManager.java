package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import model.Course;
import model.CoursePlanner;

// manages the course enlistment ( adding/removing courses and displaying said courses)
public class EnlistmentManager {
	private TableView<Course> enrolledTable;
	private CoursePlanner planner;
	private TableView<Course> offeringsTable; 
	private ObservableList<Course> enrolledCourses;
	private StudentDashboard dashboard;
	
	//constructor
	public EnlistmentManager(CoursePlanner planner, TableView<Course> offeringsTable, StudentDashboard dashboard) {
		this.planner = planner;
		this.offeringsTable = offeringsTable;
		this.enrolledCourses = FXCollections.observableArrayList(planner.getEnrolledCourses());
		this.dashboard = dashboard;
	}
	
	//creates Vbox containing enrolled courses table and add/remove buttons
	 public VBox createEnlistmentPane() { 
		 VBox content = new VBox(10);
		 content.setPadding(new Insets(20));

		 enrolledTable = new TableView<>(); 
		 enrolledTable.setItems(enrolledCourses);
		 enrolledTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		 TableColumn<Course, String> codeCol = new TableColumn<>("Course Code");
		 codeCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getCourseCode()));
		 TableColumn<Course, String> titleCol = new TableColumn<>("Title");
		 titleCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getCourseTitle()));
		 enrolledTable.getColumns().addAll(codeCol, titleCol);

		 Button addButton = new Button("Add Course");
		 Button removeButton = new Button("Remove Course");
		 HBox buttonBox = new HBox(10, addButton, removeButton);
		 buttonBox.setAlignment(Pos.CENTER);

		 addButton.setOnAction(e -> addSelectedCourse());
		 removeButton.setOnAction(e -> removeSelectedCourse());

		 content.getChildren().addAll(new Label("Enrolled Courses:"), enrolledTable, buttonBox);
		 VBox.setVgrow(enrolledTable, Priority.ALWAYS);

		 refreshEnrolledTable();
		 return content;
	 }
	 
	 // adds selected course from offerings table to enrolled table if it has no conflict or duplicate
	 private void addSelectedCourse() {
		 Course selected = offeringsTable.getSelectionModel().getSelectedItem();
		 
		 if (selected != null) {
			 if (planner.hasConflict(selected))	{
				 Alert alert = new Alert(Alert.AlertType.WARNING);
				 alert.setTitle("Conflict");
				 alert.setHeaderText(null);
				 alert.setContentText("This course conflicts with your existing schedule!");
				 alert.showAndWait();
			 }else {
				    boolean conflictSameType = false;
				    
				    for (Course c : planner.getEnrolledCourses()) {
				        // check if the course code is the same
				        if (c.getCourseCode().equals(selected.getCourseCode())) {
				            //check if both are lectures or both are labs
				            if (StudentDashboard.isLab(c.getSection()) == StudentDashboard.isLab(selected.getSection())) {
				                conflictSameType = true;
				                break;
				            }
				        }
				    }
				    
				    if (conflictSameType) {
				        Alert alert = new Alert(Alert.AlertType.WARNING);
				        alert.setTitle("Duplicate");
				        alert.setHeaderText(null);
				        alert.setContentText("You are already enrolled in this course type (lecture or lab)!");
				        alert.showAndWait();
				    } else {
				    	System.out.println("Trying to add: " + selected.getCourseCode() + " | " + selected.getSection());
				        planner.addCourse(selected);
				        System.out.println("Enrolled now: ");
				        for (Course c : planner.getEnrolledCourses()) {
				            System.out.println(c.getCourseCode() + " | " + c.getSection());
				        }
				        refreshEnrolledTable();
				        dashboard.refreshCalendar();
				    }
				}
		 }
		
	 }
	 
	 //removes selected course
	 private void removeSelectedCourse() {
		 Course selected = enrolledTable.getSelectionModel().getSelectedItem();
		 
		 if (selected != null) {
			 planner.removeCourse(selected);
			 refreshEnrolledTable();
			 dashboard.refreshCalendar();
		 }
	 }
	
	 //refreshes enrolled courses table from planner data
	 private void refreshEnrolledTable() {
		 enrolledCourses.setAll(planner.getEnrolledCourses());
	 }
}
