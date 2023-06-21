package BackEnd.Server;

import BackEnd.MessageTypePack.RequestToSeverMsg;
import BackEnd.MessageTypePack.SystemMessageType;
import BackEnd.Tools.ByteConvert;

import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class BackwardHandler {
    private Server server;

    public BackwardHandler(Server server) {
        this.server = server;
    }

    public void noSuchUser(byte[] sender) {
        server.announce(getReceivers(sender), SystemMessageType.UserNotFound);
    }

    public void analyzeRequest(byte[] sender, byte[] message) {
        RequestToSeverMsg request = RequestToSeverMsg.get(ByteConvert.byteArray2Int(message));
        switch (request){
            case GetCurrentUserList -> sendCurrentUserList(sender);
        }
    }

    private void sendCurrentUserList(byte[] sender) {
        ClientsList clientsList = server.getClientsList();
        Set<List<Byte>> names=clientsList.getUserNameSet();
        byte[] body=ByteConvert.doubleByteList2Array(new ArrayList<>(names));
        server.announce(getReceivers(sender),body);
    }

    private List<byte[]> getReceivers(byte[]... receivers){
        return new ArrayList<>(Arrays.asList(receivers));
    }

    public void duplicatedUser(SocketChannel senderChannel) {
        server.announce(senderChannel,SystemMessageType.NameDuplicated.toByte());
    }

    public void connectSucceed(byte[] sender) {
        server.announce(getReceivers(sender),SystemMessageType.InitSucceed);
    }
}
