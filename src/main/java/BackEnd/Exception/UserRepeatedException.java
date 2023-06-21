package BackEnd.Exception;

public class UserRepeatedException extends Exception{
    public UserRepeatedException() {
        super("User repeated");
    }
}
