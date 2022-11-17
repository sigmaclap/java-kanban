import Manager.ManagerTasks;
import Tasks.Epic;
import Tasks.Subtask;
import Tasks.Task;

public class Main {
    public static void main(String[] args) {
        ManagerTasks managerTasks = new ManagerTasks();

        managerTasks.createTask(new Task("Приготовить завтрак",
                "накормить кошек и себя", managerTasks.statusTasks(1)));
        managerTasks.createTask(new Task("Купить колу",
                "без сахара", managerTasks.statusTasks(0)));
        managerTasks.createEpic(new Epic("Приготовить шаурму",
                "в сырном лавше", managerTasks.statusTasks(0)));
        managerTasks.createSubTask(new Subtask("Купить овощи",
                "не забыть Халапеньо", managerTasks.statusTasks(0),3));
        managerTasks.createSubTask(new Subtask("Купить мясо",
                "описание", managerTasks.statusTasks(0),3));
        managerTasks.createEpic(new Epic("Другой эпик",
                "с одной позадачей", managerTasks.statusTasks(1)));
        managerTasks.createSubTask(new Subtask("Подзадача",
                "под вторым эпиком", managerTasks.statusTasks(0),6));

        System.out.println(managerTasks.getAllTask());
        System.out.println(managerTasks.getAllEpicTask());
        System.out.println(managerTasks.getAllSubTask());
        Task task = managerTasks.getTaskById(1);
        task.setStatusTask("NEW");
        managerTasks.updateTask(task);
        System.out.println(managerTasks.getTaskById(1));
        Epic epic = managerTasks.getEpicTaskByID(3);
        epic.setStatusTask("DONE");
        managerTasks.updateEpic(epic);
        System.out.println(managerTasks.getEpicTaskByID(3));
        Subtask subtask = managerTasks.getSubTaskByID(4);
        subtask.setStatusTask("DONE");
        managerTasks.updateSubTask(subtask);
        System.out.println(managerTasks.getSubTaskByID(4));
        System.out.println(managerTasks.getEpicTaskByID(3));
        managerTasks.deleteTaskById(1);
        managerTasks.deleteEpicTaskById(6);
        System.out.println(managerTasks.getAllTask());
        System.out.println(managerTasks.getAllEpicTask());
        System.out.println(managerTasks.printSubTaskByEpic(3));
    }
}

