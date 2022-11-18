import manager.ManagerTasks;
import statusTasks.Status;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

public class Main {
    public static void main(String[] args) {
        ManagerTasks managerTasks = new ManagerTasks();

        managerTasks.createTask(new Task("Приготовить завтрак",
                "накормить кошек и себя", Status.IN_PROGRESS));
        managerTasks.createTask(new Task("Купить колу",
                "без сахара", Status.NEW));
        managerTasks.createEpic(new Epic("Приготовить шаурму",
                "в сырном лавше", Status.NEW));
        managerTasks.createSubTask(new Subtask("Купить овощи",
                "не забыть Халапеньо", Status.NEW,3));
        managerTasks.createSubTask(new Subtask("Купить мясо",
                "описание", Status.NEW,3));
        managerTasks.createEpic(new Epic("Другой эпик",
                "с одной позадачей", Status.IN_PROGRESS));
        managerTasks.createSubTask(new Subtask("Подзадача",
                "под вторым эпиком", Status.NEW, 6));

        System.out.println(managerTasks.getAllTask());
        System.out.println(managerTasks.getAllEpicTask());
        System.out.println(managerTasks.getAllSubTask());
        Task task = managerTasks.getTaskById(1);
        task.setStatus(Status.NEW);
        managerTasks.updateTask(task);
        System.out.println(managerTasks.getTaskById(1));
        Epic epic = managerTasks.getEpicTaskByID(3);
        epic.setStatus(Status.DONE);
        managerTasks.updateEpic(epic);
        System.out.println(managerTasks.getEpicTaskByID(3));
        Subtask subtask = managerTasks.getSubTaskByID(4);
        subtask.setStatus(Status.NEW);
        managerTasks.updateSubTask(subtask);
        Subtask subtask1 = managerTasks.getSubTaskByID(5);
        subtask1.setStatus(Status.NEW);
        managerTasks.updateSubTask(subtask1);
        System.out.println(managerTasks.getSubTaskByID(4));
        System.out.println(managerTasks.getEpicTaskByID(3));
        System.out.println(managerTasks.getSubTaskByEpic(3));
        managerTasks.deleteEpicTaskById(6);
        System.out.println(managerTasks.getAllEpicTask());
        System.out.println(managerTasks.getAllSubTask());
    }
}

