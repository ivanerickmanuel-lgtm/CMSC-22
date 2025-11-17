package application;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import handler.FileManager;
import model.Course;
import model.CoursePlanner;
import model.Student;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// Main dashboard view for students to browse course offerings and program curriculum

public class StudentDashboard {
    private Scene scene;
    private Stage stage;
    private BorderPane root;
    private Student currentStudent;
    private FileManager fileManager;
    private EnlistmentManager enlistmentManager;
    private CoursePlanner planner;
    private GridPane calendarGrid;
    private VBox calendarInfoPane;
    
    
    
    public StudentDashboard(Student student) {
        this.currentStudent = student;
        this.fileManager = new FileManager();
        this.root = new BorderPane();
        this.scene = new Scene(root, 1280, 720);
        
        planner = new CoursePlanner();
        planner.setEnrolledCourses(currentStudent.getEnrolledCourses());
        setProperties();
    }

    // Sets up the main dashboard layout with header and tabs
    private void setProperties() {
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #f5f5f5;");

        // Setup header with welcome message and logout button
     // Setup header with welcome message and logout button
        HBox topBox = new HBox(20);
        topBox.setPadding(new Insets(0, 0, 10, 0));

        VBox welcomeBox = new VBox(5);
        Label welcomeLabel = new Label("Welcome, " + currentStudent.getFullName() + "!");
        welcomeLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        Label programLabel = new Label(currentStudent.getProgram());
        programLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");
        welcomeBox.getChildren().addAll(welcomeLabel, programLabel);

        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(e -> handleLogout());

        topBox.getChildren().addAll(welcomeBox, logoutButton);
        root.setTop(topBox);

        // Setup tab pane with Enlistment and Course List tabs
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        Tab enlistmentTab = new Tab("Enlistment");
        enlistmentTab.setContent(createEnlistmentContent());

        Tab courseListTab = new Tab("Course List");
        courseListTab.setContent(createCourseListContent());
        
        Tab calendarTab = new Tab("Calendar");
        calendarTab.setContent(createCalendarContent());

        tabPane.getTabs().addAll(courseListTab, enlistmentTab, calendarTab);
        root.setCenter(tabPane);
    }
    
    private VBox createCalendarContent() {
        VBox content = new VBox(10);
        content.setPadding(new Insets(20));

        Label titleLabel = new Label("Weekly Schedule");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Scrollable calendar grid
        ScrollPane scrollPane = new ScrollPane();
        calendarGrid = new GridPane();
        calendarGrid.setGridLinesVisible(true);
        calendarGrid.setHgap(5);
        calendarGrid.setVgap(5);
        calendarGrid.setPadding(new Insets(10));

        scrollPane.setContent(calendarGrid);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);

        setupGrid(); // adds header row and column
        
        //populates calendar with enrolled courses
        planner.getEnrolledCourses().clear();
        planner.getEnrolledCourses().addAll(currentStudent.getEnrolledCourses());
        fillCalendar();

        // Course info panel
        calendarInfoPane = new VBox(5);
        calendarInfoPane.setPadding(new Insets(10));
        calendarInfoPane.setStyle("-fx-border-color: #ccc; -fx-border-width: 1px;");
        calendarInfoPane.getChildren().add(new Label("Select a course block to see details."));

        content.getChildren().addAll(titleLabel, scrollPane, calendarInfoPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        return content;
    }
    // Creates the enlistment tab showing course offerings with schedule information
    private VBox createEnlistmentContent() {
        VBox content = new VBox(10);
        content.setPadding(new Insets(20));

        Label titleLabel = new Label("Course Offerings - 1S 2025-2026");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // filter dropdown
        HBox filterBox = new HBox(10);
        filterBox.setAlignment(Pos.CENTER_LEFT);
        
        Label filterLabel = new Label("Filter by Program:");
        ComboBox<String> programCombo = new ComboBox<>();
        programCombo.getItems().addAll(
            "All Programs",
            "BS Computer Science",
            "MS Computer Science",
            "Master of Information Technology",
            "PhD Computer Science"
        );
        programCombo.setValue(currentStudent.getProgram());
        
        filterBox.getChildren().addAll(filterLabel, programCombo);
        
        // Course offerings table setup
        TableView<Course> tableView = new TableView<>();
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Course, String> codeCol = new TableColumn<>("Course Code");
        codeCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getCourseCode()));

        TableColumn<Course, String> titleCol = new TableColumn<>("Course Title");
        titleCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getCourseTitle()));
        titleCol.setPrefWidth(250);

        TableColumn<Course, String> unitsCol = new TableColumn<>("Units");
        unitsCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(String.valueOf(data.getValue().getUnits())));
        unitsCol.setMaxWidth(60);

        TableColumn<Course, String> sectionCol = new TableColumn<>("Section");
        sectionCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getSection()));

        TableColumn<Course, String> timesCol = new TableColumn<>("Times");
        timesCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getTimes()));

        TableColumn<Course, String> daysCol = new TableColumn<>("Days");
        daysCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getDays()));
        daysCol.setMaxWidth(100);

        TableColumn<Course, String> roomsCol = new TableColumn<>("Rooms");
        roomsCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getRooms()));

        tableView.getColumns().addAll(codeCol, titleCol, unitsCol, sectionCol, timesCol, daysCol, roomsCol);

        // Load course offerings and setup filtering
        List<Course> allOfferings = fileManager.loadCourseOfferings();
        
        programCombo.setOnAction(e -> {
            tableView.getItems().clear();
            String selectedProgram = programCombo.getValue();
            
            if (selectedProgram.equals("All Programs")) {
                tableView.getItems().addAll(allOfferings);
            } else {
                List<Course> programCourses = fileManager.loadProgramCourses(selectedProgram);
                List<String> programCourseCodes = programCourses.stream()
                        .map(Course::getCourseCode)
                        .collect(Collectors.toList());
                
                List<Course> filteredOfferings = allOfferings.stream()
                        .filter(course -> programCourseCodes.contains(course.getCourseCode()))
                        .collect(Collectors.toList());
                
                tableView.getItems().addAll(filteredOfferings);
            }
        });
        
        programCombo.fireEvent(new javafx.event.ActionEvent());
        planner.getEnrolledCourses().clear();
        planner.getEnrolledCourses().addAll(currentStudent.getEnrolledCourses());

        
        enlistmentManager = new EnlistmentManager(planner, tableView, this);
        VBox enrolledPane = enlistmentManager.createEnlistmentPane();
        
        SplitPane splitPane = new SplitPane();
        splitPane.setDividerPositions(0.7, 0.7);
        splitPane.getItems().addAll(tableView, enrolledPane);

        content.getChildren().addAll(titleLabel, filterBox, splitPane);
        VBox.setVgrow(splitPane, Priority.ALWAYS);

        return content;
    }

    // Creates the course list tab showing program curriculum with descriptions
    private VBox createCourseListContent() {
        VBox content = new VBox(10);
        content.setPadding(new Insets(20));

        Label titleLabel = new Label("Program Courses");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Program selector dropdown
        HBox filterBox = new HBox(10);
        filterBox.setAlignment(Pos.CENTER_LEFT);
        
        Label filterLabel = new Label("Select Program:");
        ComboBox<String> programCombo = new ComboBox<>();
        programCombo.getItems().addAll(
            "BS Computer Science",
            "MS Computer Science",
            "Master of Information Technology",
            "PhD Computer Science"
        );
        programCombo.setValue(currentStudent.getProgram());
        
        filterBox.getChildren().addAll(filterLabel, programCombo);

        // Program courses table setup
        TableView<Course> tableView = new TableView<>();
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Course, String> codeCol = new TableColumn<>("Course Code");
        codeCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getCourseCode()));
        codeCol.setPrefWidth(120);

        TableColumn<Course, String> titleCol = new TableColumn<>("Course Title");
        titleCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getCourseTitle()));
        titleCol.setPrefWidth(300);

        TableColumn<Course, String> unitsCol = new TableColumn<>("Units");
        unitsCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(String.valueOf(data.getValue().getUnits())));
        unitsCol.setMaxWidth(60);

        TableColumn<Course, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getDescription()));
        descCol.setPrefWidth(500);

        tableView.getColumns().addAll(codeCol, titleCol, unitsCol, descCol);

        // Load program courses based on selected program
        programCombo.setOnAction(e -> {
            tableView.getItems().clear();
            String selectedProgram = programCombo.getValue();
            List<Course> programCourses = fileManager.loadProgramCourses(selectedProgram);
            tableView.getItems().addAll(programCourses);
        });
        
        programCombo.fireEvent(new javafx.event.ActionEvent());

        content.getChildren().addAll(titleLabel, filterBox, tableView);
        VBox.setVgrow(tableView, Priority.ALWAYS);

        return content;
    }
    
    //adds header row and column
    private void setupGrid() {
        String[] days = {"Mon", "Tue", "Wed", "Thu", "Fri"};
        for (int i = 0; i < days.length; i++) {
            Label dayLabel = new Label(days[i]);
            dayLabel.setStyle("-fx-font-weight: bold; -fx-alignment: center;");
            dayLabel.setMinWidth(100);
            dayLabel.setAlignment(Pos.CENTER);
            calendarGrid.add(dayLabel, i + 1, 0);
        }

        // hour labels from 7AM to 7PM
        for (int i = 0; i < 13; i++) {
            int hour = 7 + i;
            Label timeLabel = new Label(hour + ":00");
            timeLabel.setMinHeight(50);
            timeLabel.setAlignment(Pos.CENTER_LEFT);
            calendarGrid.add(timeLabel, 0, i + 1);
        }
    }
    
    // adds courses to the calendar
    private void fillCalendar() {
    	calendarGrid.getChildren().removeIf(node -> {
    	    Integer r = GridPane.getRowIndex(node);
    	    Integer c = GridPane.getColumnIndex(node);

    	    // keeps header row (r = 0)
    	    if (r != null && r == 0) return false;

    	    // keeps header column (c = 0)
    	    if (c != null && c == 0) return false;

    	    // Remove everything else (course blocks)
    	    return true;
    	});

        List<Course> enrolled = planner.getEnrolledCourses();

        for (Course c : enrolled) {
        	String times = c.getTimes();
            if (times == null || times.trim().isEmpty() || times.equalsIgnoreCase("TBA")) {
            	continue;
            }
        	
            //splits course time stringf into start and endd times
            String[] parts = times.split("-");
            if (parts.length < 1) {
            	continue; //skips if invalid time format
            }
             
        	String startRaw = parts[0].trim(); //gets the start time
        	int start24 = convertTo24Hour(startRaw, c.getSection());//converts hour depending if lab or lecture 
        	if (start24 < 0) {
        		continue;// skips if start time could not be converted properly since convert returns -1 if error
        	}
        	
        	//course block ui
        	for (String rawDay : expandDays(c.getDays())) {
        	    int col = dayToColumn(rawDay);
        	    int row = timeToRow(start24);
        	    
        	    Label courseBlock = new Label(c.getCourseCode() + "\n" + c.getSection());
        	    courseBlock.setStyle("-fx-background-color: lightblue; -fx-border-color: black; -fx-alignment: center;");
        	    courseBlock.setMinSize(100, 50);
        	    courseBlock.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        	    courseBlock.setOnMouseClicked(e -> showCourseInfo(c));

        	    calendarGrid.add(courseBlock, col + 1, row + 1); // +1 for header row/column
            }
        }
    }
    
   // day string to column index
    private int dayToColumn(String day) {
        switch (day) {
            case "Mon": return 1;
            case "Tue": return 2;
            case "Wed": return 3;
            case "Thu": return 4;
            case "Fri": return 5;
            default: return 1;
        }
    }
    
    //checks if  course is a lab
    public static boolean isLab(String section) {
        if (!section.contains("-")) {
            return false;
        }
        return true;
    }
    
  //checks if  course is a lecture
    public static boolean isLecture(String section) {
        if (!isLab(section)) {
        	return true;
        }
        return false;
    }
    
    // converts pm hour to military time format if necessary
    public static int convertTo24Hour(String time, String section) {
        String hourPart = time.split(":")[0].trim();
        int hour;
        
        try {
        	hour = Integer.parseInt(hourPart);
        } catch(NumberFormatException e){
        	return -1;
        }
        
        boolean lab = isLab(section);
        if (lab) {
            if (hour >= 10 && hour <= 12) {
            	return hour;
            }else if(hour >= 1 && hour <=7) {
            	return hour + 12;
            }else {
            	return -1;
            }
        } else {
            if (hour >= 7 && hour <= 12) {
            	return hour;
            }else if (hour >= 1 && hour <= 5)  {
            	return hour + 12;
            }else {
            	return -1;
            }
        }
    }
     
    // maps the course time into a row (7 am class - 6 = row 1)
    private int timeToRow(int hour24) {
    	return hour24 - 6;
    }
    
    // expands days string from WF, TTh, etc to separate days
    public static List<String> expandDays(String dayString) {
        dayString = dayString.trim();

        List<String> days = new ArrayList<>();

        if (dayString.equalsIgnoreCase("TTh")) {
            days.add("Tue");
            days.add("Thu");
        } else if (dayString.equalsIgnoreCase("WF")) {
            days.add("Wed");
            days.add("Fri");
        }else {
            // Single day formats like M, T, W, Th, F, Mon, Tue...
            switch (dayString.substring(0, 1).toUpperCase()) {
                case "M": days.add("Mon"); break;
                case "T": days.add("Tue"); break;
                case "W": days.add("Wed"); break;
                case "F": days.add("Fri"); break;
            }
        }

        return days;
    }

    // show selected courses' info
    private void showCourseInfo(Course course) {
    	calendarInfoPane.getChildren().clear();
    	calendarInfoPane.getChildren().addAll(
                new Label("Course Code: " + course.getCourseCode()),
                new Label("Title: " + course.getCourseTitle()),
                new Label("Units: " + course.getUnits()),
                new Label("Section: " + course.getSection()),
                new Label("Times: " + course.getTimes()),
                new Label("Days: " + course.getDays()),
                new Label("Rooms: " + course.getRooms()),
                new Label("Description: " + course.getDescription())
        );
    }
    
    
    public void refreshCalendar() {
        // collect all nodes to remove (keep headers)
        List<javafx.scene.Node> toRemove = new ArrayList<>();
        for (javafx.scene.Node node : calendarGrid.getChildren()) {
            Integer row = GridPane.getRowIndex(node);
            Integer col = GridPane.getColumnIndex(node);

            // keep header row (index 0) and header column (index 0)
            if ((row != null && row == 0) || (col != null && col == 0)) {
                continue;
            }

            toRemove.add(node);
        }

        //remove collected nodes
        for (javafx.scene.Node node : toRemove) {
            calendarGrid.getChildren().remove(node);
        }

        //   Refills the calendar
        fillCalendar();
    }
    

    // Handles logout by returning to login screen
    private void handleLogout() {
        ArrayList<Student> users = fileManager.load(FileManager.getSavePath());

        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getEmail().equals(currentStudent.getEmail())) {
                users.set(i, currentStudent);
                break;
            }
        }
        
        System.out.println("Logging out...");
        fileManager.save(users, FileManager.getSavePath());
        
        LoginView loginView = new LoginView();
        loginView.setStage(stage);
    }

    public void setStage(Stage stage) {
        this.stage = stage;
        this.stage.setTitle("ICS Registration Planner - Dashboard");
        this.stage.setScene(this.scene);
        this.stage.show();
    }

    public Scene getScene() {
        return scene;
    }
}