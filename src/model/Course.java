package model;

public class Course {
    private String courseCode;
    private String courseTitle;
    private int units;
    private String section;
    private String times;
    private String days;
    private String rooms;
    private String description;
    

    // Constructor for course offerings (with schedule)
    public Course(String courseCode, String courseTitle, int units, String section, 
                  String times, String days, String rooms) {
        this.courseCode = courseCode;
        this.courseTitle = courseTitle;
        this.units = units;
        this.section = section;
        this.times = times;
        this.days = days;
        this.rooms = rooms;
        this.description = "";
    }

    // Constructor for program courses (without schedule)
    public Course(String courseCode, String courseName, int units, String description) {
        this.courseCode = courseCode;
        this.courseTitle = courseName;
        this.units = units;
        this.description = description;
        this.section = "TBA";
        this.times = "TBA";
        this.days = "TBA";
        this.rooms = "TBA";
    }

    // Getters
    public String getCourseCode() { return courseCode; }
    public String getCourseTitle() { return courseTitle; }
    public int getUnits() { return units; }
    public String getSection() { return section; }
    public String getTimes() { return times; }
    public String getDays() { return days; }
    public String getRooms() { return rooms; }
    public String getDescription() { return description; }

    @Override
    public String toString() {
        if (!"TBA".equals(section)) {
            return String.format("%s - %s (%d units) | Section: %s | %s %s | Room: %s",
                    courseCode, courseTitle, units, section, times, days, rooms);
        } else {
            return String.format("%s - %s (%d units)", courseCode, courseTitle, units);
        }
    }
}