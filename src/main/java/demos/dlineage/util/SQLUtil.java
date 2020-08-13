
package demos.dlineage.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.PushbackInputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import demos.dlineage.dataflow.model.ModelBindingManager;
import demos.dlineage.dataflow.model.Table;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.nodes.TTable;

public class SQLUtil {

	public static final String TABLE_CONSTANT = "CONSTANT";

	public static boolean isEmpty(String value) {
		return value == null || value.trim().length() == 0;
	}

	public static String getFileContent(File file) {
		String charset = null;
		String sqlfilename = file.getAbsolutePath();
		int read = 0;
		try {
			FileInputStream fr = new FileInputStream(sqlfilename);
			byte[] bom = new byte[4];
			fr.read(bom, 0, bom.length);

			if ((bom[0] == (byte) 0x00) && (bom[1] == (byte) 0x00) && (bom[2] == (byte) 0xFE)
					&& (bom[3] == (byte) 0xFF)) {
				charset = "UTF-32BE";
				read = 4;
			} else if ((bom[0] == (byte) 0xFF) && (bom[1] == (byte) 0xFE) && (bom[2] == (byte) 0x00)
					&& (bom[3] == (byte) 0x00)) {
				charset = "UTF-32LE";
				read = 4;
			} else if ((bom[0] == (byte) 0xEF) && (bom[1] == (byte) 0xBB) && (bom[2] == (byte) 0xBF)) {
				charset = "UTF-8";
				read = 3;
			} else if ((bom[0] == (byte) 0xFE) && (bom[1] == (byte) 0xFF)) {
				charset = "UTF-16BE";
				read = 2;
			} else if ((bom[0] == (byte) 0xFF) && (bom[1] == (byte) 0xFE)) {
				charset = "UTF-16LE";
				read = 2;
			} else {
				charset = Charset.defaultCharset().name();
				read = 0;
			}

			fr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		Long filelength = file.length();
		byte[] filecontent = new byte[filelength.intValue()];
		try {
			InputStream in = new BufferedInputStream(new FileInputStream(sqlfilename));
			in.read(filecontent);
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		byte[] content = new byte[filelength.intValue() - read];
		System.arraycopy(filecontent, read, content, 0, content.length);

		try {
			String fileContent = new String(content, charset == null ? Charset.defaultCharset().name() : charset)
					.trim();
			return fileContent;
		} catch (UnsupportedEncodingException e) {
			System.err
					.println("The OS does not support " + charset == null ? Charset.defaultCharset().name() : charset);
			return null;
		}
	}

	public static String getInputStreamContent(InputStream is, boolean close) {
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream(4096);
			byte[] tmp = new byte[4096];
			while (true) {
				int r = is.read(tmp);
				if (r == -1)
					break;
				out.write(tmp, 0, r);
			}
			byte[] bytes = out.toByteArray();
			if (close) {
				is.close();
			}
			out.close();
			String content = new String(bytes);
			return content.trim();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getFileContent(String filePath) {
		if (filePath == null)
			return "";
		File file = new File(filePath);
		if (!file.exists() || file.isDirectory())
			return "";
		return getFileContent(file);
	}

	public static String trimColumnStringQuote(String string) {
		if (string == null)
			return string;

		if (string.indexOf('.') != -1 && string.length() < 128) {
			List<String> splits = parseNames(string);
			StringBuilder buffer = new StringBuilder();
			for (int i = 0; i < splits.size(); i++) {
				buffer.append(splits.get(i));
				if (i < splits.size() - 1) {
					buffer.append(".");
				}
			}
			string = buffer.toString();
		} else {
			if (string.startsWith("'") && string.endsWith("'"))
				return string.substring(1, string.length() - 1);
		}
		return string;
	}

	public static List<String> parseNames(String nameString) {
		String name = nameString.trim();
		List<String> names = new ArrayList<String>();
		String[] splits = nameString.split("\\.");
		if ((name.startsWith("'") && name.endsWith("'"))) {
			for (int i = 0; i < splits.length; i++) {
				String split = splits[i].trim();
				if (split.startsWith("'") && !split.endsWith("'")) {
					StringBuilder buffer = new StringBuilder();
					buffer.append(splits[i]);
					while (!(split = splits[++i].trim()).endsWith("'")) {
						buffer.append(".");
						buffer.append(splits[i]);
					}

					buffer.append(".");
					buffer.append(splits[i]);

					names.add(buffer.toString());
					continue;
				}
				names.add(splits[i]);
			}
		} else {
			names.addAll(Arrays.asList(splits));
		}
		return names;
	}

	public static void writeToFile(File file, InputStream source, boolean close) {
		BufferedInputStream bis = null;
		BufferedOutputStream fouts = null;
		try {
			bis = new BufferedInputStream(source);
			if (!file.exists()) {
				if (!file.getParentFile().exists()) {
					file.getParentFile().mkdirs();
				}
				file.createNewFile();
			}
			fouts = new BufferedOutputStream(new FileOutputStream(file));
			byte b[] = new byte[1024];
			int i = 0;
			while ((i = bis.read(b)) != -1) {
				fouts.write(b, 0, i);
			}
			fouts.flush();
			fouts.close();
			if (close)
				bis.close();
		} catch (IOException e) {
			Logger.getLogger(SQLUtil.class.getName()).log(Level.WARNING, "Write file failed.", //$NON-NLS-1$
					e);
			try {
				if (fouts != null)
					fouts.close();
			} catch (IOException f) {
				Logger.getLogger(SQLUtil.class.getName()).log(Level.WARNING, "Close output stream failed.", f); //$NON-NLS-1$
			}
			if (close) {
				try {
					if (bis != null)
						bis.close();
				} catch (IOException f) {
					Logger.getLogger(SQLUtil.class.getName()).log(Level.WARNING, "Close input stream failed.", //$NON-NLS-1$
							f);
				}
			}
		}
	}

	public static void writeToFile(File file, String string) throws IOException {

		if (!file.exists()) {
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			file.createNewFile();
		}
		PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file)));
		if (string != null)
			out.print(string);
		out.close();
	}

	public static void appendToFile(File file, String string) throws IOException {

		if (!file.exists()) {
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			file.createNewFile();
		}
		PrintWriter out = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file, true)));
		if (string != null)
			out.println(string);
		out.close();
	}

	public static void deltree(File root) {
		if (root == null || !root.exists()) {
			return;
		}

		if (root.isFile()) {
			root.delete();
			return;
		}

		File[] children = root.listFiles();
		if (children != null) {
			for (int i = 0; i < children.length; i++) {
				deltree(children[i]);
			}
		}

		root.delete();
	}

	public synchronized static String generateVirtualTableName(TCustomSqlStatement stmt) {
		if (ModelBindingManager.get().virtualTableNames.containsKey(stmt.toString()))
			return ModelBindingManager.get().virtualTableNames.get(stmt.toString());
		else {
			String tableName = null;
			ModelBindingManager.get().virtualTableIndex++;
			if (ModelBindingManager.get().virtualTableIndex == 0) {
				tableName = "RESULT SET COLUMNS";
			} else {
				tableName = "RESULT SET COLUMNS " + ModelBindingManager.get().virtualTableIndex;
			}
			ModelBindingManager.get().virtualTableNames.put(stmt.toString(), tableName);
			return tableName;
		}
	}

	public synchronized static void resetVirtualTableNames() {
		ModelBindingManager.get().virtualTableIndex = -1;
		ModelBindingManager.get().virtualTableNames.clear();
	}

	/**
	 * @param file
	 *            the filePath
	 * @param enc
	 *            the default encoding
	 * @return the UTFFileHandler.UnicodeInputStream
	 * @throws Exception
	 */
	public static InputStream getInputStreamWithoutBom(String file) throws IOException {
		UnicodeInputStream stream = null;
		FileInputStream fis = new FileInputStream(file);
		stream = new UnicodeInputStream(fis, null);
		return stream;
	}

	/**
	 * This inputstream will recognize unicode BOM marks and will skip bytes if
	 * getEncoding() method is called before any of the read(...) methods.
	 * <p>
	 * Usage pattern: String enc = "ISO-8859-1"; // or NULL to use systemdefault
	 * FileInputStream fis = new FileInputStream(file); UnicodeInputStream uin =
	 * new UnicodeInputStream(fis, enc); enc = uin.getEncoding(); // check and
	 * skip possible BOM bytes InputStreamReader in; if (enc == null) in = new
	 * InputStreamReader(uin); else in = new InputStreamReader(uin, enc);
	 */
	public static class UnicodeInputStream extends InputStream {
		PushbackInputStream internalIn;
		boolean isInited = false;
		String defaultEnc;
		String encoding;

		private static final int BOM_SIZE = 4;

		public UnicodeInputStream(InputStream in, String defaultEnc) {
			internalIn = new PushbackInputStream(in, BOM_SIZE);
			this.defaultEnc = defaultEnc;
		}

		public String getDefaultEncoding() {
			return defaultEnc;
		}

		public String getEncoding() {
			if (!isInited) {
				try {
					init();
				} catch (IOException ex) {
					IllegalStateException ise = new IllegalStateException("Init method failed.");
					ise.initCause(ise);
					throw ise;
				}
			}
			return encoding;
		}

		/**
		 * Read-ahead four bytes and check for BOM marks. Extra bytes are unread
		 * back to the stream, only BOM bytes are skipped.
		 */
		protected void init() throws IOException {
			if (isInited)
				return;

			byte bom[] = new byte[BOM_SIZE];
			int n, unread;
			n = internalIn.read(bom, 0, bom.length);

			if ((bom[0] == (byte) 0x00) && (bom[1] == (byte) 0x00) && (bom[2] == (byte) 0xFE)
					&& (bom[3] == (byte) 0xFF)) {
				encoding = "UTF-32BE";
				unread = n - 4;
			} else if ((bom[0] == (byte) 0xFF) && (bom[1] == (byte) 0xFE) && (bom[2] == (byte) 0x00)
					&& (bom[3] == (byte) 0x00)) {
				encoding = "UTF-32LE";
				unread = n - 4;
			} else if ((bom[0] == (byte) 0xEF) && (bom[1] == (byte) 0xBB) && (bom[2] == (byte) 0xBF)) {
				encoding = "UTF-8";
				unread = n - 3;
			} else if ((bom[0] == (byte) 0xFE) && (bom[1] == (byte) 0xFF)) {
				encoding = "UTF-16BE";
				unread = n - 2;
			} else if ((bom[0] == (byte) 0xFF) && (bom[1] == (byte) 0xFE)) {
				encoding = "UTF-16LE";
				unread = n - 2;
			} else {
				encoding = defaultEnc;
				unread = n;
			}

			if (unread > 0)
				internalIn.unread(bom, (n - unread), unread);

			isInited = true;
		}

		public void close() throws IOException {
			internalIn.close();
		}

		public int read() throws IOException {
			return internalIn.read();
		}
	}

	public static boolean compareIdentifier(String source, String target) {
		boolean ret = false;
		String normalTarget, normalSource;

		switch (ModelBindingManager.getGlobalVendor()) {
		case dbvbigquery:
		case dbvcouchbase:
		case dbvhive:
		case dbvimpala:
			if (target.indexOf('`') >= 0) {
				normalTarget = target.replaceAll("[`]", "");
			} else {
				normalTarget = target;
			}

			if (source.indexOf('`') >= 0) {
				normalSource = source.replaceAll("[`]", "");
			} else {
				normalSource = source;
			}

			ret = normalTarget.equalsIgnoreCase(normalSource);
			break;
		case dbvmysql:
			if (target.indexOf('`') >= 0) {
				normalTarget = target.replaceAll("[`]", "");
			} else {
				normalTarget = target;
			}

			if (source.indexOf('`') >= 0) {
				normalSource = source.replaceAll("[`]", "");
			} else {
				normalSource = source;
			}

			ret = normalTarget.equalsIgnoreCase(normalSource);
			break;
		case dbvdax:
			if (target.indexOf('\'') >= 0) {
				normalTarget = target.replaceAll("[']", "");
			} else {
				normalTarget = target;
			}

			if (source.indexOf('\'') >= 0) {
				normalSource = source.replaceAll("[']", "");
			} else {
				normalSource = source;
			}

			ret = normalTarget.equalsIgnoreCase(normalSource);
			break;
		case dbvdb2:
		case dbvhana:
		case dbvinformix:
		case dbvnetezza:
		case dbvoracle:
		case dbvredshift:
		case dbvsnowflake:
		case dbvsybase:
		case dbvteradata:
		case dbvvertica:
			if (target.indexOf('"') >= 0) {
				normalTarget = target.replaceAll("\"", "");
			} else {
				normalTarget = target.toUpperCase();
			}

			if (source.indexOf('"') >= 0) {
				normalSource = source.replaceAll("\"", "");
			} else {
				normalSource = source.toUpperCase();
			}
			ret = normalTarget.compareTo(normalSource) == 0;

			break;
		case dbvpostgresql:
			if (target.indexOf('"') >= 0) {
				normalTarget = target.replaceAll("\"", "");
			} else {
				normalTarget = target.toLowerCase();
			}

			if (source.indexOf('"') >= 0) {
				normalSource = source.replaceAll("\"", "");
			} else {
				normalSource = source.toLowerCase();
			}
			ret = normalTarget.compareTo(normalSource) == 0;

			break;
		case dbvmssql:
			if (target.indexOf('"') >= 0) {
				normalTarget = target.replaceAll("\"", "");
			} else if (target.indexOf("[") >= 0) {
				normalTarget = target.replaceAll("\\[", "").replaceAll("]", "");
			} else {
				normalTarget = target;
			}

			if (source.indexOf('"') >= 0) {
				normalSource = source.replaceAll("\"", "");
			} else if (source.indexOf("[") >= 0) {
				normalSource = source.replaceAll("\\[", "").replaceAll("]", "");
			} else {
				normalSource = source;
			}
			
			if (target.indexOf('\'') >= 0) {
				normalTarget = target.replaceAll("[']", "");
			} else {
				normalTarget = target;
			}

			if (source.indexOf('\'') >= 0) {
				normalSource = source.replaceAll("[']", "");
			} else {
				normalSource = source;
			}

			// depends on the sql server case sensitive setting, need to add an
			// option to control the comparision
			ret = normalTarget.equalsIgnoreCase(normalSource);

			break;
		default:
			if (target.indexOf('"') >= 0) {
				normalTarget = target.replaceAll("\"", "");
			} else {
				normalTarget = target.toUpperCase();
			}

			if (source.indexOf('"') >= 0) {
				normalSource = source.replaceAll("\"", "");
			} else {
				normalSource = source.toUpperCase();
			}
			ret = normalTarget.compareTo(normalSource) == 0;

			break;
		}

		return ret;
	}

	public static String getIdentifierNormalName(EDbVendor vendor, String name) {
		String normalName = null;
		switch (vendor) {
		case dbvbigquery:
		case dbvcouchbase:
		case dbvhive:
		case dbvimpala:
			if (name.indexOf('`') >= 0) {
				normalName = name.replaceAll("[`]", "");
			} else {
				normalName = name;
			}
			return normalName.toUpperCase();

		case dbvmysql:
			if (name.indexOf('`') >= 0) {
				normalName = name.replaceAll("[`]", "");
			} else {
				normalName = name;
			}

			return normalName.toUpperCase();
		case dbvdax:
			if (name.indexOf('\'') >= 0) {
				normalName = name.replaceAll("[']", "");
			} else {
				normalName = name;
			}

			return normalName.toUpperCase();
		case dbvdb2:
		case dbvhana:
		case dbvinformix:
		case dbvnetezza:
		case dbvoracle:
		case dbvredshift:
		case dbvsnowflake:
		case dbvsybase:
		case dbvteradata:
		case dbvvertica:
			if (name.indexOf('"') >= 0) {
				normalName = name.replaceAll("\"", "");
			} else {
				normalName = name.toUpperCase();
			}
			return normalName;
		case dbvpostgresql:
			if (name.indexOf('"') >= 0) {
				normalName = name.replaceAll("\"", "");
			} else {
				normalName = name.toLowerCase();
			}
			return normalName;
		case dbvmssql:
			normalName = name;
			if (normalName.indexOf('\'') >= 0) {
				normalName = normalName.replaceAll("[']", "");
			} 
			if (normalName.indexOf('"') >= 0) {
				normalName = normalName.replaceAll("\"", "");
			}
			if (normalName.indexOf("[") >= 0) {
				normalName = normalName.replaceAll("\\[", "").replaceAll("]", "");
			}
			return normalName.toUpperCase();
		default:
			if (name.indexOf('"') >= 0) {
				normalName = name.replaceAll("\"", "");
			} else {
				normalName = name.toUpperCase();
			}

			return normalName;
		}
	}
	
	public static String getIdentifierNormalName(String name) {
		return getIdentifierNormalName(ModelBindingManager.getGlobalVendor(), name);
	}

	public static boolean isTempTable(Table tableModel, EDbVendor vendor) {
		switch (vendor) {
		case dbvmssql:
			return tableModel.getName().startsWith("#");
		default:
			return false;
		}
	}
	
	public static boolean isTempTable(TTable table, EDbVendor vendor) {
		switch (vendor) {
		case dbvmssql:
			return table.getName().startsWith("#");
		default:
			return false;
		}
	}

	public static String getTableFullName(String tableName) {
		if (tableName.split("\\.").length == 1) {
			StringBuffer buffer = new StringBuffer();
			if (ModelBindingManager.getGlobalDatabase() != null) {
				buffer.append(ModelBindingManager.getGlobalDatabase() ).append(".");
			}
			if (ModelBindingManager.getGlobalSchema() != null) {
				buffer.append(ModelBindingManager.getGlobalSchema() ).append(".");
			}
			buffer.append(tableName);
			return SQLUtil.getIdentifierNormalName(buffer.toString());		
		} else if (tableName.split("\\.").length == 2) {
			if (ModelBindingManager.getGlobalDatabase() != null) {
				return SQLUtil.getIdentifierNormalName(ModelBindingManager.getGlobalDatabase() + "." + tableName);
			}
			else{
				return SQLUtil.getIdentifierNormalName(tableName);
			}
		} else {
			return SQLUtil.getIdentifierNormalName(tableName);
		}
	}
	
	public static String[] convertSQL(String json) {
        List<String> sqls = new ArrayList<String>();
        if (json != null && json.trim().startsWith("{")) {
            StringBuilder buffer = new StringBuilder();
            JSONObject queryObject = JSONObject.parseObject(json);
            JSONArray querys = queryObject.getJSONArray("queries");
            if (querys != null) {
                for (int i = 0; i < querys.size(); i++) {
                    JSONObject object = querys.getJSONObject(i);
                    sqls.add(object.toJSONString());
                }
            } else {
                sqls.add(queryObject.toJSONString());
            }
        } else {
            sqls.add(json == null ? "" : json);
        }
        return sqls.toArray(new String[0]);
    }
}
