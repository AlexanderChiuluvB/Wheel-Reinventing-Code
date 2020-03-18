package serializer;

import serializer.impl.DefaultJavaSerializer;
import serializer.impl.JsonSerializer;

import java.util.HashMap;
import java.util.Map;


public class SerializerFactory {

    private static final Map<SerializeType, Serializer> map = new HashMap<>();

    static {
        map.put(SerializeType.DefaultJavaSerializer, new DefaultJavaSerializer());
        map.put(SerializeType.JSONSerializer, new JsonSerializer());
    }

    public static <T> byte[] serialize(T obj, String Type) {
        SerializeType serializeType = SerializeType.queryByType(Type);
        if (serializeType == null) {
            serializeType = SerializeType.DefaultJavaSerializer;
        }
        Serializer serializer = map.get(serializeType);
        return serializer.serialize(obj);
    }

    public static <T> T deserialize(byte[] bytes, Class<T> clazz, String Type) {

        SerializeType serializeType = SerializeType.queryByType(Type);
        if (serializeType == null) {
            serializeType = SerializeType.DefaultJavaSerializer;
        }
        Serializer serializer = map.get(serializeType);
        return serializer.deserialize(bytes, clazz);

    }


}
