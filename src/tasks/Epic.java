package tasks;

import statusTasks.Status;
import statusTasks.TypeTasks;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {

    private List<Integer> subtaskIds = new ArrayList<>();
    private LocalDateTime endTime;


    public Epic(String name, String description, Status status,
                LocalDateTime startTime, long duration, LocalDateTime endTime) {
        super(name, description, status, startTime, duration);
        this.endTime = endTime;
    }

    public Epic(Integer id, String name, String description, Status status,
                LocalDateTime startTime, long duration, LocalDateTime endTime) {
        super(id, name, description, status, startTime, duration);
        this.endTime = endTime;
    }

    public List<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    public void addSubtaskIds(Integer id) {
        subtaskIds.add(id);
    }

    public void setSubtaskIds(List<Integer> subtaskIds) {
        this.subtaskIds = subtaskIds;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subtaskIds, epic.subtaskIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtaskIds);
    }


    @Override
    public String toString() {
        return getId() + ","
                + TypeTasks.EPIC + ","
                + getName() + ","
                + getStatus() + ","
                + getDescription() + ","
                + getStartTime() + ","
                + getDuration() + ","
                + getEndTime();
    }
}
