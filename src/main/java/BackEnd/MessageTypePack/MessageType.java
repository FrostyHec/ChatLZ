package BackEnd.MessageTypePack;

import BackEnd.Tools.EnumUtils;

public enum MessageType{
    Sentence,File,Initialize,RequestToServer,SystemMessage;
    public static final int headLength=72,userNameLength=32;
    public static MessageType get(int index) {
        return EnumUtils.getByIndex(MessageType.class,index);
    }
    //4字节长度+4字节包信息类型+32字节的来源(后面改成ID)+32字节的目标=72字节

}
