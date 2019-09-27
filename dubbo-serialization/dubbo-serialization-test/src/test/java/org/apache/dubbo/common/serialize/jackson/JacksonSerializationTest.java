package org.apache.dubbo.common.serialize.jackson;

import org.apache.dubbo.common.serialize.ObjectInput;
import org.apache.dubbo.common.serialize.ObjectOutput;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;

public class JacksonSerializationTest {
    private JacksonSerialization jacksonSerialization;

    @BeforeEach
    public void setUp() {
        this.jacksonSerialization = new JacksonSerialization();
    }

    @Test
    public void testContentType() {
        assertThat(jacksonSerialization.getContentType(), is("text/json"));
    }

    @Test
    public void testContentTypeId() {
        assertThat(jacksonSerialization.getContentTypeId(), is((byte) 17));
    }

    @Test
    public void testObjectOutput() throws IOException {
        ObjectOutput objectOutput = jacksonSerialization.serialize(null, mock(OutputStream.class));
        assertThat(objectOutput, Matchers.<ObjectOutput>instanceOf(JacksonObjectOutput.class));
    }

    @Test
    // todo not pass
    public void testObjectInput() throws IOException {
        ObjectInput objectInput = jacksonSerialization.deserialize(null, mock(InputStream.class));
        assertThat(objectInput, Matchers.<ObjectInput>instanceOf(JacksonObjectInput.class));
    }
}
