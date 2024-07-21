package main.java.com.service.proto.util;

import main.java.com.service.proto.http.HttpRequest;
import main.java.com.service.proto.http.HttpResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: ProtoUtil
 * @Description: 协议工具类
 * @author: liuchen
 * @date: 2024/7/16 0:05
 */

public class ProtoUtil {


    /**
     * 解析http请求（目前有bug）
     * @param in
     * @return
     * @throws Exception
     */
    public static HttpRequest readRequest(InputStream in) throws Exception {

        BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));

        HttpRequest request = new HttpRequest();

        String line=reader.readLine();
        Map<String,String> head = new HashMap<>();
        StringBuilder body = new StringBuilder();
        boolean isBody = false;
        boolean isLineOne = true;
        //读取 请求行，请求头，请求体     (这里出了bug，读取到body部分时，它就突然不继续往下执行了)
        while (line!=null){
            System.out.println(line);
            if(isBody){
                body.append(line);
            }else {
                if(line.isEmpty()){
                    //遇到空行后，开始读取body
                    isBody=true;
                }else {
                    if(isLineOne){
                        //读第一行，并提取请求行中的 method，url,version
                        String[] requestLine = line.split(" ");
                        if(requestLine.length==3){
                            request.setMethod(requestLine[0]);
                            request.setUri(requestLine[1]);
                            request.setVersion(requestLine[2]);
                        }
                        isLineOne=false;
                    }else {
                        //读取请求头部分
                        String[] heads = line.split(": ");
                        if(heads.length==2){
                            head.put(heads[0],heads[1]);
                        }
                    }
                }
            }
            line=reader.readLine();

        }
        request.setHeaders(head);
        request.setMessage(body.toString());

        return request;

    }

    /**
     * HTTP 请求可以分为三部分：
     * 1. 请求行：包括请求方法、URI 和 HTTP 协议版本
     * 2. 请求头：从第二行开始，直到一个空行为止
     * 3. 消息正文：紧跟在空行后的所有内容，长度由请求头中的 Content-Length 决定
     *
     * 本方法将 InputStream 中的 HTTP 请求数据解析为一个 Request 对象
     *
     * @param reqStream  包含 HTTP 请求数据的输入流
     * @return           一个表示 HTTP 请求的 Request 对象
     * @throws IOException 当发生 I/O 错误时抛出
     */
    public static HttpRequest parse2request(InputStream reqStream) throws IOException {
        // 使用 BufferedReader 和 InputStreamReader 读取输入流中的数据
        BufferedReader httpReader = new BufferedReader(new InputStreamReader(reqStream, "UTF-8"));

        // 创建一个新的 Request 对象
        HttpRequest httpRequest = new HttpRequest();

        // 解析请求行并设置到 Request 对象中
        decodeRequestLine(httpReader, httpRequest);

        // 解析请求头并设置到 Request 对象中
        decodeRequestHeader(httpReader, httpRequest);

        // 解析消息正文并设置到 Request 对象中
        decodeRequestMessage(httpReader, httpRequest);

        // 返回解析后的 Request 对象
        return httpRequest;
    }

    /**
     * 返回成功的response,当前默认返回的类型是json
     * @param request
     * @param result
     * @return
     */
    public static String buildSuccessResponse(HttpRequest request,String result,int code){

        //创建一个新的response对象
        HttpResponse response = new HttpResponse();

        //设置response的版本，状态码，状态信息
        response.setVersion(request.getVersion());
        response.setCode(code);
        if(code==200){
            response.setStatus("ok");
        }else{
            response.setStatus("error");
        }

        //设置header响应头
        HashMap<String,String> header = new HashMap<>();
        header.put("Content-Type", "application/json");
        header.put("Content-Length", String.valueOf(result.getBytes().length));
        response.setHeaders(header);

        //设置响应正文
        response.setMessage(result);

        //将response转成字符串类型
        StringBuffer stringBuffer = new StringBuffer();
        return response2String(response,stringBuffer);

    }



    private static String response2String(HttpResponse response, StringBuffer stringBuffer) {

        //先转请求行
        stringBuffer.append(response.getVersion()).append(" ").append(response.getCode())
                .append(" ").append(response.getStatus()).append("\n");

        //转 请求头header
        for(Map.Entry<String,String> entry : response.getHeaders().entrySet()){
            stringBuffer.append(entry.getKey()).append(":").append(entry.getValue()).append("\n");
        }

        stringBuffer.append("\n");

        //转 请求体message
        stringBuffer.append(response.getMessage());

        return stringBuffer.toString();
    }


    /**
     * 根据标准的HTTP协议，解析请求行
     *
     * @param reader
     * @param request
     */
    private static void decodeRequestLine(BufferedReader reader, HttpRequest request) throws IOException {
        String[] strs = reader.readLine().split(" ");
        assert strs.length == 3;
        request.setMethod(strs[0]);
        request.setUri(strs[1]);
        request.setVersion(strs[2]);
    }

    /**
     * 根据标准 HTTP 协议，解析请求头
     *
     * @param reader  读取请求头的 BufferedReader 对象
     * @param request 存储请求信息的 Request 对象
     * @throws IOException 当读取请求头信息时发生 I/O 异常时，将抛出该异常
     */
    private static void decodeRequestHeader(BufferedReader reader, HttpRequest request) throws IOException {
        // 创建一个 Map 对象，用于存储请求头信息
        Map<String, String> headers = new HashMap<>(16);
        // 读取请求头信息，每行都是一个键值对，以空行结束
        String line = reader.readLine();
        String[] kv;
        while (!"".equals(line)) {
            System.out.println(line);
            // 将每行请求头信息按冒号分隔，分别作为键和值存入 Map 中
            kv = line.split(":");
            assert kv.length == 2;
            headers.put(kv[0].trim(), kv[1].trim());
            line = reader.readLine();
        }
        // 将解析出来的请求头信息存入 Request 对象中
        request.setHeaders(headers);
    }

    /**
     * 根据标注HTTP协议，解析正文
     *
     * @param reader    输入流读取器，用于读取请求中的数据
     * @param request   Request 对象，表示 HTTP 请求
     * @throws IOException 当发生 I/O 错误时抛出
     */
    private static void decodeRequestMessage(BufferedReader reader, HttpRequest request) throws IOException {
        // 从请求头中获取 Content-Length，如果没有，则默认为 0
        int contentLen = Integer.parseInt(request.getHeaders().getOrDefault("Content-Length", "0"));

        // 如果 Content-Length 为 0，表示没有请求正文，直接返回。
        // 例如 GET 和 OPTIONS 请求通常不包含请求正文
        if (contentLen == 0) {
            return;
        }

        // 根据 Content-Length 创建一个字符数组来存储请求正文
        char[] message = new char[contentLen];

        // 使用 BufferedReader 读取请求正文
        reader.read(message);
        System.out.println(new String(message));

        // 将字符数组转换为字符串，并将其设置为 Request 对象的 message
        request.setMessage(new String(message));
    }
}
