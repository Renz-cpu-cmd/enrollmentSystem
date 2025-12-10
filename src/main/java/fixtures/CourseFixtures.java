package fixtures;

import model.Course;
import java.util.HashMap;
import java.util.Map;

public class CourseFixtures {
    private static final Map<String, Course> courses = new HashMap<>();

    static {
        courses.put("IT-311", new Course("IT-311", "Networking 1", "", 3));
        courses.put("IT-312", new Course("IT-312", "Web Development", "", 3));
        courses.put("IT-313", new Course("IT-313", "Software Engineering", "", 3));
        courses.put("GE-105", new Course("GE-105", "Purposive Communication", "", 3));
        courses.put("PE-3", new Course("PE-3", "Physical Education 3", "", 2));
        courses.put("IT-211", new Course("IT-211", "Data Structures", "", 3));
        courses.put("GE-102", new Course("GE-102", "Ethics", "", 3));
    }

    public static Course getCourseByCode(String code) {
        return courses.get(code);
    }
}
