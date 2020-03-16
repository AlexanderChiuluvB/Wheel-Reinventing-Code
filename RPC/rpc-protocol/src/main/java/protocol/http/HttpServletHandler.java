package protocol.http;

import com.damon.RpcRequest;
import com.damon.RpcResponse;
import org.apache.commons.io.IOUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.lang.reflect.Method;

public class HttpServletHandler {

    public void handler(HttpServletRequest request, HttpServletResponse response) {

        try (InputStream inputStream = request.getInputStream();

             OutputStream outputStream = response.getOutputStream()) {

            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
            RpcRequest invocation = (RpcRequest) objectInputStream.readObject();

            // 从注册中心根据接口找到接口的实现类
            Class implClass = Class.forName(invocation.getImpl());

            Method method = implClass.getMethod(invocation.getMethodName(), invocation.getParamTypes());
            Object result = method.invoke(implClass.newInstance(), invocation.getParams());

            RpcResponse rpcResponse = new RpcResponse();
            rpcResponse.setData(result);
            rpcResponse.setResponseId(invocation.getRequestId());
            IOUtils.write(toByteArray(rpcResponse), outputStream);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public byte[] toByteArray(Object obj) {

        byte[] bytes = null;
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(obj);
            objectOutputStream.flush();

            bytes = byteArrayOutputStream.toByteArray();

            byteArrayOutputStream.close();
            objectOutputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return bytes;
    }


}
