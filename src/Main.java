import manager.Managers;
import manager.TaskManager;
import statusTasks.Status;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

public class Main {
    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();

        manager.createTask(new Task("Приготовить завтрак",
                "накормить кошек и себя", Status.IN_PROGRESS));
        manager.createTask(new Task("Купить колу",
                "без сахара", Status.NEW));
        manager.createEpic(new Epic("Приготовить шаурму",
                "в сырном лавше", Status.NEW));
        manager.createSubTask(new Subtask("Купить овощи",
                "не забыть Халапеньо", Status.NEW,3));
        manager.createSubTask(new Subtask("Купить мясо",
                "описание", Status.NEW,3));
        manager.createEpic(new Epic("Другой эпик",
                "с одной позадачей", Status.IN_PROGRESS));
        manager.createSubTask(new Subtask("Подзадача",
                "под вторым эпиком", Status.NEW, 6));
        System.out.println(manager.getAllTask());
        System.out.println(manager.getAllEpicTask());
        System.out.println(manager.getAllSubTask());
        manager.getTaskById(1);
        manager.getEpicTaskByID(3);
        manager.getSubTaskByID(4);
        manager.getTaskById(2);
        manager.getSubTaskByID(5);
        manager.getSubTaskByID(7);
        manager.getTaskById(1);
        manager.getEpicTaskByID(3);
        manager.getSubTaskByID(4);
        manager.getSubTaskByID(5);
        manager.getHistory();
        manager.getEpicTaskByID(3);
        manager.getHistory();
    }
}

