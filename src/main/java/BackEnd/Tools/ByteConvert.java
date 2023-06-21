package BackEnd.Tools;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class ByteConvert {
    public static int byteArray2Int(byte[] bytes) {
        if (bytes.length != 4) throw new RuntimeException("Byte length isn't 4!");
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(bytes));
        try {
            return dis.readInt();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static byte[] int2ByteArray(int val){
        ByteBuffer b=ByteBuffer.allocate(4);
        b.putInt(val);
        return b.array();
    }
    public static byte[] byteList2Array(List<Byte> ba) {
        byte[] ans = new byte[ba.size()];
        for (int i = 0; i < ba.size(); i++) {
            ans[i] = ba.get(i);
        }
        return ans;
    }
    public static ArrayList<Byte> byteArray2List(byte[] name) {
        ArrayList<Byte> arr = new ArrayList<>();
        for (byte b : name) {
            arr.add(b);
        }
        return arr;
    }
    public static byte[] doubleByteList2Array(List<List<Byte>> lists){//与第钱一个方法出现了类型擦除，不能重载，故要换个名
        List<Byte> byteList = new ArrayList<>();
        for (List<Byte> list : lists) {
            byteList.addAll(list);
        }
        byte[] byteArray = new byte[byteList.size()];
        for (int i = 0; i < byteList.size(); i++) {
            byteArray[i] = byteList.get(i);
        }
        return byteArray;
    }
}
