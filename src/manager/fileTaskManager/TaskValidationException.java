package manager.fileTaskManager;

public class TaskValidationException extends RuntimeException {

    public TaskValidationException(String message) {
        super(message);
    }

    public TaskValidationException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
