package application.model.data_objects;

// Helper class for returning results
public class LoginResult {
    private boolean success;
    private String message;

    public LoginResult(boolean success, String message) {
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
