package Tasks;

import java.util.Objects;

public class Task {
    private String nameTask;
    private String description;
    private int idTask;
    private String statusTask;

    public Task(String nameTask, String description, String statusTask) {
        this.nameTask = nameTask;
        this.description = description;
        this.statusTask = statusTask;
    }

    public String getNameTask() {
        return nameTask;
    }

    public void setNameTask(String nameTask) {
        this.nameTask = nameTask;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getIdTask() {
        return idTask;
    }

    public void setIdTask(int idTask) {
        this.idTask = idTask;
    }

    public String getStatusTask() {
        return statusTask;
    }

    public void setStatusTask(String statusTask) {
        this.statusTask = statusTask;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(nameTask, task.nameTask)
                && Objects.equals(description, task.description)
                && Objects.equals(idTask, task.idTask)
                && Objects.equals(statusTask, task.statusTask);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nameTask, description, idTask, statusTask);
    }

    @Override
    public String toString() {
        return "Task{" +
                "nameTask='" + nameTask + '\'' +
                ", description='" + description + '\'' +
                ", idTask=" + idTask +
                ", statusTask='" + statusTask + '\'' +
                '}';
    }
}
