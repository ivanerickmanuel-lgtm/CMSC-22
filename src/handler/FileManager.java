package handler;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import model.Course;
import model.Student;

public class FileManager {
    
    // FILE PATHS
    private static final Path SAVE_PATH = Paths.get("src/database/users.txt");
    private static final Path COURSE_OFFERINGS_PATH = Paths.get("src/database/course_offerings.csv");
    private static final Path CMSC_COURSES_PATH = Paths.get("src/database/ics_cmsc_courses.csv");
    private static final Path MIT_COURSES_PATH = Paths.get("src/database/ics_mit_courses.csv");
    private static final Path MSCS_COURSES_PATH = Paths.get("src/database/ics_mscs_courses.csv");
    private static final Path PHD_COURSES_PATH = Paths.get("src/database/ics_phd_courses.csv");

    public static Path getSavePath() {
        return SAVE_PATH;
    }
    
    // Saves student list to users.txt
    public void save(ArrayList<Student> users, Path savePath) {
        System.out.println("Saving...");
        try {
            ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(savePath));
            out.writeObject(users);
            out.close();
            System.out.println("Saving complete.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Loads student list from users.txt

    public ArrayList<Student> load(Path loadPath) {
        if (!Files.exists(loadPath)) {
            System.out.println("No existing file found at: " + loadPath);
            return null;
        }
        
        try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(loadPath))) {
            @SuppressWarnings("unchecked")
            ArrayList<Student> users = (ArrayList<Student>) in.readObject();
            System.out.println("Loading complete.");
            return users;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Parses CSV line handling quoted fields with commas using regex. sineach ko lng to guys huhu
    private List<String> parseCSVLine(String line) {
        return Arrays.asList(line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)"));
    }

    // Reads all course offerings from course_offerings.csv
    public List<Course> loadCourseOfferings() {
        List<Course> courses = new ArrayList<>();
        try {
            // Read entire file as list of lines
            List<String> lines = Files.readAllLines(COURSE_OFFERINGS_PATH);
            
            // Skip first 2 lines (semester info and headers)
            for (int i = 2; i < lines.size(); i++) {
                String line = lines.get(i).trim();
                if (line.isEmpty()) continue;
                
                String[] parts = line.split(",");
                if (parts.length >= 7) {
                    try {
                        String courseCode = parts[0].trim();
                        String courseTitle = parts[1].trim();
                        int units = Integer.parseInt(parts[2].trim());
                        String section = parts[3].trim();
                        String times = parts[4].trim();
                        String days = parts[5].trim();
                        String rooms = parts[6].trim();
                        
                        courses.add(new Course(courseCode, courseTitle, units, section, times, days, rooms));
                    } catch (NumberFormatException e) {
                        System.out.println("Skipping invalid line: " + line);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return courses;
    }

    // Loads curriculum courses for a specific program with descriptions.
    public List<Course> loadProgramCourses(String program) {
        // Determine which CSV file based on program
        Path coursePath;
        if (program.equals("BS Computer Science")) {
            coursePath = CMSC_COURSES_PATH;
        } else if (program.equals("MS Computer Science")) {
            coursePath = MSCS_COURSES_PATH;
        } else if (program.equals("Master of Information Technology")) {
            coursePath = MIT_COURSES_PATH;
        } else if (program.equals("PhD Computer Science")) {
            coursePath = PHD_COURSES_PATH;
        } else {
            return new ArrayList<>();
        }

        List<Course> courses = new ArrayList<>();
        try {
            // Read entire file as list of lines
            List<String> lines = Files.readAllLines(coursePath);
            
            // Skip first line (headers)
            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i).trim();
                if (line.isEmpty()) continue;
                
                // Use special parser for quoted fields with commas
                List<String> parts = parseCSVLine(line);
                if (parts.size() >= 4) {
                    try {
                        String courseCode = parts.get(0).trim().replace("\"", "");
                        String courseName = parts.get(1).trim().replace("\"", "");
                        int units = Integer.parseInt(parts.get(2).trim());
                        String description = parts.get(3).trim().replace("\"", "");
                        
                        courses.add(new Course(courseCode, courseName, units, description));
                    } catch (NumberFormatException e) {
                        System.out.println("Skipping invalid line: " + line);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return courses;
    }
    
    public void saveEnrolledCourses(Student student) {
        try {
            List<String> courseCodes = new ArrayList<>();

            for (Course c : student.getEnrolledCourses()) {
                courseCodes.add(c.getCourseCode());
            }

            String safeEmail = student.getEmail().replace("@", "_at_").replace(".", "_");
            Path path = Paths.get("src/database/enrolled_" + safeEmail + ".txt");

            Files.write(path, courseCodes);

            System.out.println("Enrolled courses saved for " + student.getEmail());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public ArrayList<Student> loadEnrolledCourses(Path loadPath) {
        if (!Files.exists(loadPath)) {
            System.out.println("No existing file found at: " + loadPath);
            return null;
        }

        try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(loadPath))) {
            ArrayList<Student> users = (ArrayList<Student>) in.readObject();
            System.out.println("Loading complete.");
            return users;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }
}