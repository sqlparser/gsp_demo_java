
package demos.dlineageBasic.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

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
		ZipInputStream zis = null;
		BufferedReader reader = null;
		try {
			zis = new ZipInputStream(new FileInputStream(file));
			zis.getNextEntry();
			reader = new BufferedReader(new InputStreamReader(zis, "UTF-8"));
			return serializer.read(t, reader);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (zis != null) {
				try {
					zis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
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
		ZipOutputStream zos = null;
		try {
			zos = new ZipOutputStream(new FileOutputStream(file));
			zos.setLevel(2);
			zos.putNextEntry(new ZipEntry("data"));
			writer = new BufferedWriter(new OutputStreamWriter(zos, "UTF-8"));
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

			if (zos != null) {
				try {
					zos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}