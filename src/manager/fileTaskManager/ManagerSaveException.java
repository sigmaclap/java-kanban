package manager.fileTaskManager;

public class ManagerSaveException extends RuntimeException {

    public ManagerSaveException(String message) {
        super(message);
    }

    public ManagerSaveException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
