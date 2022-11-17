package Tasks;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {

    private ArrayList<Integer> subTasksId = new ArrayList<>();

    public Epic(String nameTask, String description, String statusTask) {
        super(nameTask, description, statusTask);
    }

    public ArrayList<Integer> getSubTasksId() {
        return subTasksId;
    }

    public void setSubTasksId(Integer id) {
        subTasksId.add(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subTasksId, epic.subTasksId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subTasksId);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "nameTask='" + getNameTask() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", idTask=" + getIdTask() +
                ", statusTask='" + getStatusTask() + '\'' +
                '}';
    }
}
