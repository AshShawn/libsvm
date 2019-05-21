package tcpip;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Collections;
import java.util.Map;

import knn.KnnMain2;


public class TCPEchoServer {
    private static final int BLKSIZE = 1024;

    public static void main(String[] args) throws IOException, InterruptedException {
//        if(args.length!=1){
//            throw new IllegalArgumentException("Parameter(s):<Port>");
//        }
        String port = "200";
        int servPort = Integer.parseInt(port);

        //1.创建一个ServerSocket实例并制定本地端口。此套接字的功能是侦听该制定端口收到的连接。
        ServerSocket servSock = new ServerSocket(servPort);

        int recvMsgSize;

        byte[] receiveBuf = new byte[BLKSIZE];

        //2.重复执行
        while (true) {
            //a.调用ServerSocket的accept()方法以获取下一个客户端连接。
            //基于新建立的客户端连接，创建一个Socket实例，并由accept()方法返回
            Socket clntSock = servSock.accept();
            SocketAddress clientAddress = clntSock.getRemoteSocketAddress();
            System.out.println("Handling client at " + clientAddress);

            //b,使用所返回的Socket实例的InputStream和OutputStream与客户端进行通信
            InputStream in = clntSock.getInputStream();
            OutputStream out = clntSock.getOutputStream();


            while ((recvMsgSize = in.read(receiveBuf)) != -1) {
                System.out.println("收到消息:");
                System.out.println(new String(receiveBuf, 0, recvMsgSize));
                //解析数据
                Map<String,String> map = KnnMain2.readMsg(receiveBuf);
                //回复消息
                String result="";
                for (String key : map.keySet()) {
                    String res = map.get(key);
                    result+=key+"="+res+",";
                }
                String substring = result.substring(0, result.length() - 1);
                out.write(substring.getBytes(), 0, substring.length());
                System.out.println("返回值:"+substring);
            }

            //c，通信完成后，使用Socket的close()方法关闭该客户端套接字链接
            clntSock.close();
        }
    }
}