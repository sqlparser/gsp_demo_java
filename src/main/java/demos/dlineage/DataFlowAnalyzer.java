
package demos.dlineage;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TCustomSqlStatement;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.TSourceToken;
import gudusoft.gsqlparser.dlineage.dataflow.listener.DataFlowHandleAdapter;
import gudusoft.gsqlparser.dlineage.dataflow.model.ErrorInfo;
import gudusoft.gsqlparser.dlineage.dataflow.model.ResultSetType;
import gudusoft.gsqlparser.dlineage.dataflow.model.json.Dataflow;
import gudusoft.gsqlparser.dlineage.dataflow.model.xml.dataflow;
import gudusoft.gsqlparser.dlineage.dataflow.sqlenv.SQLEnvParser;
import gudusoft.gsqlparser.dlineage.util.ProcessUtility;
import gudusoft.gsqlparser.dlineage.util.RemoveDataflowFunction;
import gudusoft.gsqlparser.dlineage.util.XML2Model;
import gudusoft.gsqlparser.sqlenv.*;
import gudusoft.gsqlparser.sqlenv.parser.TJSONSQLEnvParser;
import gudusoft.gsqlparser.util.SQLUtil;
import gudusoft.gsqlparser.util.json.JSON;

import java.io.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;

public class DataFlowAnalyzer {

	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println(
					"Usage: java DataFlowAnalyzer [/f <path_to_sql_file>] [/d <path_to_directory_includes_sql_files>] [/stat] [/s [/topselectlist] [/text] [/withTemporaryTable]] [/i] [/showResultSetTypes <resultset_types>] [/ic] [/lof] [/j] [/json] [/traceView] [/t <database type>] [/o <output file path>] [/version] [/env <path_to_metadata.json>]  [/tableLineage [/csv [/delimeter <delimeter>]]] [/transform [/coor]] [/showConstant] [/treatArgumentsInCountFunctionAsDirectDataflow]");
			System.out.println("/f: Optional, the full path to SQL file.");
			System.out.println("/d: Optional, the full path to the directory includes the SQL files.");
			System.out.println("/j: Optional, return the result including the join relation.");
			System.out.println("/s: Optional, simple output, ignore the intermediate results.");
			System.out.println("/topselectlist: Optional, simple output with top select results.");
			System.out.println("/withTemporaryTable: Optional, simple output with the temporary tables.");
			System.out.println(
					"/i: Optional, the same as /s option, but will keep the resultset generated by the SQL function, this parameter will have the same effect as /s /topselectlist + keep resultset generated by the sql function.");
			System.out.println("/showResultSetTypes: Optional, simple output with specify resultset types, separate with commas, resultset types contains array, struct, result_of, cte, insert_select, update_select, merge_update, merge_insert, output, update_set,\r\n"
					+ "	pivot_table, unpivot_table, alias, rs, function, case_when");
			System.out.println(
					"/if: Optional, keep all the intermediate resultset, but remove the resultset generated by the SQL function");
			System.out.println("/ic: Optional, ignore the coordinates in the output.");
			System.out.println("/lof: Option, link orphan column to the first table.");
			System.out.println(
					"/traceView: Optional, only output the name of source tables and views, ignore all intermedidate data.");
			System.out.println(
					"/text: Optional, this option is valid only /s is used, output the column dependency in text mode.");
			System.out.println("/json: Optional, print the json format output.");
			System.out.println("/stat: Optional, output the analysis statistic information.");
			System.out.println("/tableLineage [/csv /delimiter]: Optional, output tabel level lineage.");
			System.out.println("/csv: Optional, output column level lineage in csv format.");
			System.out.println("/delimiter: Optional, the delimiter of output column level lineage in csv format.");
			System.out.println("/t: Option, set the database type. "
					+ "Support access,bigquery,couchbase,dax,db2,greenplum,hana,hive,impala,informix,mdx,mssql,\n"
					+ "sqlserver,mysql,netezza,odbc,openedge,oracle,postgresql,postgres,redshift,snowflake,\n"
					+ "sybase,teradata,soql,vertica\n, " + "the default value is oracle");
			System.out.println("/o: Optional, write the output stream to the specified file.");
			System.out.println("/log: Optional, generate a dataflow.log file to log information.");
			System.out.println("/env: Optional, specify a metadata.json to get the database metadata information.");
			System.out.println("/transform: Optional, output the relation transform code.");
			System.out.println("/coor: Optional, output the relation transform coordinate, but not the code.");
			System.out.println("/defaultDatabase: Optional, specify the default schema.");
			System.out.println("/defaultSchema: Optional, specify the default schema.");
			System.out.println("/showImplicitSchema: Optional, show implicit schema.");
			System.out.println("/showConstant: Optional, show constant table.");
			System.out.println("/treatArgumentsInCountFunctionAsDirectDataflow: Optional, treat arguments in count function as direct dataflow.");
			//add by grq 2022.10.25 issue=I5X3KO
			System.out.println("/fromdb: Optional, specifies the database connection parameters.");
			System.out.println("/exportonly: Optional, just export metadata.json, no further data analysis.");
			System.out.println("/metadataoutput: Optional, specifies the metadata output directory and file name.");
			//end by grq
			return;
		}

		File sqlFiles = null;

		List<String> argList = Arrays.asList(args);

		EDbVendor vendor = EDbVendor.dbvoracle;
		int index = argList.indexOf("/t");
		if (index != -1 && args.length > index + 1) {
			vendor = TGSqlParser.getDBVendorByName(args[index + 1]);
		}

		if (argList.indexOf("/version") != -1) {
			System.out.println("Version: " + gudusoft.gsqlparser.dlineage.DataFlowAnalyzer.getVersion());
			System.out.println("Release Date: " + gudusoft.gsqlparser.dlineage.DataFlowAnalyzer.getReleaseDate());
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
		}
		//add by grq 2022.10.25 issue=I5X3KO
		else if (argList.indexOf("/fromdb") != -1 && argList.size() > argList.indexOf("/fromdb") + 1){
			String metadataoutput = "metadata.json";
			if(argList.indexOf("/metadataoutput") != -1 && argList.size() > argList.indexOf("/metadataoutput") + 1){
				metadataoutput = args[argList.indexOf("/metadataoutput") + 1];
			}
			if(!SqlflowIngester.export(vendor.name(), args[argList.indexOf("/fromdb") + 1].split("\\s+"), metadataoutput)){
				return;
			}
			if(argList.indexOf("/exportonly") != -1 ){
				return;
			}
			sqlFiles = new File(metadataoutput );
		}
		//end by grq
		else {
			System.out.println("Please specify a sql file path or directory path to analyze dlineage.");
			return;
		}

		String outputFile = null;

		index = argList.indexOf("/o");

		if (index != -1 && args.length > index + 1) {
			outputFile = args[index + 1];
		}

		boolean simple = argList.indexOf("/s") != -1;
		boolean ignoreTemporaryTable = argList.indexOf("/withTemporaryTable") == -1;
		boolean ignoreResultSets = argList.indexOf("/i") != -1;
		boolean showJoin = argList.indexOf("/j") != -1;
		boolean transform = argList.indexOf("/transform") != -1;
		boolean transformCoordinate = transform && (argList.indexOf("/coor") != -1);
		boolean textFormat = false;
		boolean jsonFormat = false;
		boolean linkOrphanColumnToFirstTable = argList.indexOf("/lof") != -1;
		boolean ignoreCoordinate = argList.indexOf("/ic") != -1;
		boolean showImplicitSchema = argList.indexOf("/showImplicitSchema") != -1;

		if (simple) {
			textFormat = argList.indexOf("/text") != -1;
		}

		boolean traceView = argList.indexOf("/traceView") != -1;
		if (traceView) {
			simple = true;
		}

		jsonFormat = argList.indexOf("/json") != -1;

		boolean stat = argList.indexOf("/stat") != -1;

		boolean ignoreFunction = argList.indexOf("/if") != -1;

		boolean topselectlist = argList.indexOf("/topselectlist") != -1;

		if (argList.indexOf("/s") != -1 && argList.indexOf("/topselectlist") != -1) {
			simple = true;
			topselectlist = true;
		}

		boolean tableLineage = argList.indexOf("/tableLineage") != -1;
		boolean csv = argList.indexOf("/csv") != -1;
		String delimiter = (argList.indexOf("/delimiter") != -1 && argList.size() > argList.indexOf("/delimiter") + 1) ? argList.get(argList.indexOf("/delimiter") + 1) : ",";
		if (tableLineage) {
			simple = false;
			ignoreResultSets = false;
		}

		TSQLEnv sqlenv = null;
		
		if (argList.indexOf("/env") != -1 && argList.size() > argList.indexOf("/env") + 1) {
			File metadataFile = new File(args[argList.indexOf("/env") + 1]);
			if (metadataFile.exists()) {
				TJSONSQLEnvParser jsonSQLEnvParser = new TJSONSQLEnvParser(null, null, null);
				TSQLEnv[] envs = jsonSQLEnvParser.parseSQLEnv(vendor, SQLUtil.getFileContent(metadataFile));
				if(envs!=null && envs.length>0) {
					sqlenv = envs[0];
				}
			}
		}

		if (!stat) {
			FileOutputStream writer = null;
			if (outputFile != null) {
				try {
					writer = new FileOutputStream(outputFile);
					System.setOut(new PrintStream(writer));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}

			gudusoft.gsqlparser.dlineage.DataFlowAnalyzer dlineage = new gudusoft.gsqlparser.dlineage.DataFlowAnalyzer(
					sqlFiles, vendor, simple);

			if (sqlenv != null) {
				dlineage.setSqlEnv(sqlenv);
			}

			dlineage.setTransform(transform);
			dlineage.setTransformCoordinate(transformCoordinate);
			dlineage.setShowJoin(showJoin);
			dlineage.setIgnoreRecordSet(ignoreResultSets);
			dlineage.setLinkOrphanColumnToFirstTable(linkOrphanColumnToFirstTable);
			dlineage.setIgnoreCoordinate(ignoreCoordinate);
			dlineage.setSimpleShowTopSelectResultSet(topselectlist);
			dlineage.setShowImplicitSchema(showImplicitSchema);
			dlineage.setIgnoreTemporaryTable(ignoreTemporaryTable);
			if(simple) {
				dlineage.setShowCallRelation(true);
			}
			
			dlineage.setShowConstantTable(argList.indexOf("/showConstant")!=-1);
			dlineage.setShowCountTableColumn(argList.indexOf("/treatArgumentsInCountFunctionAsDirectDataflow")!=-1);

			if (argList.indexOf("/defaultDatabase") != -1) {
				dlineage.getOption().setDefaultDatabase(args[argList.indexOf("/defaultDatabase") + 1]);
			}
			if (argList.indexOf("/defaultSchema") != -1) {
				dlineage.getOption().setDefaultSchema(args[argList.indexOf("/defaultSchema") + 1]);
			}

			if(argList.indexOf("/showResultSetTypes")!=-1) {
				String resultSetTypes = args[argList.indexOf("/showResultSetTypes") + 1];
				if(resultSetTypes!=null) {
					dlineage.getOption().showResultSetTypes(resultSetTypes.split(","));
				}
			}

			if (simple && !jsonFormat) {
				dlineage.setTextFormat(textFormat);
			}

			String result = null;
			if (tableLineage) {
				dlineage.generateDataFlow();
				dataflow originDataflow = dlineage.getDataFlow();
				if (csv) {
					result = ProcessUtility.generateTableLevelLineageCsv(dlineage, originDataflow, delimiter);
				} else {
					dataflow dataflow = ProcessUtility.generateTableLevelLineage(dlineage, originDataflow);
					if (jsonFormat) {
						Dataflow model = gudusoft.gsqlparser.dlineage.DataFlowAnalyzer.getSqlflowJSONModel(dataflow, vendor);
						result = JSON.toJSONString(model);
					} else {
						try {
							result = XML2Model.saveXML(dataflow);
						}catch (Exception e) {
							e.printStackTrace();
							result = null;
						}
					}
				}
			} else {
				result = dlineage.generateDataFlow();
				if (csv) {
					dataflow originDataflow = dlineage.getDataFlow();
					result = ProcessUtility.generateColumnLevelLineageCsv(dlineage, originDataflow, delimiter);
				}
				else if (jsonFormat) {
					dataflow dataflow = dlineage.getDataFlow();
					if (ignoreFunction) {
						dataflow = new RemoveDataflowFunction().removeFunction(dataflow);
					}
					Dataflow model = gudusoft.gsqlparser.dlineage.DataFlowAnalyzer.getSqlflowJSONModel(dataflow, vendor);
					result = JSON.toJSONString(model);
				} else if (traceView) {
					result = dlineage.traceView();
				} else if (ignoreFunction && result.trim().startsWith("<?xml")) {
					dataflow dataflow = dlineage.getDataFlow();
					dataflow = new RemoveDataflowFunction().removeFunction(dataflow);
					try {
						result = XML2Model.saveXML(dataflow);
					}catch (Exception e) {
						e.printStackTrace();
						result = null;
					}
				}
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

			List<ErrorInfo> errors = dlineage.getErrorMessages();
			if (!errors.isEmpty()) {
				System.err.println("Error log:\n");
				for (int i = 0; i < errors.size(); i++) {
					System.err.println(errors.get(i).getErrorMessage());
				}
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
		} else {
			final File[] files = SQLUtil.listFiles(sqlFiles,
					file -> file.isDirectory() || file.getName().toLowerCase().endsWith(".sql"));

			final int[] totalLines = new int[1];
			final int[] totalStatementCount = new int[1];
			final int[] successStatementCount = new int[1];
			final long[] analyzeTotalTime = new long[1];

			if (files.length > 0) {
				if (files[0].length() < 10240) {
					final gudusoft.gsqlparser.dlineage.DataFlowAnalyzer warmUp = new gudusoft.gsqlparser.dlineage.DataFlowAnalyzer(
							files[0], vendor, simple);
					warmUp.generateDataFlow();
				} else {
					final gudusoft.gsqlparser.dlineage.DataFlowAnalyzer warmUp = new gudusoft.gsqlparser.dlineage.DataFlowAnalyzer(
							"select a from b", vendor, simple);
					warmUp.generateDataFlow();
				}
			}

			int ignoreFileCount = 0;
			for (int i = 0; i < files.length; i++) {
				final File file = files[i];
				if (file.length() > 10000) {
					ignoreFileCount++;
					continue;
				}
				try {
					final gudusoft.gsqlparser.dlineage.DataFlowAnalyzer dlineage = new gudusoft.gsqlparser.dlineage.DataFlowAnalyzer(
							file, vendor, simple);
					if (sqlenv != null) {
						dlineage.setSqlEnv(sqlenv);
					}
					dlineage.setLinkOrphanColumnToFirstTable(false);
					DataFlowHandleAdapter listener = new DataFlowHandleAdapter() {
						long analyzeStartTime;
						int statmentCount = 0;

						public void startAnalyze(File file, long fileCountOrSqlLength, boolean isFileCount) {
							analyzeStartTime = System.currentTimeMillis();
						}

						@Override
						public void endAnalyzeStatment(TCustomSqlStatement stmt) {
							statmentCount++;
						}

						@Override
						public void endAnalyzeDataFlow(TGSqlParser sqlparser) {
							successStatementCount[0] += statmentCount;
							totalStatementCount[0] += sqlparser.getSqlstatements().size();
							TSourceToken endToken = sqlparser.sourcetokenlist.get(sqlparser.sourcetokenlist.size() - 1);
							totalLines[0] += (endToken.lineNo + endToken.astext.split("\n").length - 1);
						}

						@Override
						public void endAnalyze() {
							StringBuilder builder = new StringBuilder();
							if (!dlineage.getErrorMessages().isEmpty()) {
								if (dlineage.getDataFlow() != null && dlineage.getDataFlow().getTables() != null
										&& !dlineage.getDataFlow().getTables().isEmpty()) {
									builder.append("status=").append("2");
								} else {
									builder.append("status=").append("3");
								}
							} else {
								builder.append("status=").append("1");
							}
							long spendTime = System.currentTimeMillis() - analyzeStartTime;
							builder.append(", time=").append(spendTime).append("ms");
							builder.append(", file=").append(file.getAbsolutePath());
							builder.append(", length=").append(file.length());
							System.out.println(builder.toString());

							analyzeTotalTime[0] += spendTime;
						}
					};
					dlineage.setHandleListener(listener);
					dlineage.generateDataFlow();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			System.out.println();
			System.out.println("Summary:");
			System.out.println("Total sql files: " + files.length);
			System.out.println("Total sql statements: " + totalStatementCount[0]);
			System.out.println("Total sql lines: " + totalLines[0]);
			int dealFileCount = files.length - ignoreFileCount;
			if (dealFileCount > 0) {
				System.out.println(
						"Average statements per file: " + Math.round(totalStatementCount[0] / (double) dealFileCount));
				System.out.println("Average lines per file :" + Math.round(totalLines[0] / (double) dealFileCount));
				System.out.println("Average lines per sql statement: "
						+ Math.round(totalLines[0] / (double) totalStatementCount[0]));

				BigDecimal success = new BigDecimal(successStatementCount[0] / (double) totalStatementCount[0]);
				success.setScale(2, BigDecimal.ROUND_HALF_UP);

				BigDecimal failed = new BigDecimal(1 - successStatementCount[0] / (double) totalStatementCount[0]);
				failed.setScale(2, BigDecimal.ROUND_HALF_UP);

				NumberFormat percent = NumberFormat.getPercentInstance();
				percent.setMaximumFractionDigits(2);

				System.out.println(
						"Statement passed: " + successStatementCount[0] + ", " + percent.format(success.doubleValue()));
				System.out.println("Statement failed: " + (totalStatementCount[0] - successStatementCount[0]) + ", "
						+ percent.format(failed.doubleValue()));
			}
			System.out.println("Total time escaped: " + analyzeTotalTime[0] + "ms");
			System.out.println(
					"Average time per file: " + +Math.round(analyzeTotalTime[0] / (double) dealFileCount) + "ms");
			System.out.println("Average time per sql statement: "
					+ Math.round(analyzeTotalTime[0] / (double) successStatementCount[0]) + "ms");
			// if (ignoreFileCount > 0)
			{
				System.out.println("Files ignored in trial version: " + ignoreFileCount);
			}
		}
	}

	private static TSQLDataSource createSQLDataSource(EDbVendor vendor, Class<?> driver, String jdbc, String account, String password, String schema) {
		try {
			if (vendor == EDbVendor.dbvoracle) {
				TOracleSQLDataSource datasource = TSQLDataSource.createSQLDataSource(vendor, driver, jdbc, account,
						password);
				if (schema != null) {
					datasource.setExtractedSchemas(schema);
				}
				return datasource;
			}
			if (vendor == EDbVendor.dbvmssql) {
				TMssqlSQLDataSource datasource = TSQLDataSource.createSQLDataSource(vendor, driver, jdbc, account,
						password);
				if (schema != null) {
					datasource.setExtractedDbsSchemas("*/" + schema);
				}
				return datasource;
			}
			if (vendor == EDbVendor.dbvpostgresql) {
				TPostgreSQLDataSource datasource = TSQLDataSource.createSQLDataSource(vendor, driver, jdbc, account,
						password);
				if (schema != null) {
					datasource.setExtractedDbsSchemas("*/" + schema);
				}
				return datasource;
			}
			if (vendor == EDbVendor.dbvgreenplum) {
				TGreenplumSQLDataSource datasource = TSQLDataSource.createSQLDataSource(vendor, driver, jdbc, account,
						password);
				if (schema != null) {
					datasource.setExtractedDbsSchemas("*/" + schema);
				}
				return datasource;
			}
			if (vendor == EDbVendor.dbvredshift) {
				TRedshiftSQLDataSource datasource = TSQLDataSource.createSQLDataSource(vendor, driver, jdbc, account,
						password);
				if (schema != null) {
					datasource.setExtractedDbsSchemas("*/" + schema);
				}
				return datasource;
			}
			if (vendor == EDbVendor.dbvmysql) {
				TMysqlSQLDataSource datasource = TSQLDataSource.createSQLDataSource(vendor, driver, jdbc, account,
						password);
				if (schema != null) {
					datasource.setExtractedDbsSchemas("*/" + schema);
				}
				return datasource;
			}
			if (vendor == EDbVendor.dbvnetezza) {
				TNetezzaSQLDataSource datasource = TSQLDataSource.createSQLDataSource(vendor, driver, jdbc, account,
						password);
				if (schema != null) {
					datasource.setExtractedDbsSchemas("*/" + schema);
				}
				return datasource;
			}
			if (vendor == EDbVendor.dbvsnowflake) {
				TSnowflakeSQLDataSource datasource = TSQLDataSource.createSQLDataSource(vendor, driver, jdbc, account,
						password);
				if (schema != null) {
					datasource.setExtractedDbsSchemas("*/" + schema);
				}
				return datasource;
			}
			if (vendor == EDbVendor.dbvteradata) {
				TTeradataSQLDataSource datasource = TSQLDataSource.createSQLDataSource(vendor, driver, jdbc, account,
						password);
				return datasource;
			}
			if (vendor == EDbVendor.dbvhive) {
				THiveMetadataDataSource datasource = TSQLDataSource.createSQLDataSource(vendor, driver, jdbc, account,
						password);
				return datasource;
			}
			if (vendor == EDbVendor.dbvimpala) {
				TImpalaSQLDataSource datasource = TSQLDataSource.createSQLDataSource(vendor, driver, jdbc, account,
						password);
				return datasource;
			}
		} catch (Exception e) {
			System.err.println("Connect datasource failed. " + e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	private static TSQLDataSource createSQLDataSource(EDbVendor vendor, String jdbc, String user, String password,
			String schema) {
		try {
			if (vendor == EDbVendor.dbvoracle) {
				TOracleSQLDataSource datasource = TSQLDataSource.createSQLDataSource(vendor, jdbc, user, password);
				if (schema != null) {
					datasource.setExtractedSchemas(schema);
				}
				return datasource;
			}
			if (vendor == EDbVendor.dbvmssql) {
				TMssqlSQLDataSource datasource = TSQLDataSource.createSQLDataSource(vendor, jdbc, user, password);
				String database = datasource.getDatabase();
				if (database != null) {
					if (schema != null) {
						datasource.setExtractedDbsSchemas(database + "/" + schema);
					} else {
						datasource.setExtractedDbsSchemas(database);
					}
				}
				return datasource;
			}
			if (vendor == EDbVendor.dbvpostgresql) {
				TPostgreSQLDataSource datasource = TSQLDataSource.createSQLDataSource(vendor, jdbc, user, password);
				String database = datasource.getDatabase();
				if (database != null) {
					if (schema != null) {
						datasource.setExtractedDbsSchemas(database + "/" + schema);
					} else {
						datasource.setExtractedDbsSchemas(database);
					}
				}
				return datasource;
			}
			if (vendor == EDbVendor.dbvgreenplum) {
				TGreenplumSQLDataSource datasource = TSQLDataSource.createSQLDataSource(vendor, jdbc, user, password);
				String database = datasource.getDatabase();
				if (database != null) {
					if (schema != null) {
						datasource.setExtractedDbsSchemas(database + "/" + schema);
					} else {
						datasource.setExtractedDbsSchemas(database);
					}
				}
				return datasource;
			}
			if (vendor == EDbVendor.dbvredshift) {
				TRedshiftSQLDataSource datasource = TSQLDataSource.createSQLDataSource(vendor, jdbc, user, password);
				String database = datasource.getDatabase();
				if (database != null) {
					if (schema != null) {
						datasource.setExtractedDbsSchemas(database + "/" + schema);
					} else {
						datasource.setExtractedDbsSchemas(database);
					}
				}
				return datasource;
			}
			if (vendor == EDbVendor.dbvmysql) {
				TMysqlSQLDataSource datasource = TSQLDataSource.createSQLDataSource(vendor, jdbc, user, password);
				String database = datasource.getDatabase();
				if (database != null) {
					if (schema != null) {
						datasource.setExtractedDbsSchemas(database + "/" + schema);
					} else {
						datasource.setExtractedDbsSchemas(database);
					}
				}
				return datasource;
			}
			if (vendor == EDbVendor.dbvnetezza) {
				TNetezzaSQLDataSource datasource = TSQLDataSource.createSQLDataSource(vendor, jdbc, user, password);
				String database = datasource.getDatabase();
				if (database != null) {
					if (schema != null) {
						datasource.setExtractedDbsSchemas(database + "/" + schema);
					} else {
						datasource.setExtractedDbsSchemas(database);
					}
				}
				return datasource;
			}
			if (vendor == EDbVendor.dbvsnowflake) {
				TSnowflakeSQLDataSource datasource = TSQLDataSource.createSQLDataSource(vendor, jdbc, user, password);
				String database = datasource.getDatabase();
				if (database != null) {
					if (schema != null) {
						datasource.setExtractedDbsSchemas(database + "/" + schema);
					} else {
						datasource.setExtractedDbsSchemas(database);
					}
				}
				return datasource;
			}
			if (vendor == EDbVendor.dbvteradata) {
				TTeradataSQLDataSource datasource = TSQLDataSource.createSQLDataSource(vendor, jdbc, user, password);
				String database = datasource.getDatabase();
				if (database != null) {
					datasource.setExtractedDatabases(database);
				}
				return datasource;
			}
			if (vendor == EDbVendor.dbvhive) {
				THiveMetadataDataSource datasource = TSQLDataSource.createSQLDataSource(vendor, jdbc, user, password);
				String database = datasource.getDatabase();
				if (database != null) {
					datasource.setExtractedDatabases(database);
				}
				return datasource;
			}
			if (vendor == EDbVendor.dbvimpala) {
				TImpalaSQLDataSource datasource = TSQLDataSource.createSQLDataSource(vendor, jdbc, user, password);
				String database = datasource.getDatabase();
				if (database != null) {
					datasource.setExtractedDatabases(database);
				}
				return datasource;
			}
		} catch (Exception e) {
			System.err.println("Connect datasource failed. " + e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	private static TSQLDataSource createSQLDataSource(EDbVendor vendor, String host, String port, String user,
			String password, String database, String schema) {
		try {
			if (vendor == EDbVendor.dbvoracle) {
				TOracleSQLDataSource datasource = new TOracleSQLDataSource(host, port, user, password, database);
				if (schema != null) {
					datasource.setExtractedSchemas(schema);
				}
				return datasource;
			}
			if (vendor == EDbVendor.dbvmssql) {
				TMssqlSQLDataSource datasource = new TMssqlSQLDataSource(host, port, user, password);
				if (database != null) {
					if (schema != null) {
						datasource.setExtractedDbsSchemas(database + "/" + schema);
					} else {
						datasource.setExtractedDbsSchemas(database);
					}
				}
				return datasource;
			}
			if (vendor == EDbVendor.dbvpostgresql) {
				TPostgreSQLDataSource datasource = new TPostgreSQLDataSource(host, port, user, password, database);
				if (schema != null) {
					datasource.setExtractedDbsSchemas(database + "/" + schema);
				} else {
					datasource.setExtractedDbsSchemas(database);
				}
				return datasource;
			}
			if (vendor == EDbVendor.dbvgreenplum) {
				TGreenplumSQLDataSource datasource = new TGreenplumSQLDataSource(host, port, user, password);
				if (schema != null) {
					datasource.setExtractedDbsSchemas(database + "/" + schema);
				} else {
					datasource.setExtractedDbsSchemas(database);
				}
				return datasource;
			}
			if (vendor == EDbVendor.dbvhive) {
				THiveMetadataDataSource datasource = new THiveMetadataDataSource(host, port, user, password, database);
				if (database != null) {
					datasource.setExtractedDatabases(database);
				}
				return datasource;
			}
			if (vendor == EDbVendor.dbvimpala) {
				TImpalaSQLDataSource datasource = new TImpalaSQLDataSource(host, port, user, password, database);
				if (database != null) {
					datasource.setExtractedDatabases(database);
				}
				return datasource;
			}
			if (vendor == EDbVendor.dbvredshift) {
				TRedshiftSQLDataSource datasource = new TRedshiftSQLDataSource(host, port, user, password, database);
				if (schema != null) {
					datasource.setExtractedDbsSchemas(database + "/" + schema);
				} else {
					datasource.setExtractedDbsSchemas(database);
				}
				return datasource;
			}
			if (vendor == EDbVendor.dbvmysql) {
				TMysqlSQLDataSource datasource = new TMysqlSQLDataSource(host, port, user, password);
				if (database != null) {
					if (schema != null) {
						datasource.setExtractedDbsSchemas(database + "/" + schema);
					} else {
						datasource.setExtractedDbsSchemas(database);
					}
				}
				return datasource;
			}
			if (vendor == EDbVendor.dbvnetezza) {
				TNetezzaSQLDataSource datasource = new TNetezzaSQLDataSource(host, port, user, password, database);
				if (database != null) {
					if (schema != null) {
						datasource.setExtractedDbsSchemas(database + "/" + schema);
					} else {
						datasource.setExtractedDbsSchemas(database);
					}
				}
				return datasource;
			}
			if (vendor == EDbVendor.dbvsnowflake) {
				TSnowflakeSQLDataSource datasource = new TSnowflakeSQLDataSource(host, port, user, password);
				if (database != null) {
					if (schema != null) {
						datasource.setExtractedDbsSchemas(database + "/" + schema);
					} else {
						datasource.setExtractedDbsSchemas(database);
					}
				}
				return datasource;
			}
			if (vendor == EDbVendor.dbvteradata) {
				TTeradataSQLDataSource datasource = new TTeradataSQLDataSource(host, port, user, password, database);
				if (database != null) {
					datasource.setExtractedDatabases(database);
				}
				return datasource;
			}
		} catch (Exception e) {
			System.err.println("Connect datasource failed. " + e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
}
