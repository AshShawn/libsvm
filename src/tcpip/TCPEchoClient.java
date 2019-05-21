package tcpip;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

public class TCPEchoClient {
    public static void main(String[] args) throws UnknownHostException, IOException, InterruptedException {
//        if(args.length<2||args.length>3){
//            throw new IllegalArgumentException("Parameter(s):<Server> <Word> [<Port>]");
//        }

        String server = "127.0.0.1";

        //要传的数据，也就是knnmain2的运行结果
        byte[] data = "13:0.29 79:0.35 80:0.29 120:0.22 132:0.37 133:0.33 161:0.26 163:0.29 164:0.29 165:0.36 171:0.3 172:0.27 173:0.25 192:0.44 193:0.37 194:0.39 280:0.41 281:0.22 282:0.39 283:0.66 284:0.65 285:0.63 289:0.41 291:0.42 295:0.38 296:0.36 297:0.37 319:0.54 320:0.53 321:0.51 325:0.49 326:0.52 327:0.5 414:0.24 446:0.34 467:0.38 489:0.3 523:0.3 555:0.34 556:0.24"
                .getBytes();

        int servPort = 200;

        //1.创建一个Socket实例：构造函数向指定的远程主机和端口建立一个TCP连接
        Socket socket = new Socket(server, servPort);
        System.out.println("Connected to server... sending echo string");

        /**
         *2. 通过套接字的输入输出流进行通信：一个Socket连接实例包括一个InputStream和一个OutputStream，它们的用法同于其他Java输入输出流。
         */
        InputStream in = socket.getInputStream();
        OutputStream out = socket.getOutputStream();

//        for (int i = 0; i < 10; i++) {
        out.write(data);
//            Thread.currentThread().sleep(1000);
//        }

        int totalBytesRcvd = 0;
        int bytesRcvd;

        while (totalBytesRcvd < data.length) {
            if ((bytesRcvd = in.read(data, totalBytesRcvd, data.length - totalBytesRcvd)) == -1) {
                throw new SocketException("Connection closed prematurely");
            }
            totalBytesRcvd += bytesRcvd;
        }
        System.out.println("Receved: " + new String(data));

        //3.使用Socet类的close（）方法关闭连接
        socket.close();
    }
}