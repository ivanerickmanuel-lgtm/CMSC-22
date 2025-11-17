package model;

import java.util.ArrayList;
import java.util.List;

import application.StudentDashboard;

public class CoursePlanner {
	private List<Course> enrolledCourses;
	
	public CoursePlanner() {
		this.enrolledCourses = new ArrayList<>();
	}
	
    public List<Course> getEnrolledCourses() {
        return enrolledCourses;
    }
    
    public boolean addCourse(Course course) {
        for (Course c : enrolledCourses) {
            if (c.getCourseCode().equals(course.getCourseCode()) &&
                StudentDashboard.isLab(c.getSection()) == StudentDashboard.isLab(course.getSection())) {
                return false;
            }
        }
        enrolledCourses.add(course);
        return true;
    }
    
    public boolean removeCourse(Course course) {
        for (int i = 0; i<enrolledCourses.size();i++) {
            if (enrolledCourses.get(i).getCourseCode().equals(course.getCourseCode())) {
            	enrolledCourses.remove(i);
                return true;
            }
        }
        return false;
    }

    public int getTotalUnits() {
    	int totalUnits = 0;
    	
        for (Course c : enrolledCourses) {
        	int unit = c.getUnits();
        	totalUnits += unit;
        }
        return totalUnits;
    } 

    public boolean hasConflict(Course newCourse) {
        for (Course c : enrolledCourses) {
            if (!c.getDays().equals("TBA") && !newCourse.getDays().equals("TBA")) {
                if (c.getDays().equals(newCourse.getDays()) && c.getTimes().equals(newCourse.getTimes())) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public void setEnrolledCourses(List<Course> courses) {
    	this.enrolledCourses.clear();
        this.enrolledCourses.addAll(courses);
    }
}
