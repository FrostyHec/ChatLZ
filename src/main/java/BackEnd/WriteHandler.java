package BackEnd;

import BackEnd.MessageTypePack.MessageType;
import BackEnd.MessageTypePack.RequestToSeverMsg;

import java.nio.ByteBuffer;

public class WriteHandler {
    public static byte[] getNameByte(String name){
        byte[] nameByteArray=new byte[MessageType.userNameLength],origin2=name.getBytes();
        System.arraycopy(origin2,0,nameByteArray,0,origin2.length);
        return nameByteArray;
    }
    public ByteBuffer initName(String userName) throws Exception {//如果不是32字符以内则抛出异常
        byte[] arr = userName.getBytes();
        int len = arr.length, maxLen = MessageType.userNameLength;
        if (len > maxLen) throw new Exception("User name too long: length " + len);//超过32个字符

        byte[] name = new byte[maxLen], empty = new byte[maxLen];
        System.arraycopy(arr, 0, name, 0, len);
        return write(MessageType.Initialize, name, empty, new byte[1]);
    }

    public ByteBuffer write(byte[] source, RequestToSeverMsg msg){
        byte[] empty=new byte[MessageType.userNameLength];
        return write(MessageType.RequestToServer,source,empty,msg.toByte());
    }
    public ByteBuffer write(byte[] source, byte[] target, String words) {
        return write(MessageType.Sentence, source, target, words.getBytes());
    }

    public ByteBuffer write(MessageType type, byte[] source, byte[] target, byte[] message) {
        ByteBuffer buffer = ByteBuffer.allocate(MessageType.headLength + message.length);
        buffer.putInt(message.length);//长度
        buffer.putInt(type.ordinal());//消息类型
        buffer.put(source);//消息源用户
        buffer.put(target);//目标用户
        buffer.put(message);//信息
        buffer.flip();
        return buffer;
    }
}
