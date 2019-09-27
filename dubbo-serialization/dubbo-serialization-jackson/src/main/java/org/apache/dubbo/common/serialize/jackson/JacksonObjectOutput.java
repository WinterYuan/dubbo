package org.apache.dubbo.common.serialize.jackson;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.dubbo.common.serialize.ObjectOutput;
import org.apache.dubbo.common.utils.ReflectUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class JacksonObjectOutput implements ObjectOutput {
    private final ObjectMapper objectMapper;
    private final Map<String, Object> data;
    private final static String KEY_PREFIX = "$";
    private int index = 0;

    private final PrintWriter writer;

    public JacksonObjectOutput(OutputStream out) {
        this(new OutputStreamWriter(out));
    }

    public JacksonObjectOutput(Writer writer) {
        objectMapper = new ObjectMapper();
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.setTimeZone(TimeZone.getDefault());
        this.writer = new PrintWriter(writer);
        this.data = new HashMap<>();
    }

    @Override
    public void writeBool(boolean v) throws IOException {
        writeObject0(v);
    }

    @Override
    public void writeByte(byte v) throws IOException {
        writeObject0(v);
    }

    @Override
    public void writeShort(short v) throws IOException {
        writeObject0(v);
    }

    @Override
    public void writeInt(int v) throws IOException {
        writeObject0(v);
    }

    @Override
    public void writeLong(long v) throws IOException {
        writeObject0(v);
    }

    @Override
    public void writeFloat(float v) throws IOException {
        writeObject0(v);
    }

    @Override
    public void writeDouble(double v) throws IOException {
        writeObject0(v);
    }

    @Override
    public void writeUTF(String v) throws IOException {
        writeObject0(v);
    }

    @Override
    public void writeBytes(byte[] b) throws IOException {
        writeObject0(new String(b));
    }

    @Override
    public void writeBytes(byte[] b, int off, int len) throws IOException {
        writeObject0(new String(b, off, len));
    }

    @Override
    public void writeObject(Object obj) throws IOException {
//        int i = ++index;
        if (obj == null) {
            writeObject0(obj);
            return;
        }
        //write data value
        writeObject0(obj);
        //write data type
        Class c = obj.getClass();
        String desc = ReflectUtils.getDesc(c);
        data.put(KEY_PREFIX + (index) + "t", desc);
//        if (obj instanceof Collection) {
//            //集合类型
//        } else if (obj instanceof Map) {
//            //
//        } else {
//        }
    }

    private void writeObject0(Object obj) throws IOException {
        data.put(KEY_PREFIX + (++index), objectMapper.writeValueAsString(obj));
    }

    @Override
    public void flushBuffer() throws IOException {
        objectMapper.writeValue(writer, data);
        writer.println();
        writer.flush();
    }
}
