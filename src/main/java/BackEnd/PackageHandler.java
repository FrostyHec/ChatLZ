package BackEnd;

import BackEnd.MessageTypePack.MessageType;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import BackEnd.Tools.ByteConvert;

public class PackageHandler {
    public int bodyLength;
    public int remainLength;
    public MessageType messageType;
    public byte[] headByte;
    private List<byte[]> caches;
    public byte[] sender, receiver;

    public PackageHandler() {
        init();
    }

    public synchronized void parse(SocketChannel channel,Object obj, Method handler) throws SocketException {//注意要加锁
        ByteBuffer buffer = ByteBuffer.allocate(78);
        try {
            channel.read(buffer);//触发一次阅读读取事件
            buffer.flip();
            while (buffer.hasRemaining()) {
                if (hasNotReadHead()) {//没有读出包头
                    if (buffer.remaining() >= MessageType.headLength) {
                        if (hasTempHead()) {
                            collageHeadByte(buffer);
                        } else {
                            buffer.get(headByte);
                        }
                        parseHeadByte();
                        System.out.println(remainLength);
                    } else {
                        int start=buffer.position(),len=buffer.limit()-start;
                        byte[] tempHead=new byte[len];
                        System.arraycopy(buffer.array(),start,tempHead,0,len);
                        addTempHead(tempHead);//不满一个包
                        break;
                    }
                } else {//读出包头
                    if (buffer.remaining() >= remainLength) {//大于等于一个包，说明本次传输结束
                        byte[] arr=new byte[remainLength];
                        for (int i = 0; i < arr.length; i++) {
                            arr[i]= buffer.get();
                        }
                        addCache(arr);
                        handler.invoke(obj,messageType, sender, receiver, getBodyByteArray());
                        init();
                    } else {//没读完继续缓存
                        int start=buffer.position(),len=buffer.limit()-start;
                        byte[] cache=new byte[len];
                        System.arraycopy(buffer.array(),start,cache,0,len);
                        addCache(cache);//注意一定要克隆！
                        break;
                    }
                }
            }
            buffer.clear();
        }  catch (SocketException e) {
            throw e;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InvocationTargetException |
                 IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }

    private void init() {
        remainLength = 0;
        bodyLength = -1;
        messageType = null;
        headByte = new byte[MessageType.headLength];
        caches = new ArrayList<>();
    }

    private void parseHeadByte() {
        setLength(Arrays.copyOfRange(headByte, 0, 4));//得到包长度
        messageType = MessageType.get(ByteConvert.byteArray2Int(Arrays.copyOfRange(headByte, 4, 8)));//得到包类型
        sender = Arrays.copyOfRange(headByte, 8, 40);//得到数据源
        receiver = Arrays.copyOfRange(headByte, 40, 72);//得到接收者
    }

    private byte[] getBodyByteArray() {
        byte[] bodyByte = new byte[bodyLength];
        int destPos = 0;
        for (byte[] cache : caches) {
            System.arraycopy(cache, 0, bodyByte, destPos, cache.length);
            destPos += cache.length;

        }
        return bodyByte;
    }

    private void addCache(byte[] arr) {
        caches.add(arr);
        remainLength -= arr.length;
    }

    private void setLength(byte[] arr) {
        int length = ByteConvert.byteArray2Int(arr);
        if (length == -1) throw new RuntimeException("Length can't be -1!");
        bodyLength = length;
        remainLength = length;
    }


    private boolean hasNotReadHead() {
        return bodyLength == -1;
    }

    private byte[] tempHead;
    private int filledLength;

    private void addTempHead(byte[] arr) {
        if (arr.length >= MessageType.headLength) throw new RuntimeException("No need to create temp head");
        tempHead = new byte[MessageType.headLength];
        System.arraycopy(arr, 0, tempHead, 0, arr.length);
        filledLength = arr.length;
    }

    private boolean hasTempHead() {
        return tempHead != null;
    }

    private void collageHeadByte(ByteBuffer buffer) {
        while (filledLength < tempHead.length) {//填充完headByte
            tempHead[filledLength] = buffer.get();
        }
        headByte = tempHead;
        //初始化
        tempHead = null;
        filledLength = 0;
    }
}
