package demos.dlineage.dataflow.metadata.sqldep;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import demos.dlineage.dataflow.metadata.MetadataAnalyzer;
import demos.dlineage.dataflow.metadata.MetadataReader;
import demos.dlineage.dataflow.metadata.model.MetadataRelation;
import demos.dlineage.dataflow.model.ModelBindingManager;
import demos.dlineage.dataflow.model.RelationType;
import demos.dlineage.dataflow.model.xml.column;
import demos.dlineage.dataflow.model.xml.dataflow;
import demos.dlineage.dataflow.model.xml.procedure;
import demos.dlineage.dataflow.model.xml.relation;
import demos.dlineage.dataflow.model.xml.sourceColumn;
import demos.dlineage.dataflow.model.xml.table;
import demos.dlineage.dataflow.model.xml.targetColumn;
import demos.dlineage.util.Pair3;
import demos.dlineage.util.SQLUtil;
import demos.dlineage.util.XML2Model;
import gudusoft.gsqlparser.EDbVendor;

public class SQLDepMetadataAnalyzer implements MetadataAnalyzer {

	private Map<String, procedure> procedureMap = new LinkedHashMap<>();
	private Map<String, table> tableMap = new LinkedHashMap<>();
	private Map<String, column> columnMap = new LinkedHashMap<>();
	private EDbVendor vendor;

	@Override
	public synchronized dataflow analyzeMetadata(EDbVendor vendor, String metadata) {
		init(vendor);
		List<MetadataRelation> relations = MetadataReader.read(metadata);
		dataflow dataflow = new dataflow();
		appendProcedures(dataflow, relations);
		appendTables(dataflow, relations);
		sortTableColumns(dataflow);
		appendRelations(dataflow, relations);
		return dataflow;
	}

	private void init(EDbVendor vendor) {
		this.vendor = vendor;
		procedureMap.clear();
		tableMap.clear();
		columnMap.clear();
		if (ModelBindingManager.get() == null) {
			ModelBindingManager.set(new ModelBindingManager());
		}
	}

	private void sortTableColumns(dataflow dataflow) {
		if (dataflow.getTables() != null) {
			for (table table : dataflow.getTables()) {
				table.getColumns().sort((t1, t2) -> {
					if (t1.getName().equalsIgnoreCase("PseudoRows"))
						return 1;
					if (t2.getName().equalsIgnoreCase("PseudoRows"))
						return -1;
					return 0;
				});
			}
		}

		if (dataflow.getResultsets() != null) {
			for (table table : dataflow.getResultsets()) {
				table.getColumns().sort((t1, t2) -> {
					if (t1.getName().equalsIgnoreCase("PseudoRows"))
						return 1;
					if (t2.getName().equalsIgnoreCase("PseudoRows"))
						return -1;
					return 0;
				});
			}
		}
	}

	private void appendRelations(dataflow dataflow, List<MetadataRelation> relations) {
		for (MetadataRelation metadataRelation : relations) {
			relation relation = new relation();
			relation.setId(String.valueOf(++ModelBindingManager.get().RELATION_ID));
			if ("SQLDEP-INDIRECT".equalsIgnoreCase(metadataRelation.getSourceColumn())
					|| "SQLDEP-INDIRECT".equalsIgnoreCase(metadataRelation.getTargetColumn())) {
				relation.setType(RelationType.frd.name());
			} else {
				relation.setType(RelationType.fdd.name());
			}

			sourceColumn sourceColumn = new sourceColumn();

			String sourceColumnName = metadataRelation.getSourceColumn();
			if ("SQLDEP-INDIRECT".equalsIgnoreCase(sourceColumnName)) {
				sourceColumnName = "PseudoRows";
			}
			sourceColumn.setCoordinate(new Pair3<Long, Long, String>(-1L, -1L, ModelBindingManager.getGlobalHash())
					+ "," + new Pair3<Long, Long, String>(-1L, -1L, ModelBindingManager.getGlobalHash()));
			sourceColumn.setColumn(sourceColumnName);
			String sourceParentFullName = getFullName(checkDefault(metadataRelation.getSourceDb()),
					checkDefault(metadataRelation.getSourceSchema()), getTableName(metadataRelation.getSourceTable()));
			String sourceColumnFullName = getFullName(checkDefault(metadataRelation.getSourceDb()),
					checkDefault(metadataRelation.getSourceSchema()), getTableName(metadataRelation.getSourceTable()),
					sourceColumnName);
			String sourceColumnKey = SQLUtil.getIdentifierNormalName(vendor, sourceColumnFullName);
			String sourceParentKey = SQLUtil.getIdentifierNormalName(vendor, sourceParentFullName);

			if ("SQLDEP-CONSTANT".equalsIgnoreCase(metadataRelation.getSourceColumn())) {
				sourceColumn.setColumn_type("constant");
				sourceColumn.setId(String.valueOf(++ModelBindingManager.get().TABLE_COLUMN_ID));
			} else {
				sourceColumn.setId(columnMap.get(sourceColumnKey).getId());
				sourceColumn.setParent_name(sourceParentFullName);
				sourceColumn.setParent_id(tableMap.get(sourceParentKey).getId());
			}

			relation.getSources().add(sourceColumn);
			targetColumn targetColumn = new targetColumn();

			String targetColumnName = metadataRelation.getTargetColumn();
			if ("SQLDEP-INDIRECT".equalsIgnoreCase(targetColumnName)) {
				targetColumnName = "PseudoRows";
			}

			targetColumn.setColumn(targetColumnName);
			targetColumn.setCoordinate(new Pair3<Long, Long, String>(-1L, -1L, ModelBindingManager.getGlobalHash())
					+ "," + new Pair3<Long, Long, String>(-1L, -1L, ModelBindingManager.getGlobalHash()));
			String targetParentFullName = getFullName(checkDefault(metadataRelation.getTargetDb()),
					checkDefault(metadataRelation.getTargetSchema()), getTableName(metadataRelation.getTargetTable()));
			String targetColumnFullName = getFullName(checkDefault(metadataRelation.getTargetDb()),
					checkDefault(metadataRelation.getTargetSchema()), getTableName(metadataRelation.getTargetTable()),
					targetColumnName);
			String targetColumnKey = SQLUtil.getIdentifierNormalName(vendor, targetColumnFullName);
			String targetParentKey = SQLUtil.getIdentifierNormalName(vendor, targetParentFullName);

			if ("SQLDEP-CONSTANT".equalsIgnoreCase(metadataRelation.getTargetColumn())) {
				targetColumn.setId(String.valueOf(++ModelBindingManager.get().TABLE_COLUMN_ID));
			} else {
				targetColumn.setId(columnMap.get(targetColumnKey).getId());
				targetColumn.setParent_name(targetParentFullName);
				targetColumn.setParent_id(tableMap.get(targetParentKey).getId());
			}

			relation.setTarget(targetColumn);
			dataflow.getRelations().add(relation);
		}
	}

	private void appendTables(dataflow dataflow, List<MetadataRelation> relations) {
		for (MetadataRelation relation : relations) {
			if (!"SQLDEP-CONSTANT".equalsIgnoreCase(relation.getSourceColumn())) {
				appendTable(dataflow, checkDefault(relation.getSourceDb()), checkDefault(relation.getSourceSchema()),
						getTableName(relation.getSourceTable()), relation.getSourceColumn());
			}

			if (!"SQLDEP-CONSTANT".equalsIgnoreCase(relation.getTargetColumn())) {
				appendTable(dataflow, checkDefault(relation.getTargetDb()), checkDefault(relation.getTargetSchema()),
						getTableName(relation.getTargetTable()), relation.getTargetColumn());
			}
		}
	}

	private String getTableName(String tableName) {
		return tableName.replaceAll("//s*//(.+//)", "");
	}

	private void appendTable(dataflow dataflow, String databaseName, String schemaName, String tableName,
			String columnName) {
		String tableKey = SQLUtil.getIdentifierNormalName(vendor, getFullName(databaseName, schemaName, tableName));
		if (!tableMap.containsKey(tableKey)) {
			table table = new table();
			table.setDatabase(databaseName);
			table.setSchema(schemaName);
			table.setName(tableName);
			table.setId(String.valueOf(++ModelBindingManager.get().TABLE_COLUMN_ID));
			table.setCoordinate(new Pair3<Long, Long, String>(-1L, -1L, ModelBindingManager.getGlobalHash()) + ","
					+ new Pair3<Long, Long, String>(-1L, -1L, ModelBindingManager.getGlobalHash()));
			if (tableName.toUpperCase().indexOf("-RES") == -1) {
				table.setType("table");
				dataflow.getTables().add(table);
			} else {
				table.setType("select_list");
				dataflow.getResultsets().add(table);
			}
			tableMap.put(tableKey, table);
		}

		table table = tableMap.get(tableKey);

		if ("SQLDEP-INDIRECT".equalsIgnoreCase(columnName)) {
			columnName = "PseudoRows";
		}
		String sourceColumnKey = SQLUtil.getIdentifierNormalName(vendor,
				getFullName(databaseName, schemaName, tableName, columnName));
		if (!columnMap.containsKey(sourceColumnKey)) {
			column column = new column();
			column.setId(String.valueOf(++ModelBindingManager.get().TABLE_COLUMN_ID));
			column.setName(columnName);
			if ("PseudoRows".equalsIgnoreCase(columnName)) {
				column.setSource("system");
			}
			column.setCoordinate(new Pair3<Long, Long, String>(-1L, -1L, ModelBindingManager.getGlobalHash()) + ","
					+ new Pair3<Long, Long, String>(-1L, -1L, ModelBindingManager.getGlobalHash()));
			table.getColumns().add(column);
			columnMap.put(sourceColumnKey, column);
		}
	}

	private String checkDefault(String value) {
		if (value.toUpperCase().indexOf("DEFAULT") != -1) {
			value = value.replaceAll("(?i)DEFAULT\\.", "").replaceAll("(?i)DEFAULT", "");
		}
		if (SQLUtil.isEmpty(value))
			return null;
		return value;
	}

	private String getFullName(String... segments) {
		return String.join(".", segments).replace("null.", "");
	}

	private void appendProcedures(dataflow dataflow, List<MetadataRelation> relations) {
		for (MetadataRelation relation : relations) {
			if (!SQLUtil.isEmpty(relation.getProcedureName())) {
				String produceName = SQLUtil.getIdentifierNormalName(vendor, relation.getProcedureName());
				if (!procedureMap.containsKey(produceName)) {
					procedure procedure = new procedure();
					procedure.setName(relation.getProcedureName());
					procedure.setId(String.valueOf(++ModelBindingManager.get().TABLE_COLUMN_ID));
					procedure.setCoordinate(new Pair3<Long, Long, String>(-1L, -1L, ModelBindingManager.getGlobalHash())
							+ "," + new Pair3<Long, Long, String>(-1L, -1L, ModelBindingManager.getGlobalHash()));
					procedureMap.put(produceName, procedure);
					dataflow.getProcedures().add(procedure);
				}
			}
		}
	}

	public static void main(String[] args) {
		dataflow dataflow = new SQLDepMetadataAnalyzer().analyzeMetadata(EDbVendor.dbvmssql,
				SQLUtil.getFileContent("D:\\5.txt"));
		System.out.println(XML2Model.saveXML(dataflow));
	}

}
