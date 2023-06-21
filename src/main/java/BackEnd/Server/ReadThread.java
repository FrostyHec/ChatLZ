package BackEnd.Server;

import BackEnd.MessageTypePack.MessageType;
import BackEnd.PackageHandler;

import java.lang.reflect.Method;
import java.net.SocketException;
import java.nio.channels.SocketChannel;

public class ReadThread implements Runnable {
    SocketChannel channel;
    Server server;
    PackageHandler helper;

    public ReadThread(SocketChannel socketChannel, Server server, PackageHandler helper) {
        channel = socketChannel;
        this.server = server;
        this.helper = helper;
    }

    @Override
    public void run() {
        //包处理
        Method method = null;
        try {
            method = getClass().getMethod("messageHandle", MessageType.class, byte[].class, byte[].class, byte[].class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        try {
            if (!channel.isConnected()) {
                return;//用户已经退出
            }
            helper.parse(channel, this, method);
        } catch (SocketException e) {
            //TODO SocketException可能包含其他异常，需要继续你处理
            server.userOffLine(channel);
        }
    }

    public void messageHandle(MessageType type, byte[] sender, byte[] receiver, byte[] bodyByte) {
        System.out.println(type.name());
        switch (type) {
            case File, Sentence -> {
                server.forward(type, sender, receiver, bodyByte);
            }
            case Initialize -> {
                server.initClientData(sender, channel, helper);
            }
            case RequestToServer -> {
                server.respondRequest(sender, bodyByte);
            }
        }
    }
}
