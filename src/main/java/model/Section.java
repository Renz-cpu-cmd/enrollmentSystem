package model;

/**
 * Academic section metadata (program + year level + shift).
 */
public class Section {
    private Integer id;
    private String code;
    private String name;
    private String program;
    private int yearLevel;
    private String shift;
    private int capacity;

    public Section() {
    }

    public Section(Integer id, String code, String name, String program, int yearLevel, String shift, int capacity) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.program = program;
        this.yearLevel = yearLevel;
        this.shift = shift;
        this.capacity = capacity;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public int getYearLevel() {
        return yearLevel;
    }

    public void setYearLevel(int yearLevel) {
        this.yearLevel = yearLevel;
    }

    public String getShift() {
        return shift;
    }

    public void setShift(String shift) {
        this.shift = shift;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
}
