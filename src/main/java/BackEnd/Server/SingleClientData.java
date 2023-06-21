package BackEnd.Server;

import java.nio.channels.SocketChannel;

public class SingleClientData {
    public final SocketChannel channel;

    public SingleClientData(SocketChannel channel) {
        this.channel = channel;
    }
}
