package demos.sqlenv;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.sqlenv.ESQLDataObjectType;
import gudusoft.gsqlparser.util.Identifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class DatabaseSQLDataSource extends TSQLDataSource {
	private String[] extractedDatabases;
	private String[] excludedDatabases;

	private List<String> systemDatabases = new ArrayList<String>();

	public DatabaseSQLDataSource(EDbVendor vendor, String hostName, String port, String account, String password) {
		super(vendor, hostName, port, account, password);
	}

	public DatabaseSQLDataSource(EDbVendor vendor, String hostName, String port, String account, String password,
			String database) {
		super(vendor, hostName, port, account, password, database);
	}

	public String[] getExtractedDatabases() {
		return extractedDatabases == null ? new String[0] : extractedDatabases;
	}

	public void setExtractedDatabases(String... extractedDatabases) {
		this.extractedDatabases = extractedDatabases;
	}

	public String[] getExcludedDatabases() {
		return excludedDatabases == null ? new String[0] : excludedDatabases;
	}

	public void setExcludedDatabases(String... excludedDatabases) {
		this.excludedDatabases = excludedDatabases;
	}

	public String[] getSystemDatabases() {
		return systemDatabases.toArray(new String[0]);
	}

	public void setSystemDatabases(String... systemDatabases) {
		if (systemDatabases != null) {
			this.systemDatabases.addAll(Arrays.asList(systemDatabases));
		}
	}

	public boolean isSystemDatabase(String databaseName) {
		for (String systemDatabase : getSystemDatabases()) {
			if (new Identifier(getVendor(), ESQLDataObjectType.dotCatalog, systemDatabase)
					.equals(new Identifier(getVendor(), ESQLDataObjectType.dotCatalog, databaseName))) {
				return true;
			}
		}
		return false;
	}
}
