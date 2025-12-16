package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Block = pre-defined set of course meetings for a section/shift.
 */
public class Block {
    private Integer id;
    private String blockCode;
    private String title;
    private String description;
    private int capacity;
    private boolean active;
    private Section section;
    private List<Schedule> schedules = new ArrayList<>();

    public Block() {
    }

    public Block(Integer id, String blockCode, String title, String description, int capacity, boolean active, Section section) {
        this.id = id;
        this.blockCode = blockCode;
        this.title = title;
        this.description = description;
        this.capacity = capacity;
        this.active = active;
        this.section = section;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBlockCode() {
        return blockCode;
    }

    public void setBlockCode(String blockCode) {
        this.blockCode = blockCode;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Section getSection() {
        return section;
    }

    public void setSection(Section section) {
        this.section = section;
    }

    public List<Schedule> getSchedules() {
        return schedules;
    }

    public void setSchedules(List<Schedule> schedules) {
        this.schedules = schedules != null ? schedules : new ArrayList<>();
    }

    public void addSchedule(Schedule schedule) {
        if (schedule != null) {
            this.schedules.add(schedule);
        }
    }
}
