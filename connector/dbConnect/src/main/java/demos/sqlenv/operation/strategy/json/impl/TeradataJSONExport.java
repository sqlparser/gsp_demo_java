package demos.sqlenv.operation.strategy.json.impl;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.sqlenv.ESQLDataObjectType;
import demos.sqlenv.TSQLDataSource;
import demos.sqlenv.metadata.Metadata;
import demos.sqlenv.metadata.model.Column;
import demos.sqlenv.metadata.model.Database;
import demos.sqlenv.metadata.model.MetadataItem;
import demos.sqlenv.metadata.model.Table;
import demos.sqlenv.operation.strategy.json.AbstractJSONExport;
import gudusoft.gsqlparser.sqlenv.util.SQLFileUtil;
import gudusoft.gsqlparser.util.CollectionUtil;
import gudusoft.gsqlparser.util.Identifier;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class TeradataJSONExport extends AbstractJSONExport {
	@Override
	protected String dbLinksSql(TSQLDataSource datasource, String database) {
		return null;
	}

	@Override
	protected String synonymsSql(TSQLDataSource datasource, String database, String catalog) {
		return null;
	}

	@Override
	protected String databasesSql(TSQLDataSource datasource) {
		return SQLFileUtil.readSqlContent(datasource.getDbCategory(), "database.sql");
	}

	@Override
	protected String tablesSql(TSQLDataSource datasource, String database, String catalog) {
		return SQLFileUtil.readSqlContent(datasource.getDbCategory(), "schema_table.sql").replace("%s", new Identifier(EDbVendor.dbvteradata, ESQLDataObjectType.dotCatalog, catalog)
				.getNormalizeIdentifier());
	}

	protected String viewsSql(TSQLDataSource datasource, String database, String catalog) {
		return SQLFileUtil.readSqlContent(datasource.getDbCategory(), "schema_view.sql").replace("%s", new Identifier(EDbVendor.dbvteradata, ESQLDataObjectType.dotCatalog, catalog)
				.getNormalizeIdentifier());
	}

	@Override
	protected String queriesSql(TSQLDataSource datasource, String database, String catalog) {
		return SQLFileUtil.readSqlContent(datasource.getDbCategory(), "query.sql").replace("%s", new Identifier(EDbVendor.dbvteradata, ESQLDataObjectType.dotCatalog, catalog)
				.getNormalizeIdentifier());
	}

	@Override
	protected String queriesPSql(TSQLDataSource datasource, String database, String catalog) {
		return null;
	}
	
	protected List<String> exportDatabases(TSQLDataSource datasource, Statement statement,
			Metadata exportMetadataModel) {
		try {
			String databasesSql = databasesSql(datasource);
			ResultSet resultSet = statement.executeQuery(databasesSql);
			List<String> dbNames = new ArrayList<String>();
			while (resultSet.next()) {
				dbNames.add("\"" + resultSet.getString(1).trim() + "\"");
			}
			return dbNames;
		} catch (Exception e) {
			if (e.getMessage() != null) {
				exportMetadataModel.appendErrorMessage(e.getMessage());
			}
			e.printStackTrace();
			return null;
		}
	}

	protected void exportTables(TSQLDataSource datasource, String database, String catalog,
			Metadata exportMetadataModel) {
		super.exportTables(datasource, database, catalog, exportMetadataModel);

		Statement statement = null;
		Connection connection = null;
		try {
			connection = datasource.getConnection();
			connection.setCatalog(new Identifier(datasource.getVendor(), ESQLDataObjectType.dotCatalog, catalog)
					.getNormalizeIdentifier());
			statement = connection.createStatement();
			Database sqlDatabase = new Database();
			sqlDatabase.setName(database);
			List<Table> tables = new CopyOnWriteArrayList<Table>();
			String viewsSql = viewsSql(datasource, database, catalog);
			ResultSet resultSet = statement.executeQuery(viewsSql);
			List<MetadataItem> metadataItems = new ArrayList<MetadataItem>();
			while (resultSet.next()) {
				MetadataItem item = fetchMetadataItem(resultSet, exportMetadataModel);
				if (!acceptSchema(datasource, item.getDatabaseColumn(), item.getSchemaColumn())) {
					continue;
				}
				metadataItems.add(item);
			}
			close(resultSet);
			if (!CollectionUtil.isEmpty(metadataItems)) {
				Map<String, List<MetadataItem>> listMap = new LinkedHashMap<String, List<MetadataItem>>();
				for (MetadataItem v : metadataItems) {
					String key = v.getTableNameColumn() + v.getSchemaColumn();
					if (!listMap.containsKey(key)) {
						listMap.put(key, new ArrayList<MetadataItem>());
					}
					listMap.get(key).add(v);
				}

				for (Map.Entry<String, List<MetadataItem>> entry : listMap.entrySet()) {
					try {
						List<MetadataItem> v = entry.getValue();
						Table table = new Table();
						MetadataItem sqlResultSet = v.get(0);
						table.setDatabase(sqlResultSet.getDatabaseColumn());
						table.setSchema(sqlResultSet.getSchemaColumn());
						table.setName(sqlResultSet.getTableNameColumn());
						table.setIsView(sqlResultSet.getIsViewColumn());

						statement = connection.createStatement();
						resultSet = statement.executeQuery(
								String.format("HELP COLUMN %s.%s.*;", table.getDatabase(), table.getName()));
						Map<String, String> viewColumns = new HashMap<String, String>();
						while (resultSet.next()) {
							MetadataItem item = fetchMetadataViewColumn(resultSet, exportMetadataModel);
							viewColumns.put(item.getColumnNameColumn(), item.getColumnDataTypeColumn());
						}
						close(resultSet);

						List<Column> columns = new ArrayList<Column>();
						for (MetadataItem value : v) {
							Column column = new Column();
							column.setName(value.getColumnNameColumn());
							column.setDataType(viewColumns.get(column.getName()));
							column.setComment(value.getColumnCommentColumn());
							columns.add(column);
						}
						table.setColumns(columns);
						tables.add(table);
					} catch (Exception e) {
						if (e.getMessage() != null) {
							exportMetadataModel.appendErrorMessage(e.getMessage());
						}
						e.printStackTrace();
					}
				}
			}
			sqlDatabase.setTables(tables);
			appendDatabase(exportMetadataModel, datasource, sqlDatabase);
		} catch (Exception e) {
			if (e.getMessage() != null) {
				exportMetadataModel.appendErrorMessage(e.getMessage());
			}
			e.printStackTrace();
		} finally {
			close(statement);
		}
	}

	private MetadataItem fetchMetadataViewColumn(ResultSet resultSet,
			Metadata exportMetadataModel) {
		MetadataItem result = new MetadataItem();
		try {
			result.setColumnNameColumn(detectResult(1, resultSet));
			String type = detectResult(2, resultSet);
			String columnLength = detectResult(5, resultSet);
			String decimalTotalDigits = detectResult("Decimal Total Digits", resultSet);
			String decimalFractionalDigits = detectResult("Decimal Fractional Digits", resultSet);
			String charType = detectResult("Char Type", resultSet);
			String dataType = computeDataType(type, columnLength, decimalTotalDigits, decimalFractionalDigits,
					charType);
			result.setColumnDataTypeColumn(dataType);
		} catch (Exception e) {
			if (e.getMessage() != null) {
				exportMetadataModel.appendErrorMessage(e.getMessage());
			}
			e.printStackTrace();
		}
		return result;
	}

	private String computeDataType(String type, String columnLength, String decimalTotalDigits,
			String decimalFractionalDigits, String charType) {
		StringBuilder buffer = new StringBuilder();
		switch (type) {
		case "AT":
			buffer.append("time");
			break;
		case "BF":
			buffer.append("byte");
			break;
		case "BO":
			buffer.append("blob");
			break;
		case "BV":
			buffer.append("varbyte");
			break;
		case "CF":
			buffer.append("char");
			break;
		case "CO":
			buffer.append("clob");
			break;
		case "CV":
			buffer.append("varchar");
			break;
		case "D":
			buffer.append("decimal");
			break;
		case "DA":
			buffer.append("date");
			break;
		case "DH":
			buffer.append("interval day to hour");
			break;
		case "DM":
			buffer.append("interval day to minute");
			break;
		case "DS":
			buffer.append("interval day to second");
			break;
		case "DY":
			buffer.append("interval day");
			break;
		case "F":
			buffer.append("float");
			break;
		case "HM":
			buffer.append("interval hour to minute");
			break;
		case "HR":
			buffer.append("interval hour");
			break;
		case "HS":
			buffer.append("interval hour to second");
			break;
		case "I1":
			buffer.append("byteint");
			break;
		case "I2":
			buffer.append("smallint");
			break;
		case "I8":
			buffer.append("bigint");
			break;
		case "I":
			buffer.append("int");
			break;
		case "MI":
			buffer.append("interval minute");
			break;
		case "MO":
			buffer.append("interval month");
			break;
		case "MS":
			buffer.append("interval minute to second");
			break;
		case "N":
			buffer.append("number");
			break;
		case "PD":
			buffer.append("period(date)");
			break;
		case "PS":
			buffer.append("period(timestamp(");
			break;
		case "PT":
			buffer.append("period(time(");
			break;
		case "SC":
			buffer.append("interval second");
			break;
		case "SZ":
			buffer.append("timestamp with time zone");
			break;
		case "TS":
			buffer.append("timestamp");
			break;
		case "TZ":
			buffer.append("time with time zone");
			break;
		case "YI":
			buffer.append("interval year");
			break;
		case "YM":
			buffer.append("interval year to month");
			break;
		}

		switch (type) {
		case "BF":
		case "BV":
			buffer.append("(").append(columnLength).append(")");
			break;
		case "CF":
		case "CV":
			buffer.append("(").append(columnLength).append(") character set ").append(getCharSet(charType));
			break;
		case "AT":
		case "TS":
			buffer.append("(").append(decimalFractionalDigits).append(")");
			break;
		case "PS":
		case "PT":
			buffer.append("(").append(decimalFractionalDigits).append("))");
			break;
		case "D":
			buffer.append("(").append(decimalTotalDigits).append(",").append(decimalFractionalDigits).append(")");
			break;
		}

		return buffer.toString();
	}

	private String getCharSet(String charType) {
		switch (charType) {
		case "1":
			return "latin";
		case "2":
			return "unicode";
		default:
			return "";
		}
	}
}
