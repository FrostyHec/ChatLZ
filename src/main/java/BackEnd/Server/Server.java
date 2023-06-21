package BackEnd.Server;

import BackEnd.Exception.UserNotFoundException;
import BackEnd.Exception.NameSetting.UserDuplicatedException;
import BackEnd.MessageTypePack.MessageType;
import BackEnd.MessageTypePack.SystemMessageType;
import BackEnd.PackageHandler;
import BackEnd.WriteHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.List;

public class Server implements Runnable {
    private int port;
    private boolean isRunning;
    private ServerSocketChannel serverSocket;
    private Selector selector;
    private final ClientsList clientsList;
    private final WriteHandler writer;
    private final BackwardHandler backwardHandler;

    public Server(int port) {
        this.port = port;
        isRunning = false;
        writer = new WriteHandler();
        clientsList = new ClientsList();
        backwardHandler = new BackwardHandler(this);
    }

    private void init() {
        System.out.println("Server invoked");
        try {
            serverSocket = ServerSocketChannel.open();
            serverSocket.socket().bind(new InetSocketAddress(port));
            serverSocket.configureBlocking(false);
            System.out.println("Server is listening on port:" + port);

            //创建Selector并注册SeverSocketChannel
            selector = Selector.open();
            serverSocket.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("Selector binding succeed");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        init();
        isRunning = true;
        while (isRunning) {
            try {
                //阻塞到有事件发生
                if (selector.select() == 0) continue;
                for (SelectionKey key : selector.selectedKeys()) {
                    if(!key.isValid()) continue;//如果key已经失效则移除key
                    if (key.isAcceptable()) {
                        connectHandle();
                    } else if (key.isReadable()) {
                        //读取事件
                        SocketChannel channel = (SocketChannel) key.channel();
                        PackageHandler helper = clientsList.hasInitPackageHelper(channel) ? clientsList.getPackageHelper(channel) : new PackageHandler();
                        ReadThread reader = new ReadThread(channel, this, helper);
                        new Thread(reader).start();
                    }
                }
                selector.selectedKeys().clear();
            } catch (IOException e) {
                e.printStackTrace();
                close();
            }
        }
    }

    public void announce(List<byte[]> receivers, byte[] message) {//管理员通知的信息
        byte[] adminName = WriteHandler.getNameByte("Admin");
        for (byte[] receiver : receivers) {
            forward(MessageType.Sentence, adminName, receiver, message);
        }
    }

    public void announce(List<byte[]> receivers, SystemMessageType msg) {
        announce(receivers, msg, null);
    }

    public void announce(List<byte[]> receivers, SystemMessageType msg, String details) {//系统通知的信息
        byte[] systemName = WriteHandler.getNameByte("System");
        byte[] messageHead = msg.toByte(), message = null;
        if (details == null) {
            message = messageHead;
        } else {
            byte[] messageBody = details.getBytes();
            message = new byte[messageHead.length + messageBody.length];
            //TODO 测试这一模块
            System.arraycopy(messageHead, 0, message, 0, messageHead.length);
            System.arraycopy(messageBody, 0, message, messageHead.length, messageBody.length);
        }
        for (byte[] receiver : receivers) {
            forward(MessageType.SystemMessage, systemName, receiver, message);
        }
    }

    public void forward(MessageType type, byte[] sender, byte[] receiver, byte[] message) {
        ByteBuffer buffer = writer.write(type, sender, receiver, message);
        try {
            SocketChannel channel = clientsList.get(receiver);
            channel.write(buffer);
        } catch (UserNotFoundException e) {
            backwardHandler.noSuchUser(sender);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void announce(SocketChannel receiverChannel,byte[] msg){
        byte[] empty=new byte[MessageType.userNameLength];
        try {
            receiverChannel.write(writer.write(MessageType.SystemMessage,empty,empty,msg));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void connectHandle() throws IOException {
        //接收并且注册
        SocketChannel clientChannel = serverSocket.accept();
        if (clientChannel == null) return;
        clientChannel.configureBlocking(false);
        clientChannel.register(selector, SelectionKey.OP_READ);
        System.out.println("Accept connection from " + clientChannel.getRemoteAddress());
    }

    public void close() {
        isRunning = false;
        try {
            selector.close();
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initClientData(byte[] sender, SocketChannel channel, PackageHandler helper) {
        try {
            clientsList.add(sender, channel, helper);
        } catch (UserDuplicatedException e) {
            backwardHandler.duplicatedUser(channel);
            return;
        }
        System.out.println("Successfully set username:");
        clientsList.printList();
        backwardHandler.connectSucceed(sender);//回传登录成功
    }

    public synchronized void userOffLine(SocketChannel channel) {
        clientsList.remove(channel);
        try {
            channel.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void respondRequest(byte[] sender, byte[] msg) {
        backwardHandler.analyzeRequest(sender, msg);
    }

    public ClientsList getClientsList() {
        return clientsList;
    }
}
