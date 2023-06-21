package BackEnd.MessageTypePack;

import BackEnd.Tools.ByteConvert;

public interface enum2ByteArray {
    default byte[] toByte(){
        return ByteConvert.int2ByteArray(((Enum<?>)this).ordinal());
    }
}
