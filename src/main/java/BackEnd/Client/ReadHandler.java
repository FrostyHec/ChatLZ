package BackEnd.Client;

import BackEnd.MessageTypePack.SystemMessageType;

public class ReadHandler {
    String savePath;
    private Client client;
    public ReadHandler(String savePath) {
        this.savePath = savePath;
    }

    public void initialize(Client client){
        this.client=client;
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
                System.out.println("系统消息：名称重复！");
                client.targetResetName();
            }case InitSucceed -> {
                System.out.println("初始化成功!");
                client.initSucceed();
            }
        }
    }
}
