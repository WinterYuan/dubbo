package org.apache.dubbo.common.serialize.jackson;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.dubbo.common.logger.Logger;
import org.apache.dubbo.common.logger.LoggerFactory;
import org.apache.dubbo.common.serialize.ObjectInput;
import org.apache.dubbo.common.utils.ReflectUtils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.TimeZone;

public class JacksonObjectInput implements ObjectInput {
    private static Logger logger = LoggerFactory.getLogger(JacksonObjectInput.class);

    private final ObjectMapper objectMapper;
    private final Map<String, String> data;
    private static final String KEY_PREFIX = "$";
    private int index = 0;

    @SuppressWarnings("unchecked")
    public JacksonObjectInput(InputStream inputstream) throws IOException {
        objectMapper = new ObjectMapper();
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.setTimeZone(TimeZone.getDefault());
        try {
            data = objectMapper.readValue(inputstream, Map.class);
        } catch (IOException e) {
            logger.error("parse input stream error.", e);
            throw e;
        }
    }

    @Override
    public boolean readBool() throws IOException {
        try {
            return readObject(Boolean.class);
        } catch (ClassNotFoundException e) {
            throw new IOException(e.getMessage());
        }
    }

    @Override
    public byte readByte() throws IOException {
        try {
            return readObject(Byte.class);
        } catch (ClassNotFoundException e) {
            throw new IOException(e.getMessage());
        }
    }

    @Override
    public short readShort() throws IOException {
        try {
            return readObject(Short.class);
        } catch (ClassNotFoundException e) {
            throw new IOException(e.getMessage());
        }
    }

    @Override
    public int readInt() throws IOException {
        try {
            return readObject(Integer.class);
        } catch (ClassNotFoundException e) {
            throw new IOException(e.getMessage());
        }
    }

    @Override
    public long readLong() throws IOException {
        try {
            return readObject(Long.class);
        } catch (ClassNotFoundException e) {
            throw new IOException(e.getMessage());
        }
    }

    @Override
    public float readFloat() throws IOException {
        try {
            return readObject(Float.class);
        } catch (ClassNotFoundException e) {
            throw new IOException(e.getMessage());
        }
    }

    @Override
    public double readDouble() throws IOException {
        try {
            return readObject(Double.class);
        } catch (ClassNotFoundException e) {
            throw new IOException(e.getMessage());
        }
    }

    @Override
    public String readUTF() throws IOException {
        try {
            return readObject(String.class);
        } catch (ClassNotFoundException e) {
            throw new IOException(e.getMessage());
        }
    }

    @Override
    public byte[] readBytes() throws IOException {
        return readUTF().getBytes();
    }

    @Override
    public Object readObject() throws IOException, ClassNotFoundException {
        try {
            return readObject(Object.class);
        } catch (ClassNotFoundException e) {
            throw new IOException(e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T readObject(Class<T> cls) throws IOException, ClassNotFoundException {
        String json = this.data.get(KEY_PREFIX + (++index));
        //read data type
        String dataType = this.data.get(KEY_PREFIX + index + "t");
        if (dataType != null) {
            Class clazz = ReflectUtils.desc2class(dataType);
            if (cls.isAssignableFrom(clazz)) {
                cls = clazz;
            } else {
                throw new IllegalArgumentException("Class \"" + clazz + "\" is not inherited from \"" + cls + "\"");
            }
        }
        return objectMapper.readValue(json, cls);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T readObject(Class<T> cls, Type type) throws IOException, ClassNotFoundException {
//        Object value = readObject();
        return readObject(cls);
    }
}
