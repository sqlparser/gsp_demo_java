package demos.sqlenv;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.sqlenv.ESQLDataObjectType;
import gudusoft.gsqlparser.util.Identifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class DbSchemaSQLDataSource extends TSQLDataSource {
	private String[] extractedDbsSchemas;
	private String[] excludedDbsSchemas;

	private List<String> systemDbsSchemas = new ArrayList<String>();

	public DbSchemaSQLDataSource(EDbVendor vendor, String hostName, String port, String account, String password) {
		super(vendor, hostName, port, account, password);
	}

	public DbSchemaSQLDataSource(EDbVendor vendor, String hostName, String port, String account, String password,
			String database) {
		super(vendor, hostName, port, account, password, database);
	}

	public String[] getExtractedDbsSchemas() {
		return extractedDbsSchemas == null ? new String[0] : extractedDbsSchemas;
	}

	public void setExtractedDbsSchemas(String... extractedDbsSchemas) {
		List<String> dbschemas = new ArrayList<String>();
		if (extractedDbsSchemas != null && extractedDbsSchemas.length > 0) {
			for (String dbschema : extractedDbsSchemas) {
				dbschema = dbschema.replace("/", ".").replace(".*", "").trim();
				if (dbschema.length() > 0) {
					dbschemas.add(dbschema);
				}
			}
			if(dbschemas.size()>0){
				this.extractedDbsSchemas = dbschemas.toArray(new String[0]);
			}
		}
	}

	public String[] getExcludedDbsSchemas() {
		return excludedDbsSchemas == null ? new String[0] : excludedDbsSchemas;
	}

	public void setExcludedDbsSchemas(String... excludedDbsSchemas) {
		List<String> dbschemas = new ArrayList<String>();
		if (excludedDbsSchemas != null && excludedDbsSchemas.length > 0) {
			for (String dbschema : excludedDbsSchemas) {
				dbschema = dbschema.replace("/", ".").replace(".*", "").trim();
				if (dbschema.length() > 0) {
					dbschemas.add(dbschema);
				}
			}
			if(dbschemas.size()>0){
				this.excludedDbsSchemas = dbschemas.toArray(new String[0]);
			}
		}
	}

	public boolean isSystemDbsSchemas(String databaseSchemaName) {
		databaseSchemaName = databaseSchemaName.replace("/", ".");
		int index = databaseSchemaName.indexOf(".");
		if (index == -1) {
			for (String systemDatabase : systemDbsSchemas) {
				if (new Identifier(getVendor(), ESQLDataObjectType.dotCatalog, systemDatabase)
						.equals(new Identifier(getVendor(), ESQLDataObjectType.dotCatalog, databaseSchemaName))) {
					return true;
				}
			}
		} else {
			String[] splits = databaseSchemaName.split("\\.");
			String database = splits[0];
			String schema = splits[1];
			for (String systemDatabase : systemDbsSchemas) {
				if (new Identifier(getVendor(), ESQLDataObjectType.dotSchema, systemDatabase.replace("/", "."))
						.equals(new Identifier(getVendor(), ESQLDataObjectType.dotSchema, databaseSchemaName.replace("/", ".")))) {
					return true;
				}
				if (new Identifier(getVendor(), ESQLDataObjectType.dotSchema, systemDatabase.replace("/", "."))
						.equals(new Identifier(getVendor(), ESQLDataObjectType.dotSchema, ("*/" + schema).replace("/", ".")))) {
					return true;
				}
				if (new Identifier(getVendor(), ESQLDataObjectType.dotCatalog, systemDatabase.replace("/", "."))
						.equals(new Identifier(getVendor(), ESQLDataObjectType.dotCatalog, database.replace("/", ".")))) {
					return true;
				}
			}
		}
		return false;
	}

	public String[] getSystemDbsSchemas() {
		return systemDbsSchemas.toArray(new String[0]);
	}

	public void setSystemDbsSchemas(String... systemDatabases) {
		if (systemDatabases != null) {
			this.systemDbsSchemas.addAll(Arrays.asList(systemDatabases));
		}
	}
}
