package BackEnd.Client;

import BackEnd.MessageTypePack.RequestToSeverMsg;
import BackEnd.WriteHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class Client {
    private boolean isConnected;
    private SocketChannel socketChannel;
    private final ReadHandler reader;
    private final WriteHandler writer;
    private Thread readThread;

    public Client(ReadHandler rh, WriteHandler wh) {
        reader = rh;
        writer = wh;
    }

    public void connect(String IP, int port) {
        try {
            socketChannel = SocketChannel.open(new InetSocketAddress(IP, port));
            socketChannel.configureBlocking(false);
            System.out.println("Linking Server");
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Link Succeed");
        isConnected = true;

        //开启监听线程
        readThread = new Thread(new ReadThread(socketChannel, reader,this));
        readThread.start();
    }
    public void setName(String name) throws Exception {
        write(writer.initName(name));
    }
    public void write(byte[] source,byte[] target,String words){
        write(writer.write(source,target,words));
    }
    private void write(ByteBuffer buffer){
        try {
            socketChannel.write(buffer);
            System.out.println("发送成功");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void linkBreak(){
        disconnect();
    }
    public void disconnect() {
        if (socketChannel != null) {
            try {
                socketChannel.close();
                readThread.interrupt();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        isConnected = false;
    }
    public void requestToAdmin(byte[] source,RequestToSeverMsg msg){
        write(writer.write(source,msg));
    }
}
