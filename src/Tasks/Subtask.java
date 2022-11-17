package Tasks;

import java.util.Objects;

public class Subtask extends Task {
    private int epicId;

    public Subtask(String nameTask, String description, String statusTask, Integer epicId) {
        super(nameTask, description, statusTask);
        this.epicId = epicId;
    }
    public int getEpicId() {
        return epicId;
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
        return "Subtask{" +
                "nameTask='" + getNameTask() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", idTask=" + getIdTask() +
                ", statusTask='" + getStatusTask() + '\'' +
                '}';
    }
}
