package BackEnd.Server;

import BackEnd.Exception.UserNotFoundException;
import BackEnd.PackageHandler;
import BackEnd.Tools.ByteConvert;

import java.net.SocketException;
import java.nio.channels.SocketChannel;
import java.util.*;

public class ClientsList {
    private final Map<List<Byte>, SingleClientData> nameData;
    private final Map<SocketChannel, PackageHandler> helperData;
    private final Map<SingleClientData, List<Byte>> dataName;

    public ClientsList() {
        nameData = new HashMap<>();
        helperData = new HashMap<>();
        dataName = new HashMap<>();
    }

    public boolean hasInitName(SocketChannel channel) {
        return helperData.containsKey(channel);
    }

    public PackageHandler getPackageHelper(SocketChannel channel) {
        return helperData.get(channel);
    }

    public void add(byte[] name, SocketChannel channel, PackageHandler helper) {
        ArrayList<Byte> temp = ByteConvert.byteArray2List(name);
        SingleClientData data = new SingleClientData(channel);
        if (nameData.containsKey(temp)) throw new RuntimeException("名字重了");//TODO 重复名称处理
        nameData.put(temp, data);
        dataName.put(data, temp);
        helperData.put(channel, helper);
    }
    public void remove(SocketChannel channel) {
        SingleClientData c = new SingleClientData(channel);
        if (!dataName.containsKey(c)) return;//不需要移除，已经移除过了
        List<Byte> name = dataName.get(c);
        dataName.remove(c);
        nameData.remove(name);
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