package model;

/**
 * A single scheduled meeting for a block (optionally mapped to a course).
 */
public class Schedule {
    private Integer id;
    private Integer blockId;
    private Integer courseId;
    private String courseCode;
    private String subject;
    private String dayPattern;
    private String timeStart;
    private String timeEnd;
    private String room;
    private String instructor;
    private double units;

    public Schedule() {
    }

    public Schedule(Integer id, Integer blockId, Integer courseId, String courseCode, String subject,
                    String dayPattern, String timeStart, String timeEnd, String room, String instructor, double units) {
        this.id = id;
        this.blockId = blockId;
        this.courseId = courseId;
        this.courseCode = courseCode;
        this.subject = subject;
        this.dayPattern = dayPattern;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.room = room;
        this.instructor = instructor;
        this.units = units;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getBlockId() {
        return blockId;
    }

    public void setBlockId(Integer blockId) {
        this.blockId = blockId;
    }

    public Integer getCourseId() {
        return courseId;
    }

    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDayPattern() {
        return dayPattern;
    }

    public void setDayPattern(String dayPattern) {
        this.dayPattern = dayPattern;
    }

    public String getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(String timeStart) {
        this.timeStart = timeStart;
    }

    public String getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(String timeEnd) {
        this.timeEnd = timeEnd;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getInstructor() {
        return instructor;
    }

    public void setInstructor(String instructor) {
        this.instructor = instructor;
    }

    public double getUnits() {
        return units;
    }

    public void setUnits(double units) {
        this.units = units;
    }
}
