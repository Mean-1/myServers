package main.java.com.service.proto.util;

/**
 * @ClassName: ByteUtils
 * @Description: 字节转化工具
 * @author: liuchen
 * @date: 2024/7/16 21:45
 */

public class ByteUtils {

    /**
     * 将byte数组转化为int数字
     * @param bytes
     * @return
     */
    public static int byte2Int(byte[] bytes){
        int result = 0;
        for (int i = 0; i < 4; i++) {
            result <<= 8;
            result |= (bytes[i] & 0xFF);
        }
        return result;
    }

    /**
     * 将int类型数字转化为byte数组
     * @param
     * @return
     */
    public static byte[] int2ByteArray(int i){
        byte[] result = new byte[4];
        result[0]  = (byte)(( i >> 24 ) & 0xFF);
        result[1]  = (byte)(( i >> 16 ) & 0xFF);
        result[2]  = (byte)(( i >> 8 ) & 0xFF);
        result[3]  = (byte)(i & 0xFF);
        return result;
    }
}
