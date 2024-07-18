package main.java.com.service.proto.util;

import main.java.com.service.proto.http.HttpRequest;

import java.io.IOException;
import java.io.InputStream;

/**
 * @ClassName: ProtoUtil
 * @Description: 协议工具类
 * @author: liuchen
 * @date: 2024/7/16 0:05
 */

public class ProtoUtil {

    //UTF-8编码
    public static final byte UTF8 = 1;
    //GBK编码
    public static final byte GBK = 2;

    public static HttpRequest readRequest(InputStream input) throws IOException {

        System.out.println("input_avail:"+input.available());
//        int content;
//        System.out.println("content：");
//        while ((content = input.read())!=-1){
//            System.out.print((byte)content);
//        }
        //读取编码
        byte[] encodeByte = new byte[1];
        input.read(encodeByte);
        byte encode = encodeByte[0];
        System.out.println("encode:"+encode);


        //读取命令长度
        byte[] commmandLengthBytes = new byte[4];
        input.read(commmandLengthBytes);
        int commandLength = ByteUtils.byte2Int(commmandLengthBytes);
        System.out.println("commandLength:"+commandLength);
        //读取命令
        byte[] commandBytes = new byte[commandLength];
        input.read(commandBytes);

        String command = "";
        if(UTF8 == encode){
            command = new String(commandBytes, "UTF-8");
        }else if(GBK == encode){
            command = new String(commandBytes, "GBK");
        }
        System.out.println("command:"+command);

        HttpRequest httpRequest = new HttpRequest();
        return httpRequest;

    }
}
