package BackEnd.MessageTypePack;

import BackEnd.Tools.ByteConvert;
import BackEnd.Tools.EnumUtils;

public enum SystemMessageType implements enum2ByteArray {
    CurrentUserList,UserNotFound,NameDuplicated,InitSucceed;
    public static SystemMessageType get(int index) {
        return EnumUtils.getByIndex(SystemMessageType.class,index);
    }

}
