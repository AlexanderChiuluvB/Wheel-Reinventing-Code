package protocol.http;

import com.damon.RpcRequest;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class SocketClient {
    //单例
    private volatile static SocketClient INSTANCE = null;

    private SocketClient() {

    }

    /**
     * 雙检锁单例模式
     */
    public static SocketClient getInstance() {
        if (INSTANCE == null) {
            synchronized (SocketClient.class) {
                if (INSTANCE == null) {
                    return new SocketClient();
                }
            }
        }
        return INSTANCE;
    }

    private Socket newSocket(String host, Integer port) {
        System.out.println("Creating a new socket");

        try {
            return new Socket(host, port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Object sendRequest(String host, Integer port, RpcRequest rpc) {
        Socket socket = newSocket(host, port);
        try (ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream())) {

            outputStream.writeObject(rpc);
            outputStream.flush();

            Object result = inputStream.readObject();

            inputStream.close();
            outputStream.close();
            return result;


        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
