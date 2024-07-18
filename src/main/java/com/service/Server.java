package main.java.com.service;

import main.java.com.data.Data;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @ClassName: Server
 * @Description: 监听服务
 * @author: liuchen
 * @date: 2024/7/15 21:16
 */

public class Server implements Runnable{

    //传入的端口号
    private int port;

    public Server(int port){
        this.port=port;
    }

    public void serverListener() throws IOException {
        ServerSocket serverSocket = null;

        try {
            serverSocket=new ServerSocket(port);
            System.out.println("开始监听请求");
            while (Data.isRun){
                Socket socket = serverSocket.accept();
                System.out.println("接收到请求了");

                RequestExecute requestExecute = new RequestExecute(socket);
                requestExecute.init();
                requestExecute.start();

            }




        }catch (Exception e ){
            e.printStackTrace();
            throw new RuntimeException("端口"+port+"监听服务器端口失败"+e.getMessage());
        }finally {
            serverSocket.close();
        }

    }

    @Override
    public void run() {
        try {

            serverListener();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        Server server = new Server(8088);

        new Thread(server).start();

    }


}
