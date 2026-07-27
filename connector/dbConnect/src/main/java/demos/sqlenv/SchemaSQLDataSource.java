package demos.sqlenv;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.sqlenv.ESQLDataObjectType;
import gudusoft.gsqlparser.util.Identifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class SchemaSQLDataSource extends TSQLDataSource {
	private String[] extractedSchemas;
	private String[] excludedSchemas;

	private List<String> systemSchemas = new ArrayList<String>();

	public SchemaSQLDataSource(EDbVendor vendor, String hostName, String port, String account, String password) {
		super(vendor, hostName, port, account, password);
	}

	public SchemaSQLDataSource(EDbVendor vendor, String hostName, String port, String account, String password,
			String database) {
		super(vendor, hostName, port, account, password, database);
	}

	public String[] getExtractedSchemas() {
		return extractedSchemas == null ? new String[0] : extractedSchemas;
	}

	public void setExtractedSchemas(String... extractedSchemas) {
		this.extractedSchemas = extractedSchemas;
	}

	public String[] getExcludedSchemas() {
		return excludedSchemas == null ? new String[0] : excludedSchemas;
	}

	public void setExcludedSchemas(String... excludedSchemas) {
		this.excludedSchemas = excludedSchemas;
	}

	public String[] getSystemSchemas() {
		return systemSchemas.toArray(new String[0]);
	}

	public void setSystemSchemas(String... systemSchemas) {
		if (systemSchemas != null) {
			this.systemSchemas.addAll(Arrays.asList(systemSchemas));
		}
	}

	public boolean isSystemSchema(String schemaName) {
		for (String systemSchema : getSystemSchemas()) {
			if (new Identifier(getVendor(), ESQLDataObjectType.dotSchema, systemSchema)
					.equals(new Identifier(getVendor(), ESQLDataObjectType.dotSchema, schemaName))) {
				return true;
			}
		}
		return false;
	}
}
