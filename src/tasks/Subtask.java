package tasks;

import statusTasks.Status;
import statusTasks.TypeTasks;

import java.time.LocalDateTime;
import java.util.Objects;

public class Subtask extends Task {
    private Integer epicId;


    public Subtask(String name, String description, Status status,
                   LocalDateTime startTime, long duration, int epicId) {
        super(name, description, status, startTime, duration);
        this.epicId = epicId;
    }

    public Subtask(Integer id, String name, String description, Status status,
                   LocalDateTime startTime, long duration, int epicId) {
        super(id, name, description, status, startTime, duration);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public TypeTasks getTypeTasks() {
        return TypeTasks.SUBTASK;
    }

    public void setEpicId(Integer epicId) {
        this.epicId = epicId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Subtask subtask = (Subtask) o;
        return Objects.equals(epicId, subtask.epicId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicId);
    }


    @Override
    public String toString() {
        return getId() + ","
                + TypeTasks.SUBTASK + ","
                + getName() + ","
                + getStatus() + ","
                + getDescription() + ","
                + getStartTime() + ","
                + getDuration() + ","
                + getEndTime() + ","
                + getEpicId();
    }
}
