package BackEnd.Exception;

public class UserDuplicatedException extends Exception{
    public UserDuplicatedException() {
        super("User duplicated");
    }
}
