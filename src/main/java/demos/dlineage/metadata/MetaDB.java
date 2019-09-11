package demos.dlineage.metadata;

import gudusoft.gsqlparser.IMetaDatabase;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import demos.dlineage.model.metadata.ColumnMetaData;
import demos.dlineage.model.metadata.TableMetaData;
import demos.dlineage.util.SQLUtil;

public class MetaDB implements IMetaDatabase {

	private String columns[][];
	private boolean strict = false;

	public MetaDB(Map<TableMetaData, List<ColumnMetaData>> metaMap,
			boolean strict) {
		List<String[]> columnList = new ArrayList<String[]>();
		if (metaMap != null) {
			Iterator<TableMetaData> tableIter = metaMap.keySet().iterator();
			while (tableIter.hasNext()) {
				TableMetaData table = tableIter.next();
				List<ColumnMetaData> columnMetadatas = metaMap.get(table);
				for (int i = 0; i < columnMetadatas.size(); i++) {
					ColumnMetaData columnMetadata = columnMetadatas.get(i);
					String[] column = new String[5];
					column[0] = "";
					column[1] = columnMetadata.getTable().getCatalogName();
					column[2] = columnMetadata.getTable().getSchemaName();
					column[3] = columnMetadata.getTable().getName();
					column[4] = columnMetadata.getName();
					columnList.add(column);
				}
			}
		}
		columns = columnList.toArray(new String[columnList.size()][5]);
		this.strict = strict;
	}

	public boolean checkColumn(String server, String database, String schema,
			String table, String column) {
		boolean bServer, bDatabase, bSchema, bTable, bColumn, bRet = false;
		for (int i = 0; i < columns.length; i++) {
			if (strict) {
				if ((server == null) || (server.length() == 0)) {
					bServer = true;
				} else {
					bServer = columns[i][0].equalsIgnoreCase(SQLUtil.trimObjectName(server));
				}
				if (!bServer)
					continue;

				if ((database == null) || (database.length() == 0)) {
					bDatabase = true;
				} else {
					bDatabase = columns[i][1].equalsIgnoreCase(SQLUtil.trimObjectName(database));
				}
				if (!bDatabase)
					continue;

				if ((schema == null) || (schema.length() == 0)) {
					bSchema = true;
				} else {
					bSchema = columns[i][2].equalsIgnoreCase(SQLUtil.trimObjectName(schema));
				}

				if (!bSchema)
					continue;
			}

			bTable = columns[i][3].equalsIgnoreCase(SQLUtil.trimObjectName(table));
			if (!bTable)
				continue;

			bColumn = columns[i][4].equalsIgnoreCase(SQLUtil.trimObjectName(column));
			if (!bColumn)
				continue;

			bRet = true;
			break;

		}

		return bRet;
	}
}
