package BackEnd.Exception.NameSetting;

public class UsernameTooLongException extends Exception{
    public UsernameTooLongException(int len){
        super("User name too long: length " + len);
    }
}
