
package demos.dlineage.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
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
	
	public static <T> T loadXML(Class<T> t, File file) {
		Serializer serializer = new Persister();
		try {
			return serializer.read(t, file);
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

	public static <T> void saveXML(T t, File file) {
		Serializer serializer = new Persister();
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"));
			serializer.write(t, writer);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}