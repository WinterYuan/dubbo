package org.apache.dubbo.common.serialize.jackson;

import org.apache.dubbo.common.serialize.model.Organization;
import org.apache.dubbo.common.serialize.model.Person;
import org.apache.dubbo.common.serialize.model.media.Image;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JacksonObjectInputOutputTest {
    private JacksonObjectOutput jacksonObjectOutput;
    private JacksonObjectInput jacksonObjectInput;
    private ByteArrayOutputStream byteArrayOutputStream;
    private ByteArrayInputStream byteArrayInputStream;

    @BeforeEach
    public void setUp() throws Exception {
        this.byteArrayOutputStream = new ByteArrayOutputStream();
        this.jacksonObjectOutput = new JacksonObjectOutput(byteArrayOutputStream);
    }

    @Test
    public void testWriteBool() throws IOException {
        this.jacksonObjectOutput.writeBool(true);
        this.flushToInput();

        assertThat(jacksonObjectInput.readBool(), is(true));
    }

    @Test
    public void testWriteShort() throws IOException {
        this.jacksonObjectOutput.writeShort((short) 2);
        this.flushToInput();

        assertThat(jacksonObjectInput.readShort(), is((short) 2));
    }

    @Test
    public void testWriteInt() throws IOException {
        this.jacksonObjectOutput.writeInt(1);
        this.flushToInput();

        assertThat(jacksonObjectInput.readInt(), is(1));
    }

    @Test
    public void testWriteLong() throws IOException {
        this.jacksonObjectOutput.writeLong(1000L);
        this.flushToInput();

        assertThat(jacksonObjectInput.readLong(), is(1000L));
    }

    @Test
    public void testWriteUTF() throws IOException {
        this.jacksonObjectOutput.writeUTF("Pace Hasîtî 和平 Мир");
        this.flushToInput();

        assertThat(jacksonObjectInput.readUTF(), is("Pace Hasîtî 和平 Мир"));
    }


    @Test
    public void testWriteFloat() throws IOException {
        this.jacksonObjectOutput.writeFloat(1.88f);
        this.flushToInput();

        assertThat(this.jacksonObjectInput.readFloat(), is(1.88f));
    }

    @Test
    public void testWriteDouble() throws IOException {
        this.jacksonObjectOutput.writeDouble(1.66d);
        this.flushToInput();

        assertThat(this.jacksonObjectInput.readDouble(), is(1.66d));
    }

    @Test
    public void testWriteBytes() throws IOException {
        this.jacksonObjectOutput.writeBytes("hello".getBytes());
        this.flushToInput();

        assertThat(this.jacksonObjectInput.readBytes(), is("hello".getBytes()));
    }

    @Test
    public void testWriteBytesWithSubLength() throws IOException {
        this.jacksonObjectOutput.writeBytes("hello".getBytes(), 2, 2);
        this.flushToInput();

        assertThat(this.jacksonObjectInput.readBytes(), is("ll".getBytes()));
    }

    @Test
    public void testWriteByte() throws IOException {
        this.jacksonObjectOutput.writeByte((byte) 123);
        this.flushToInput();

        assertThat(this.jacksonObjectInput.readByte(), is((byte) 123));
    }

    @Test
    public void testWriteObject() throws IOException, ClassNotFoundException {
        Image image = new Image("http://dubbo.io/logo.png", "logo", 300, 480, Image.Size.SMALL);
        this.jacksonObjectOutput.writeObject(image);
        this.flushToInput();

        Image readObjectForImage = jacksonObjectInput.readObject(Image.class);
        assertThat(readObjectForImage, not(nullValue()));
        assertThat(readObjectForImage, is(image));

        assertThat(readObjectForImage.getUri(), Is.is("http://dubbo.io/logo.png"));
        assertThat(readObjectForImage.getWidth(), Is.is(300));
    }

    @Test
    public void testReadObjectWithoutClass() throws IOException, ClassNotFoundException {
        Image image = new Image("http://dubbo.io/logo.png", "logo", 300, 480, Image.Size.SMALL);
        this.jacksonObjectOutput.writeObject(image);
        this.flushToInput();

        Image readObject = (Image) jacksonObjectInput.readObject();

        assertThat(readObject, not(nullValue()));
        assertThat(readObject, is(image));

        assertThat(readObject.getUri(), Is.is("http://dubbo.io/logo.png"));
        assertThat(readObject.getWidth(), Is.is(300));
    }


    @Test
    public void testReadObjectWithTowType() throws Exception {
        Person person1 = new Person();
        person1.setName("John");
        person1.setAge(30);
        Person person2 = new Person();
        person2.setName("Born");
        person2.setAge(24);
        List<Person> personList = Arrays.asList(person1, person2);
        this.jacksonObjectOutput.writeObject(personList);
        this.flushToInput();

        Method methodReturnType = getClass().getMethod("towLayer");
        Type type = methodReturnType.getGenericReturnType();
        List<Person> o = jacksonObjectInput.readObject(List.class, type);

        assertTrue(o instanceof List);
        // todo not pass
        assertTrue(o.get(0) instanceof Person);

        assertThat(o.size(), Is.is(2));
        assertThat(o.get(1).getName(), Is.is("John"));
    }

    public List<Person> towLayer() {
        return null;
    }

    @Test
    public void testReadObjectWithThreeType() throws Exception {
        Person person1 = new Person();
        person1.setName("John");
        person1.setAge(30);
        Person person2 = new Person();
        person2.setName("Born");
        person2.setAge(24);
        List<Person> personList = Arrays.asList(person1, person2);
        Organization organization = new Organization();
        organization.setData(personList);
        this.jacksonObjectOutput.writeObject(organization);
        this.flushToInput();

        Method methodReturnType = getClass().getMethod("threeLayer");
        Type type = methodReturnType.getGenericReturnType();
        Organization<List<Person>> o = jacksonObjectInput.readObject(Organization.class, type);

        assertTrue(o instanceof Organization);
        assertTrue(o.getData() instanceof List);
        // todo not pass
        assertTrue(o.getData().get(0) instanceof Person);

        assertThat(o.getData().size(), Is.is(2));
        assertThat(o.getData().get(1).getName(), Is.is("Born"));
    }

    public Organization<List<Person>> threeLayer() {
        return null;
    }

    private void flushToInput() throws IOException {
        this.jacksonObjectOutput.flushBuffer();
        this.byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        this.jacksonObjectInput = new JacksonObjectInput(byteArrayInputStream);
    }
}
