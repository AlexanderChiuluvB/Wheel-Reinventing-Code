package protocol.http;

import com.damon.RpcRequest;
import com.damon.RpcResponse;
import org.apache.commons.io.IOUtils;


import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class httpClient {

    private static volatile httpClient INSTANCE = null;

    private httpClient() {

    }

    public static httpClient getInstance() {
        if (INSTANCE == null) {
            synchronized (httpClient.class) {
                if (INSTANCE == null) {
                    INSTANCE = new httpClient();
                    return INSTANCE;
                }
            }
        }
        return INSTANCE;
    }

    public Object post(String hostname, Integer port, RpcRequest invocation) {

        try{
            URL url = new URL("http", hostname, port, "/");
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

            httpURLConnection.setRequestMethod("post");
            httpURLConnection.setDoOutput(true);

            OutputStream outputStream = httpURLConnection.getOutputStream();
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            //传参数
            objectOutputStream.writeObject(invocation);
            objectOutputStream.flush();
            objectOutputStream.close();

            InputStream inputStream = httpURLConnection.getInputStream();
            RpcResponse rpcResponse = (RpcResponse) toObject(IOUtils.toByteArray(inputStream));

            return rpcResponse.getData();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Object toObject(byte[] bytes) {
        Object obj = null;
        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);

            obj = objectInputStream.readObject();
            objectInputStream.close();
            byteArrayInputStream.close();

        }
         catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return obj;

    }


}
