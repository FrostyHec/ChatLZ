package BackEnd.MessageTypePack;

import BackEnd.Tools.ByteConvert;
import BackEnd.Tools.EnumUtils;

import java.util.Arrays;

public enum RequestToSeverMsg implements enum2ByteArray{
    GetCurrentUserList;
    public static final int length=4;
    public static RequestToSeverMsg get(int index) {
        return EnumUtils.getByIndex(RequestToSeverMsg.class,index);
    }
}
