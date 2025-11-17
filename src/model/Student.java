package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Student implements Serializable {

    private String firstName;
    private String middleName;
    private String lastName;
    private String email;
    private String password;
    private String program;
    private List<Course> enrolledCourses = new ArrayList<>(); 
    
    public Student() {}

    public Student(String firstName, String middleName, String lastName, 
                String email, String password, String program) {
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.program = program;
        this.enrolledCourses = new ArrayList<>();
    }

    // getter and setters
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getMiddleName() { return middleName; }
    public void setMiddleName(String middleName) { this.middleName = middleName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getProgram() { return program; }
    public void setProgram(String program) { this.program = program; }

    public String getFullName() {
        if (middleName != null && !middleName.isEmpty()) {
            return firstName + " " + middleName + " " + lastName;
        }
        return firstName + " " + lastName;
    }
    
    public void addCourse(Course course) { enrolledCourses.add(course); }
    
    public void removeCourse(Course course) { enrolledCourses.remove(course); }
    
    public List<Course> getEnrolledCourses() {
        return enrolledCourses;
    }
}