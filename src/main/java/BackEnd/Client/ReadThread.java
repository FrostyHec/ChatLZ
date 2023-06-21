package BackEnd.Client;

import BackEnd.MessageTypePack.MessageType;
import BackEnd.MessageTypePack.SystemMessageType;
import BackEnd.PackageHandler;
import BackEnd.Tools.ByteConvert;

import java.lang.reflect.Method;
import java.net.SocketException;
import java.nio.channels.SocketChannel;

public class ReadThread implements Runnable {
    SocketChannel socketChannel;
    ReadHandler reader;
    Client client;

    public ReadThread(SocketChannel socketChannel, ReadHandler reader, Client client) {
        this.socketChannel = socketChannel;
        this.reader = reader;
        this.client = client;
    }

    @Override
    public void run() {
        System.out.println("监听线程开启");
        //包处理
        PackageHandler helper = new PackageHandler();
        Method method = null;
        try {
            method = getClass().getMethod("messageHandle", MessageType.class, byte[].class, byte[].class, byte[].class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        while (socketChannel.isConnected()) {
            try {
                helper.parse(socketChannel, this, method);
            } catch (SocketException e) {
                System.out.println("已与服务器断开！");
                client.linkBreak();
                //TODO 断线重连
            }
        }
        System.out.println("监听线程结束");
    }

    public void messageHandle(MessageType type, byte[] sender, byte[] receiver, byte[] bodyByte) {
        switch (type) {
            case File -> {
                //TODO 文件读写
            }
            case Sentence -> {
                reader.read(sender, new String(bodyByte));
            }
            case SystemMessage -> {
                reader.read(SystemMessageType.get(ByteConvert.byteArray2Int(bodyByte)));
            }
        }
    }
}

