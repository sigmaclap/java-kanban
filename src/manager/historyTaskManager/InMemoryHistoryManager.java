package manager.historyTaskManager;

import manager.fileTaskManager.TaskValidationException;
import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {
    Map<Integer, Node> nodeMap = new HashMap<>();
    private Node head;
    private Node tail;


    public static class Node {
        public Task task;
        public Node next;
        public Node prev;


        public Node(Node prev, Task task, Node next) {
            this.task = task;
            this.next = next;
            this.prev = prev;
        }
    }

    @Override
    public void add(Task task) {
        if (nodeMap.containsKey(task.getId())) {
            removeNode(nodeMap.get(task.getId()));
        }
        linkLast(task);
    }

    @Override
    public void remove(int id) {
        removeNode(nodeMap.get(id));
    }

    private void removeNode(Node node) {
        if (nodeMap.isEmpty()) {
            return;
        }
        if (node == null) {
            throw new TaskValidationException("Невозможно удалить выбранную задачу из истории, укажите верный номер задачи");
        }
        nodeMap.remove(node.task.getId());
        Node next = node.next;
        Node prev = node.prev;

        if (prev == null) {
            head = next;
        } else {
            prev.next = next;
        }

        if (next == null) {
            tail = prev;
        } else {
            next.prev = prev;
        }
        node.task = null;
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    private void linkLast(Task task) {
        final Node oldTail = tail;
        final Node newNode = new Node(oldTail, task, null);
        tail = newNode;
        if (oldTail == null) {
            head = newNode;
        } else {
            oldTail.next = newNode;
        }
        nodeMap.put(task.getId(), newNode);
    }

    private List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();
        Node node = head;
        while (node != null) {
            tasks.add(node.task);
            node = node.next;
        }
        return tasks;
    }
}
