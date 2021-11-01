package demos.sqlenv.util;

import gudusoft.gsqlparser.util.SQLUtil;

import java.io.*;

public class SQLFileUtil {

	private static String sqlPath = "gudusoft/gsqlparser/sqlenv/%s/%s/default-cmd.sql";

	public static String readSql(String dbType, String readType) {
		InputStream inputStream = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream(String.format(sqlPath, dbType, readType));
		BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
		String data;
		StringBuilder execSql = new StringBuilder();
		try {
			while ((data = br.readLine()) != null) {
				execSql.append(data).append("\n");
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				br.close();
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return execSql.toString();
	}

	private static String externalSqlDir = "conf/";

	public static String readSqlContent(String dbType, String sqlFileName) {
		try {
			File sqlFile = new File(externalSqlDir + dbType + "/" + sqlFileName);
			if (sqlFile.exists()) {
				return SQLUtil.getFileContent(sqlFile);
			} else {
				InputStream inputStream = Thread.currentThread().getContextClassLoader()
						.getResourceAsStream("gudusoft/gsqlparser/sqlenv/" + dbType + "/" + sqlFileName);
				return SQLUtil.getInputStreamContent(inputStream, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
