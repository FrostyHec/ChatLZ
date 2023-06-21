package BackEnd.Server;

import BackEnd.Exception.UserNotFoundException;
import BackEnd.Exception.UserDuplicatedException;
import BackEnd.PackageHandler;
import BackEnd.Tools.ByteConvert;

import java.nio.channels.SocketChannel;
import java.util.*;

public class ClientsList {
    private final Map<List<Byte>, SingleClientData> nameData;
    private final Map<SocketChannel, List<Byte>> channelName;
    private final Map<SocketChannel, PackageHandler> channelHelper;

    public ClientsList() {
        nameData = new HashMap<>();
        channelHelper = new HashMap<>();
        channelName = new HashMap<>();
    }

    public boolean hasInitPackageHelper(SocketChannel channel) {
        return channelHelper.containsKey(channel);
    }

    public PackageHandler getPackageHelper(SocketChannel channel) {
        return channelHelper.get(channel);
    }

    public void add(byte[] name, SocketChannel channel, PackageHandler helper) throws UserDuplicatedException {
        ArrayList<Byte> temp = ByteConvert.byteArray2List(name);
        SingleClientData data = new SingleClientData(channel);
        if (nameData.containsKey(temp)) throw new UserDuplicatedException();//TODO 重复名称处理
        nameData.put(temp, data);
        channelName.put(channel, temp);
        channelHelper.put(channel, helper);
    }

    public synchronized void remove(SocketChannel channel) {
        if (!channelName.containsKey(channel)){
            if(channel.isConnected()) System.out.println("一个未初始化的channel断连了");
            return;
        }
        List<Byte> name = channelName.get(channel);
        channelName.remove(channel);
        nameData.remove(name);
        channelHelper.remove(channel);
        System.out.println("用户:" + new String(ByteConvert.byteList2Array(name)).trim() + " 已退出聊天室");
    }

    public SocketChannel get(byte[] to) throws UserNotFoundException {
        ArrayList<Byte> temp = ByteConvert.byteArray2List(to);
        if (nameData.containsKey(temp)) return nameData.get(temp).channel;
        else throw new UserNotFoundException();
    }

    public void printList() {
        System.out.println("当前用户列表");
        for (var sets : nameData.keySet()) {
            System.out.println(new String(ByteConvert.byteList2Array(sets)).trim());
        }
    }

    public Set<List<Byte>> getUserNameSet() {//new hash set;
        return new HashSet<>(nameData.keySet());
    }
}