package BackEnd.Server;

import BackEnd.PackageHandler;

import java.nio.channels.SocketChannel;
import java.util.ArrayList;

public class SingleClientData {
    public final SocketChannel channel;

    public SingleClientData(SocketChannel channel) {
        this.channel = channel;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof SingleClientData)) return false;
        return ((SingleClientData) o).channel == this.channel;
    }
    @Override
    public int hashCode(){
        return channel.hashCode();
    }
}
