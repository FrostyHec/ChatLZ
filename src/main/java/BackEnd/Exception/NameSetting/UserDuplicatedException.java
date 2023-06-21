package BackEnd.Exception.NameSetting;

public class UserDuplicatedException extends Exception{
    public UserDuplicatedException() {
        super("User duplicated");
    }
}
