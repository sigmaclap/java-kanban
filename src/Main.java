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
                "не забыть Халапеньо", Status.NEW, 3));
        manager.createSubTask(new Subtask("Купить мясо",
                "описание", Status.NEW, 3));
        manager.createSubTask(new Subtask("Подзадача",
                "под вторым эпиком", Status.NEW, 3));
        manager.createEpic(new Epic("Другой эпик",
                "с одной позадачей", Status.NEW));

        System.out.println(manager.getAllTask());
        System.out.println(manager.getAllEpicTask());
        System.out.println(manager.getAllSubTask());
        manager.getTaskById(1);
        manager.getTaskById(2);
        manager.getTaskById(1);
        System.out.println("------ История ниже ------");
        manager.getSubTaskByID(4);
        System.out.println(manager.getHistory());
        manager.getTaskById(2);
        System.out.println(manager.getHistory());
        System.out.println(manager.getHistory());
        manager.deleteTaskById(1);
        manager.getSubTaskByID(4);
        manager.getSubTaskByID(5);
        manager.getSubTaskByID(6);
        System.out.println("Получили все сабтаски Эпика под id = 3");
        manager.getEpicTaskByID(3);
        System.out.println(manager.getHistory());
        manager.deleteEpicTaskById(3);
        System.out.println(manager.getHistory());
    }
}

