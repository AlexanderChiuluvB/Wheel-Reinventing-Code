package protocol.socket;

import com.damon.RpcRequest;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;

public class SocketHandler implements Runnable {

    private Socket socket;

    public SocketHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream())) {
            {
                //从发送方发来的流反序列化为对象
                Object obj = objectInputStream.readObject();
                Object result = invoke((RpcRequest) obj);

                objectOutputStream.writeObject(result);
                objectOutputStream.flush();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Object invoke(RpcRequest o)  {

        try{
            Class implClass = Class.forName(o.getImpl());
            Method method = implClass.getMethod(o.getMethodName(), o.getParamTypes());
            String result = (String)method.invoke(implClass.newInstance(), o.getParams());

            return result;

        } catch (ClassNotFoundException | NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }
}
