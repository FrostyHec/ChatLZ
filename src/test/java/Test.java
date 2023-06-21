import BackEnd.Client.Client;
import BackEnd.Client.ReadHandler;
import BackEnd.Exception.NameSetting.UsernameTooLongException;
import BackEnd.MessageTypePack.MessageType;
import BackEnd.MessageTypePack.RequestToSeverMsg;
import BackEnd.Server.Server;
import BackEnd.WriteHandler;

import java.util.Scanner;

public class Test {
    public static void main(String[] args) {
        Server server = new Server(1145);
        Thread thread = new Thread(server);
        thread.start();
    }
}

class User {
    private String initName(Client c){
        boolean succeed = false;
        String userName = null;
        while (!succeed) {
            System.out.println("请设置你的名字（小于32个字符）");
            try {
                userName = sc.nextLine();
                c.setName(userName);
                succeed = true;
            } catch (UsernameTooLongException e) {
                System.out.println(e.getMessage());
            }
        }
        return userName;
    }
    Scanner sc = new Scanner(System.in);
    public void start() {

        Client c = new Client(new ReadHandler(""), new WriteHandler());
        c.connect("127.0.0.1", 1145);
        String userName=initName(c);

        while (!c.isInit()) {//没有初始化成功
            if (!c.needResetName()) {//消息还没到
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }else {//需要重写名字
                userName=initName(c);
            }
        }

        byte[] userNameByteArray = new byte[MessageType.userNameLength], origin = userName.getBytes();
        System.arraycopy(origin, 0, userNameByteArray, 0, origin.length);

        String say = null;
        System.out.println("现在可以开始聊天了，以 用户名(空一格)要说的话 向指定用户对话，对话中不得包含空格，若用户名填All,则向所有用户群发");
        System.out.println("单回s结束聊天，单回r查看当前聊天室的人员列表");
        while (!(say = sc.nextLine()).equals("s")) {
            if (say.equals("r")) {
                c.requestToAdmin(userNameByteArray, RequestToSeverMsg.GetCurrentUserList);
                continue;
            }
            String[] temp = say.split(" ");
            String target = temp[0];
            String words = temp[1];
            byte[] targetNameByteArray = new byte[MessageType.userNameLength], origin2 = target.getBytes();
            System.arraycopy(origin2, 0, targetNameByteArray, 0, origin2.length);
            c.write(userNameByteArray, targetNameByteArray, words);
        }
    }
}

class User1 {
    public static void main(String[] args) {
        new User().start();
    }
}

class User2 {
    public static void main(String[] args) {
        new User().start();
    }
}

class User3 {
    public static void main(String[] args) {
        new User().start();
    }
}