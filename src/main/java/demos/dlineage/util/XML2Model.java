
package demos.dlineage.util;

import java.io.StringWriter;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

public class XML2Model {

    public static <T> T loadXML(Class<T> t, String xml) {
        Serializer serializer = new Persister();
        try {
            return serializer.read(t, xml);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> String saveXML(T t) {
        Serializer serializer = new Persister();
        try {
            StringWriter writer = new StringWriter();
            serializer.write(t, writer);
            writer.close();
            return writer.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}