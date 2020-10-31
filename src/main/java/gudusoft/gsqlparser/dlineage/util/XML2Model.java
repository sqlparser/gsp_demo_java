
package gudusoft.gsqlparser.dlineage.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

@SuppressWarnings("unchecked")
public class XML2Model {

	public static <T> T loadXML(Class<T> t, String xml) {
		StringReader reader = null;
		try {
			JAXBContext context = JAXBContext.newInstance(t.getClass());
			Unmarshaller unmarshaller = context.createUnmarshaller();
			reader = new StringReader(xml);
			return (T) unmarshaller.unmarshal(reader);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}

	public static <T> T loadXML(Class<T> t, File file) {
		ZipInputStream zis = null;
		BufferedReader reader = null;
		try {
			JAXBContext context = JAXBContext.newInstance(t.getClass());
			Unmarshaller unmarshaller = context.createUnmarshaller();
			zis = new ZipInputStream(new FileInputStream(file));
			zis.getNextEntry();
			reader = new BufferedReader(new InputStreamReader(zis, "UTF-8"));
			return (T) unmarshaller.unmarshal(reader);
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
		try {
			JAXBContext context = JAXBContext.newInstance(t.getClass());
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
			StringWriter writer = new StringWriter();
			marshaller.marshal(t, writer);
			writer.close();
			return writer.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static <T> void saveXML(T t, File file) {
		BufferedWriter writer = null;
		ZipOutputStream zos = null;
		try {
			JAXBContext context = JAXBContext.newInstance(t.getClass());
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
			zos = new ZipOutputStream(new FileOutputStream(file));
			zos.setLevel(2);
			zos.putNextEntry(new ZipEntry("data"));
			writer = new BufferedWriter(new OutputStreamWriter(zos, "UTF-8"));
			marshaller.marshal(t, writer);
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