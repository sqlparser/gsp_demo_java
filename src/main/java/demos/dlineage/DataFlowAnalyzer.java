
package demos.dlineage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.dlineage.dataflow.model.json.DataFlow;

public class DataFlowAnalyzer {

	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println(
					"Usage: java DataFlowAnalyzer [/f <path_to_sql_file>] [/d <path_to_directory_includes_sql_files>] [/s [/text]] [/json] [/traceView] [/t <database type>] [/o <output file path>][/version]");
			System.out.println("/f: Option, specify the sql file path to analyze fdd relation.");
			System.out.println("/d: Option, specify the sql directory path to analyze fdd relation.");
			System.out.println("/j: Option, analyze the join relation.");
			System.out.println("/s: Option, simple output, ignore the intermediate results.");
			System.out.println("/i: Option, ignore all result sets.");
			System.out.println("/traceView: Option, analyze the source tables of views.");
			System.out.println("/text: Option, print the plain text format output.");
			System.out.println("/json: Option, print the json format output.");
			System.out.println(
					"/t: Option, set the database type. Support oracle, mysql, mssql, db2, netezza, teradata, informix, sybase, postgresql, hive, greenplum and redshift, the default type is oracle");
			System.out.println("/o: Option, write the output stream to the specified file.");
			System.out.println("/log: Option, generate a dataflow.log file to log information.");
			return;
		}

		File sqlFiles = null;

		List<String> argList = Arrays.asList(args);
		
		if(argList.indexOf("/version")!=-1){
			System.out.println("Version: "+gudusoft.gsqlparser.dlineage.DataFlowAnalyzer.getVersion());
			System.out.println("Release Date: "+gudusoft.gsqlparser.dlineage.DataFlowAnalyzer.getReleaseDate());
			return;
		}

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
			System.out.println("Please specify a sql file path or directory path to analyze dlineage.");
			return;
		}

		EDbVendor vendor = EDbVendor.dbvoracle;

		int index = argList.indexOf("/t");

		if (index != -1 && args.length > index + 1) {
			vendor = TGSqlParser.getDBVendorByName(args[index + 1]);
		}

		String outputFile = null;

		index = argList.indexOf("/o");

		if (index != -1 && args.length > index + 1) {
			outputFile = args[index + 1];
		}

		FileOutputStream writer = null;
		if (outputFile != null) {
			try {
				writer = new FileOutputStream(outputFile);
				System.setOut(new PrintStream(writer));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}

		boolean simple = argList.indexOf("/s") != -1;
		boolean ignoreResultSets = argList.indexOf("/i") != -1;
		boolean showJoin = argList.indexOf("/j") != -1;
		boolean textFormat = false;
		boolean jsonFormat = false;
		if (simple) {
			textFormat = argList.indexOf("/text") != -1;
		}
		
		boolean traceView = argList.indexOf("/traceView") != -1;
		if(traceView){
			simple = true;
		}
		
		jsonFormat = argList.indexOf("/json") != -1;

		gudusoft.gsqlparser.dlineage.DataFlowAnalyzer dlineage = new gudusoft.gsqlparser.dlineage.DataFlowAnalyzer(sqlFiles, vendor, simple);

		dlineage.setShowJoin(showJoin);
		dlineage.setIgnoreRecordSet(ignoreResultSets);


		if (simple && !jsonFormat) {
			dlineage.setTextFormat(textFormat);
		}

		StringBuffer errorBuffer = new StringBuffer();
		String result = dlineage.generateDataFlow(errorBuffer);
		
		if (jsonFormat) {
			JSONObject jsonResult = new JSONObject();
			DataFlow model = gudusoft.gsqlparser.dlineage.DataFlowAnalyzer.getSqlflowJSONModel(dlineage.getDataFlow());
			model.setDbvendor(vendor.name());
			jsonResult.put("data", model);
			result = JSON.toJSONString(jsonResult, true);
		} else if (traceView) {
			result = dlineage.traceView();
		}
		
		if (result != null) {
			System.out.println(result);

			if (writer != null && result.length() < 1024 * 1024) {
				System.err.println(result);
			}
		}

		try {
			if (writer != null) {
				writer.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		boolean log = argList.indexOf("/log") != -1;
		PrintStream pw = null;
		ByteArrayOutputStream sw = null;
		PrintStream systemSteam = System.err;

		try {
			sw = new ByteArrayOutputStream();
			pw = new PrintStream(sw);
			System.setErr(pw);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (errorBuffer.length() > 0) {
			System.err.println("Error log:\n" + errorBuffer);
		}

		if (sw != null) {
			String errorMessage = sw.toString().trim();
			if (errorMessage.length() > 0) {
				if (log) {
					try {
						pw = new PrintStream(new File(".", "dataflow.log"));
						pw.print(errorMessage);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
				}

				System.setErr(systemSteam);
				System.err.println(errorMessage);
			}
		}
	}
}
