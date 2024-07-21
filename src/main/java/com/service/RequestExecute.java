package main.java.com.service;

import main.java.com.service.control.Controller;
import main.java.com.service.control.IController;
import main.java.com.service.control.Request;
import main.java.com.service.proto.http.HttpRequest;
import main.java.com.service.proto.util.ProtoUtil;
import main.java.com.service.util.ScanUtil;

import java.io.*;
import java.lang.reflect.Method;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: RequestExecute
 * @Description: 处理请求结果
 * @author: liuchen
 * @date: 2024/7/15 21:42
 */

public class RequestExecute extends Thread {

    private static Map<String, MethodPair> methodMap = new HashMap<>();
    private Socket socket;

    //获取所有controller注解下的方法
    public void init() throws Exception {
        methodMap.clear();
        //1.扫描指定包下所有Controller类文件,并过滤 @Controller 注解
        List<String> classNameList = ScanUtil.scanClassFromDir("src/main/java/com/service/control/impl");
        for (String className : classNameList) {
            Class<?> forName = Class.forName(className);
            if (forName.isAnnotationPresent(Controller.class)) {
                // 2. 实例化controller对象,解析包含Request注解的方法，并初始化放入methodmap中
                IController controller = (IController) forName.newInstance();
                for (Method method : forName.getMethods()) {
                    if (method.isAnnotationPresent(Request.class)) {

                        Request annotation = method.getAnnotation(Request.class);
                        MethodPair methodPair = new MethodPair();
                        methodPair.method=method;
                        methodPair.impl=controller;
                        methodMap.put(annotation.url(),methodPair);
                    }
                }
            }
        }

    }

    public RequestExecute(Socket socket){
        this.socket=socket;
    }

    private class MethodPair{
        IController impl;
        Method method;
    }
    public void run(){
        //输入流
        InputStream in = null;
        InputStreamReader reader = null;
        BufferedReader bufferedReader = null;

        //输出流
        OutputStream out = null;
        PrintWriter print = null;
        //请求
        HttpRequest httpRequest = null;

        try {
            //初始化输出流
            out = socket.getOutputStream();
            print = new PrintWriter(out);

            //初始化输入流
            in = socket.getInputStream();
            reader = new InputStreamReader(in);
            bufferedReader = new BufferedReader(reader);
            String line = null;
            int lineNum = 1;
            //请求头
            String head = "";
            //请求地址
            String reqPath = "";
            //请求ip地址
            String host = "";
            //循环读取输入字符流的数据
            /*while ((line=bufferedReader.readLine())!=null){
                System.out.println(line);
                //取第一行
                if(lineNum==1){
                    String[] info = line.split(" ");
                    if(info!=null&&info.length>2){
                        head = info[0];
                        reqPath=info[1];//获取请求路径
                    }else {
                        throw new RuntimeException("请求解析失败");
                    }

                }else{
                    if(line.contains("host")){
                        String[] info = line.split(": ");
                        if(info!=null&&info.length==2){
                            host=info[1];
                        }
                    }

                }
                lineNum++;

                if(line.equals("")){
                    break;
                }
            }*/

            // 1. 解析http协议
//            httpRequest = ProtoUtil.readRequest(in);
            httpRequest = ProtoUtil.parse2request(in);

            //对message中的中文进行解码
            httpRequest.setMessage(URLDecoder.decode(httpRequest.getMessage(), StandardCharsets.UTF_8.toString()));



            // 2.执行url对应方法
            MethodPair methodPair = methodMap.get(httpRequest.getUri());

            //返回结果
            Object result = null;
            if(methodPair!=null){
                result = methodPair.method.invoke(methodPair.impl, httpRequest);
            }


            // 3.返回response

            //当有结果则返回成功的response
            String response = ProtoUtil.buildSuccessResponse(httpRequest, result.toString(),200);
            System.out.println("-------response-------");
            System.out.println(response);
            print.println(response);
            print.flush();
            System.out.println("请求类型："+httpRequest.getMethod());
            System.out.println("解析请求地址："+httpRequest.getUri());
            System.out.println("返回结果："+result);


        }catch (Exception e){
            e.printStackTrace();
            //返回错误的response
            String response = ProtoUtil.buildSuccessResponse(httpRequest, e.toString(),500);
            print.println(response);
            print.flush();

            throw new RuntimeException(e);
        }finally {
            try {
                in.close();
                reader.close();
                bufferedReader.close();
                out.close();
                print.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    //成功的响应返回
    public void response(OutputStream out,String filePath,String suf){

    }
    //失败的响应返回
    public void response404(OutputStream out){

    }
}
