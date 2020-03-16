package protocol.http;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SocketServer {
    private static volatile SocketServer INSTANCE = null;

    private ExecutorService executorService = Executors.newCachedThreadPool();

    private SocketServer() {
    }

    public static SocketServer getInstance() {
        if (INSTANCE == null) {
            synchronized (SocketServer.class) {
                if (INSTANCE == null) {
                    return new SocketServer();
                }
            }
        }
        return null;

    }

    public void publish(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)){
            while(true){
                Socket socket = serverSocket.accept();
                executorService.submit(new SocketHandler(socket));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
