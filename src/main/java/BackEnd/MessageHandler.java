package BackEnd;

import BackEnd.MessageTypePack.MessageType;

public interface MessageHandler {
    void messageHandle(MessageType type, byte[] sender, byte[] receiver, byte[] bodyByte);
}
