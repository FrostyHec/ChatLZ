package BackEnd.Tools;

public class EnumUtils {
    public static <T extends Enum<T>> T getByIndex(Class<T> enumClass, int index){//泛型的使用
        T[] arr=enumClass.getEnumConstants();
        if(arr.length<=index) throw new RuntimeException("index invalid: "+index+"while type length: "+arr.length);
        return arr[index];
    }
}
