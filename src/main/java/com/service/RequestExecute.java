package main.java.com.service;

import main.java.com.data.Data;
import main.java.com.service.control.Controller;
import main.java.com.service.control.IController;
import main.java.com.service.control.Request;
import main.java.com.service.proto.http.HttpRequest;
import main.java.com.service.proto.http.HttpResponse;
import main.java.com.service.proto.util.ProtoUtil;
import main.java.com.service.util.ScanUtil;

import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import static com.sun.xml.internal.ws.api.message.Packet.Status.Response;

/**
 * @ClassName: RequestExecute
 * @Description: 处理请求结果
 * @author: liuchen
 * @date: 2024/7/15 21:42
 */

public class RequestExecute extends Thread {

    private static Map<String, MethodPair> methodMap = new HashMap<>();
    private Socket socket;

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
        PrintWriter printWriter = null;
        HttpRequest httpRequest = new HttpRequest((byte) 1, "requestCommand", 11);
        HttpResponse httpResponse = new HttpResponse((byte) 1, "responseCommand", 11);
        try {
            //初始化输出流
            out = socket.getOutputStream();
            printWriter = new PrintWriter(out);

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
            while ((line=bufferedReader.readLine())!=null){
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
            }

            // 1. 解析http协议
//            ProtoUtil.readRequest(in);


            // 2.执行url对应方法
            MethodPair methodPair = methodMap.get(reqPath);
            Object result = methodPair.method.invoke(methodPair.impl, httpRequest, httpResponse);


            // 3.返回response


            System.out.println("请求类型："+head);
            System.out.println("解析请求地址："+host+reqPath);
            //处理请求输出信息
            if(!reqPath.equals("")){
                //默认访问返回响应头
                if(reqPath.equals("/")){
                    printWriter.println("HTTP/1.1 200 OK");
                    printWriter.println("Content-Type:text/html;charset=utf-8");
                    printWriter.println();
                    printWriter.println("<h2>欢迎成功访问网页</h2>");
                    printWriter.flush();
                }else {
                    //去除请求地址前的 /
                    reqPath = reqPath.substring(1);
                    String resourcePath = Data.resourcePath;

                    if(reqPath.contains("/")){//包含子目录

                    }else {//只在根目录

                    }
                }
            }

        }catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException(e);
        }finally {
            try {
                in.close();
                reader.close();
                bufferedReader.close();
                out.close();
                printWriter.close();
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
