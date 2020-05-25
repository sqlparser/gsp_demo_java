package demos.dlineage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import demos.dlineage.dataflow.model.RelationType;
import demos.dlineage.dataflow.model.json.Argument;
import demos.dlineage.dataflow.model.json.Column;
import demos.dlineage.dataflow.model.json.Coordinate;
import demos.dlineage.dataflow.model.json.DBObject;
import demos.dlineage.dataflow.model.json.DataFlow;
import demos.dlineage.dataflow.model.json.JoinRelation;
import demos.dlineage.dataflow.model.json.Relation;
import demos.dlineage.dataflow.model.json.RelationElement;
import demos.dlineage.dataflow.model.xml.argument;
import demos.dlineage.dataflow.model.xml.column;
import demos.dlineage.dataflow.model.xml.dataflow;
import demos.dlineage.dataflow.model.xml.procedure;
import demos.dlineage.dataflow.model.xml.sourceColumn;
import demos.dlineage.dataflow.model.xml.table;
import demos.dlineage.dataflow.model.xml.targetColumn;
import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;

public class SQLFlow {

	public static void main(String[] args) {
		if (args.length < 1) {
			System.out.println(
					"Usage: java SQLFlow [/f <path_to_sql_file>] [/d <path_to_directory_includes_sql_files>] [/r <show_relation_types>] [/s] [/t <database type>] [/o <output file path>]");
			System.out.println("/f: Option, specify the sql file path to analyze sqlflow relation.");
			System.out.println("/d: Option, specify the sql directory path to analyze sqlflow relation.");
			System.out.println(
					"/r: Option, set the relation types, split by comma. Support fdd, frd, frd, fddi, join. The default relation type is fdd.");
			System.out.println("/s: Option, simple output, ignore the intermediate results.");
			System.out.println( "/i: Option, ignore all result sets." );
			System.out.println(
					"/t: Option, set the database type. Support oracle, mysql, mssql, db2, netezza, teradata, informix, sybase, postgresql, hive, greenplum and redshift, the default type is oracle.");
			System.out.println("/o: Option, write the output stream to the specified file.");
			return;
		}

		File sqlFiles = null;

		List<String> argList = Arrays.asList(args);

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

		EDbVendor vendor = EDbVendor.dbvmssql;

		int index = argList.indexOf("/t");

		if (index != -1 && args.length > index + 1) {
			vendor = TGSqlParser.getDBVendorByName(args[index + 1]);
		}
		String outputFile = null;

		index = argList.indexOf("/o");

		if (index != -1 && args.length > index + 1) {
			outputFile = args[index + 1];
		}

		boolean simple = argList.indexOf("/s") != -1;
		boolean ignoreResultSets = argList.indexOf("/i") != -1;

		String showRelationType = "fdd";
		index = argList.indexOf("/r");
		if (index != -1 && args.length > index + 1) {
			showRelationType = args[index + 1];
		}

		JSONObject result = analyzeSQLFlow(sqlFiles, vendor, simple, ignoreResultSets, showRelationType);

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

		System.out.println(JSON.toJSONString(result, true).replaceAll("\t", "    "));

		try {
			if (writer != null) {
				writer.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static JSONObject analyzeSQLFlow(File sqlFiles, EDbVendor vendor, boolean simple, boolean ignoreResultSets, String showRelationType) {
		JSONObject result = new JSONObject();

		List<RelationType> relationTypes = new ArrayList<RelationType>();
		List<String> types = Arrays.asList(showRelationType.split(","));
		for (RelationType relationType : RelationType.values()) {
			for (String type : types) {
				if(relationType.name().equals(type)) {
					relationTypes.add(relationType);
				}
			}
		}

		DataFlowAnalyzer dataFlow = new DataFlowAnalyzer(sqlFiles, vendor,
				simple && relationTypes.contains(RelationType.fdd) && relationTypes.size() == 1);
		dataFlow.setShowJoin(relationTypes.contains(RelationType.join));
		dataFlow.setIgnoreRecordSet(ignoreResultSets);
		StringBuffer errorMessage = new StringBuffer();

		dataFlow.generateDataFlow(errorMessage);
		dataFlow.dispose();

		if (errorMessage.length() > 0) {
			result.put("error", errorMessage.toString().trim());
			if (dataFlow.getDataFlow() == null) {
				result.put("data", new Object());
				return result;
			}
		}

		dataflow dataflowModel = dataFlow.getDataFlow();
		DataFlow model = getSqlflowModel(dataflowModel);
		model.setDbvendor(vendor.name());
		result.put("data", model);
		return result;
	}

	private static DataFlow getSqlflowModel(dataflow dataflow) {
		DataFlow model = new DataFlow();

		List<DBObject> list = new ArrayList<DBObject>();

		List<procedure> procedures = new ArrayList<procedure>();
		if (dataflow.getProcedures() != null) {
			procedures.addAll(dataflow.getProcedures());
		}

		List<table> tables = new ArrayList<table>();
		if (dataflow.getTables() != null) {
			tables.addAll(dataflow.getTables());
		}
		if (dataflow.getViews() != null) {
			tables.addAll(dataflow.getViews());
		}
		if (dataflow.getResultsets() != null) {
			tables.addAll(dataflow.getResultsets());
		}

		if (!procedures.isEmpty()) {
			for (procedure procedure : procedures) {
				DBObject item = new DBObject();
				item.setId(procedure.getId());
				item.setDatabase(procedure.getDatabase());
				item.setSchema(procedure.getSchema());
				item.setName(procedure.getName());
				item.setType("procedure");
				item.setCoordinates(Coordinate.parse(procedure.getCoordinate()));

				List<argument> arguments = procedure.getArguments();
				List<Argument> argumentModels = new ArrayList<Argument>();
				if (arguments != null) {
					for (argument argument : arguments) {
						Argument argumentModel = new Argument();
						argumentModel.setId(argument.getId());
						argumentModel.setName(argument.getName());
						argumentModel.setInout(argument.getInout());
						argumentModel.setDatatype(argument.getDatatype());
						argumentModel.setCoordinates(Coordinate.parse(argument.getCoordinate()));
						argumentModels.add(argumentModel);
					}
				}
				item.setArguments(argumentModels.toArray(new Argument[0]));
				list.add(item);
			}
		}

		if (!tables.isEmpty()) {
			for (table table : tables) {
				DBObject item = new DBObject();
				item.setId(table.getId());
				item.setDatabase(table.getDatabase());
				item.setSchema(table.getSchema());
				item.setAlias(table.getAlias());
				item.setName(table.getName());
				item.setType(table.getType());
				item.setCoordinates(Coordinate.parse(table.getCoordinate()));

				List<column> columns = table.getColumns();
				List<Column> columnModels = new ArrayList<Column>();
				if (columns != null) {
					for (column column : columns) {
						Column columnModel = new Column();
						columnModel.setId(column.getId());
						columnModel.setName(column.getName());
						columnModel.setCoordinates(Coordinate.parse(column.getCoordinate()));
						columnModel.setSource(column.getSource());
						columnModel.setQualifiedTable(column.getQualifiedTable());
						columnModels.add(columnModel);
					}
				}
				item.setColumns(columnModels.toArray(new Column[0]));
				list.add(item);
			}
		}

		model.setDbobjs(list.toArray(new DBObject[0]));

		List<Relation> relations = new ArrayList<Relation>();
		if (dataflow.getRelations() != null) {
			for (demos.dlineage.dataflow.model.xml.relation relation : dataflow.getRelations()) {
				Relation relationModel;
				if (relation.getType().equals("join")) {
					relationModel = new JoinRelation();
					((JoinRelation) relationModel).setCondition(relation.getCondition());
					((JoinRelation) relationModel).setJoinType(relation.getJoinType());
					((JoinRelation) relationModel).setClause(relation.getClause());
				} else {
					relationModel = new Relation();
				}

				relationModel.setId(relation.getId());
				relationModel.setType(relation.getType());
				relationModel.setEffectType(relation.getEffectType());
				relationModel.setFunction(relation.getFunction());

				if (relation.getTarget() != null && relation.getSources() != null && !relation.getSources().isEmpty()) {
					{
						RelationElement targetModel = new RelationElement();
						targetColumn target = relation.getTarget();
						targetModel.setColumn(target.getColumn());
						targetModel.setId(target.getId());
						targetModel.setParentId(target.getParent_id());
						targetModel.setParentName(target.getParent_name());
						targetModel.setCoordinates(Coordinate.parse(target.getCoordinate()));
						targetModel.setFunction(target.getFunction());
						relationModel.setTarget(targetModel);
					}

					List<RelationElement> sourceModels = new ArrayList<RelationElement>();
					for (sourceColumn source : relation.getSources()) {
						RelationElement sourceModel = new RelationElement();
						sourceModel.setColumn(source.getColumn());
						sourceModel.setColumnType(source.getColumn_type());
						sourceModel.setId(source.getId());
						sourceModel.setParentId(source.getParent_id());
						sourceModel.setParentName(source.getParent_name());
						sourceModel.setSourceId(source.getSource_id());
						sourceModel.setSourceName(source.getSource_name());
						sourceModel.setCoordinates(Coordinate.parse(source.getCoordinate()));
						sourceModel.setClauseType(source.getClauseType());
						sourceModels.add(sourceModel);
					}
					relationModel.setSources(sourceModels.toArray(new RelationElement[0]));
					relations.add(relationModel);
				}
			}
		}
		model.setRelations(relations.toArray(new Relation[0]));
		return model;
	}

}
