package BackEnd.Client;

import BackEnd.MessageTypePack.SystemMessageType;

import java.nio.channels.SocketChannel;
import java.util.Arrays;

public class ReadHandler {
    String savePath;

    public ReadHandler(String savePath) {
        this.savePath = savePath;
    }

    public void read(byte[] source, String words) {
        String name = new String(source).trim();
        System.out.println(name + "说：" + words);
    }

    public void read(SystemMessageType systemMessageType) {
        //TODO 处理系统消息
        switch (systemMessageType){
            case UserNotFound -> {
                System.out.println("系统消息：用户不存在！");
            }case CurrentUserList -> {

            }case NameDuplicated -> {

            }
        }
    }
}
