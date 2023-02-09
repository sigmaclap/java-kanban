package manager.fileTaskManager;

public class ManagerSaveException extends IllegalArgumentException {

    public ManagerSaveException(String s) {
        super(s);
    }

    public ManagerSaveException(String message, Throwable cause) {
        super(message, cause);
    }
}
