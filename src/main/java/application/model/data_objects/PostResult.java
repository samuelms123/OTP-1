package application.model.data_objects;

public class PostResult {
    private boolean success;
    private String message;

    public PostResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}
