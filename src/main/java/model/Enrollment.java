package model;

public class Enrollment {
    private Integer id;
    private Integer studentId;
    private Integer blockId;
    private String term;
    private String status;
    private String createdAt;

    public Enrollment() {}

    public Enrollment(Integer studentId, Integer blockId, String term, String status) {
        this.studentId = studentId;
        this.blockId = blockId;
        this.term = term;
        this.status = status;
    }

    /** Legacy-friendly constructor where courseId maps to blockId. Academic year is stored in term. */
    public Enrollment(Integer studentId, Integer courseId, String academicYear, String term, String status) {
        this.studentId = studentId;
        this.blockId = courseId; // courseId kept for backward compatibility
        this.term = academicYear != null ? academicYear : term;
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getStudentId() {
        return studentId;
    }

    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }

    public Integer getBlockId() {
        return blockId;
    }

    public void setBlockId(Integer blockId) {
        this.blockId = blockId;
    }

    /** Backward compatible accessor; maps to blockId. */
    public Integer getCourseId() {
        return blockId;
    }

    public void setCourseId(Integer courseId) {
        this.blockId = courseId;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Academic year helper; term currently carries the academic year/term label.
     */
    public String getAcademicYear() {
        return term;
    }
}
