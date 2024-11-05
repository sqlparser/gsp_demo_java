package demos.snowflake.sqlextract;

import java.io.File;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.nodes.TParameterDeclaration;
import gudusoft.gsqlparser.stmt.TCallStatement;
import gudusoft.gsqlparser.stmt.TCreateProcedureStmt;

public class SnowflakeSQLExtractor {

	public static void main(String args[]) {

		if (args.length < 1) {
			System.out.println(
					"Usage: java SnowflakeSQLExtractor [/f <path_to_sql_file>] [/d <path_to_directory_includes_sql_files>]");
			System.out.println("/f: Option, specify the sql file path to analyze fdd relation.");
			System.out.println("/d: Option, specify the sql directory path to analyze fdd relation.");
			return;
		}

		List<String> argList = Arrays.asList(args);

		File sqlFiles = null;
		if (argList.indexOf("/f") != -1 && argList.size() > argList.indexOf("/f") + 1) {
			sqlFiles = new File(args[argList.indexOf("/f") + 1]);
			if (!sqlFiles.exists() || !sqlFiles.isFile()) {
				System.out.println(sqlFiles + " is not a valid file.");
				return;
			}
		} else if (argList.indexOf("/d") != -1 && argList.size() > argList.indexOf("/d") + 1) {
			sqlFiles = new File(args[argList.indexOf("/d") + 1]);
			if (!sqlFiles.exists() || !sqlFiles.isDirectory()) {
				System.out.println(sqlFiles + " is not a valid directory.");
				return;
			}
		} else {
			System.out.println("Please specify a sql file path or directory path to extract sql.");
			return;
		}

		File[] files = sqlFiles.isDirectory() ? sqlFiles.listFiles() : new File[] { sqlFiles };
		for (File file : files) {
			TGSqlParser sqlparser = new TGSqlParser(EDbVendor.dbvsnowflake);
			sqlparser.sqlfilename = file.getAbsolutePath();
			int result = sqlparser.parse();
			if (result != 0) {
				System.err.println(sqlparser.getErrormessage());
			} else {
				for (int i = 0; i < sqlparser.getSqlstatements().size(); i++) {
					TCustomSqlStatement stmt = sqlparser.getSqlstatements().get(i);
					analyzeCustomSqlStmt(stmt);
				}
			}
		}
	}

	private static void analyzeCustomSqlStmt(TCustomSqlStatement stmt) {
		if (stmt instanceof TCallStatement) {

		} else if (stmt instanceof TCreateProcedureStmt) {
			TCreateProcedureStmt procedure = (TCreateProcedureStmt) stmt;
			if (procedure.getRoutineBodyInConstant() != null) {
				extractSQLFromProcedure(procedure);
			}
		} else if (stmt.getStatements() != null && stmt.getStatements().size() > 0) {
			for (int i = 0; i < stmt.getStatements().size(); i++) {
				analyzeCustomSqlStmt(stmt.getStatements().get(i));
			}
		}
	}

	public static void printSQL(String sql) {
		System.out.println(sql);
	}

	private static void extractSQLFromProcedure(TCreateProcedureStmt procedure) {
		Map<String, String> argMap = new LinkedHashMap<String, String>();
		if (procedure.getParameterDeclarations() != null) {
			for (int i = 0; i < procedure.getParameterDeclarations().size(); i++) {
				TParameterDeclaration def = procedure.getParameterDeclarations().getParameterDeclarationItem(i);
				argMap.put(def.getParameterName().toString(), def.getDataType().getDataTypeName());
			}
		}
		StringBuilder buffer = new StringBuilder();
		buffer.append("(function(");
		String[] args = argMap.keySet().toArray(new String[0]);
		for (int i = 0; i < args.length; i++) {
			buffer.append(args[i].toUpperCase());
			if (i < args.length - 1) {
				buffer.append(",");
			}
		}
		buffer.append("){\n");

		int start = procedure.getRoutineBody().indexOf("$$") + 2;
		int end = procedure.getRoutineBody().lastIndexOf("$$") - 1;
		String body = procedure.getRoutineBody().substring(start, end);
		if (body.indexOf("`") != -1) {
			Pattern pattern = Pattern.compile("`.+?`", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
			Matcher matcher = pattern.matcher(body);
			StringBuffer replaceBuffer = new StringBuffer();
			while (matcher.find()) {
				String condition = matcher.group().replaceAll("\r\n", "\n").replace("'", "\\\\'").replace("\n", "\\\\n'\n+'").replace("`", "'");
				matcher.appendReplacement(replaceBuffer, condition);
			}
			matcher.appendTail(replaceBuffer);
			body = replaceBuffer.toString();
		}
		buffer.append(body);
		buffer.append("})(");
		for (int i = 0; i < args.length; i++) {
			String type = argMap.get(args[i]);
			if (type.equalsIgnoreCase("VARCHAR")) {
				buffer.append("'pseudo'");
			} else if (type.equalsIgnoreCase("STRING")) {
				buffer.append("'pseudo'");
			} else if (type.equalsIgnoreCase("CHAR")) {
				buffer.append("'pseudo'");
			} else if (type.equalsIgnoreCase("CHARACTER")) {
				buffer.append("'pseudo'");
			} else if (type.equalsIgnoreCase("TEXT")) {
				buffer.append("'pseudo'");
			} else if (type.equalsIgnoreCase("BINARY")) {
				buffer.append("'pseudo'");
			} else if (type.equalsIgnoreCase("VARBINARY")) {
				buffer.append("'pseudo'");
			} else if (type.equalsIgnoreCase("BOOLEAN")) {
				buffer.append(true);
			} else if (type.equalsIgnoreCase("FLOAT")) {
				buffer.append("1.0");
			} else if (type.equalsIgnoreCase("FLOAT4")) {
				buffer.append("1.0");
			} else if (type.equalsIgnoreCase("FLOAT8")) {
				buffer.append("1.0");
			} else if (type.equalsIgnoreCase("DOUBLE")) {
				buffer.append("1.0");
			} else if (type.equalsIgnoreCase("DOUBLE PRECISION")) {
				buffer.append("1.0");
			} else if (type.equalsIgnoreCase("REAL")) {
				buffer.append("1.0");
			} else if (type.equalsIgnoreCase("NUMBER")) {
				buffer.append("1.0");
			} else if (type.equalsIgnoreCase("DECIMAL")) {
				buffer.append("1.0");
			} else if (type.equalsIgnoreCase("NUMERIC")) {
				buffer.append("1.0");
			} else if (type.equalsIgnoreCase("INT")) {
				buffer.append("1");
			} else if (type.equalsIgnoreCase("INTEGER")) {
				buffer.append("1");
			} else if (type.equalsIgnoreCase("BIGINT")) {
				buffer.append("1");
			} else if (type.equalsIgnoreCase("SMALLINT")) {
				buffer.append("1");
			} else if (type.equalsIgnoreCase("DATE")) {
				buffer.append("new Date()");
			} else if (type.equalsIgnoreCase("DATETIME")) {
				buffer.append("new Date()");
			} else if (type.equalsIgnoreCase("TIME")) {
				buffer.append("new Date()");
			} else if (type.equalsIgnoreCase("TIMESTAMP")) {
				buffer.append("new Date()");
			} else if (type.equalsIgnoreCase("TIMESTAMP_LTZ")) {
				buffer.append("new Date()");
			} else if (type.equalsIgnoreCase("TIMESTAMP_NTZ")) {
				buffer.append("new Date()");
			} else if (type.equalsIgnoreCase("TIMESTAMP_TZ")) {
				buffer.append("new Date()");
			} else if (type.equalsIgnoreCase("VARIANT")) {
				buffer.append("{}");
			} else if (type.equalsIgnoreCase("OBJECT")) {
				buffer.append("{}");
			} else if (type.equalsIgnoreCase("ARRAY")) {
				buffer.append("[]");
			} else if (type.equalsIgnoreCase("GEOGRAPHY")) {
				buffer.append("{}");
			}
			if (i < args.length - 1) {
				buffer.append(",");
			}
		}
		buffer.append(");");

		ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
		ScriptEngine nashorn = scriptEngineManager.getEngineByName("nashorn");

		try {
			nashorn.eval(new InputStreamReader(SnowflakeSQLExtractor.class.getResourceAsStream("snowflake.js")));
			nashorn.eval(new StringReader(buffer.toString()));
		} catch (ScriptException e) {
			System.err.println("执行脚本错误: " + e.getMessage());
		}
	}

}
