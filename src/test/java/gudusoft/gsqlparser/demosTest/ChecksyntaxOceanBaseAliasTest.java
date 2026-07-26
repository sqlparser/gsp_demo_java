package gudusoft.gsqlparser.demosTest;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.EOBTenantMode;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.demos.checksyntax.checksyntax;
import junit.framework.TestCase;

/**
 * Regression tests that pin the OceanBase tenant-mode aliases accepted by the
 * checksyntax /t argument. Covers US-004 of the OceanBase documented syntax
 * gaps PRD.
 */
public class ChecksyntaxOceanBaseAliasTest extends TestCase {

    // ---------- resolveOBTenantMode / stripOBTenantSuffix -------------------

    public void test_plain_oceanbase_defaults_to_mysql_tenant() {
        assertEquals(EOBTenantMode.MYSQL, checksyntax.resolveOBTenantMode("oceanbase"));
        assertEquals("oceanbase", checksyntax.stripOBTenantSuffix("oceanbase"));
    }

    public void test_oceanbase_mysql_alias_is_mysql_tenant() {
        assertEquals(EOBTenantMode.MYSQL, checksyntax.resolveOBTenantMode("oceanbase-mysql"));
        assertEquals("oceanbase", checksyntax.stripOBTenantSuffix("oceanbase-mysql"));
    }

    public void test_oceanbase_oracle_alias_is_oracle_tenant() {
        assertEquals(EOBTenantMode.ORACLE, checksyntax.resolveOBTenantMode("oceanbase-oracle"));
        assertEquals("oceanbase", checksyntax.stripOBTenantSuffix("oceanbase-oracle"));
    }

    public void test_oceanbase_system_alias_is_system_tenant() {
        assertEquals(EOBTenantMode.SYSTEM, checksyntax.resolveOBTenantMode("oceanbase-system"));
        assertEquals("oceanbase", checksyntax.stripOBTenantSuffix("oceanbase-system"));
    }

    public void test_ob_short_alias_variants() {
        assertEquals(EOBTenantMode.ORACLE, checksyntax.resolveOBTenantMode("ob-oracle"));
        assertEquals("ob", checksyntax.stripOBTenantSuffix("ob-oracle"));

        assertEquals(EOBTenantMode.SYSTEM, checksyntax.resolveOBTenantMode("ob-system"));
        assertEquals("ob", checksyntax.stripOBTenantSuffix("ob-system"));

        assertEquals(EOBTenantMode.MYSQL, checksyntax.resolveOBTenantMode("ob"));
        assertEquals("ob", checksyntax.stripOBTenantSuffix("ob"));
    }

    public void test_alias_lookup_is_case_insensitive() {
        assertEquals(EOBTenantMode.ORACLE, checksyntax.resolveOBTenantMode("OceanBase-Oracle"));
        assertEquals("oceanbase", checksyntax.stripOBTenantSuffix("OceanBase-Oracle"));
    }

    public void test_non_oceanbase_vendor_passes_through_unchanged() {
        assertEquals(EOBTenantMode.MYSQL, checksyntax.resolveOBTenantMode("oracle"));
        assertEquals("oracle", checksyntax.stripOBTenantSuffix("oracle"));

        assertEquals(EOBTenantMode.MYSQL, checksyntax.resolveOBTenantMode("mysql"));
        assertEquals("mysql", checksyntax.stripOBTenantSuffix("mysql"));
    }

    public void test_getDBVendorByName_resolves_all_stripped_aliases_to_dbvoceanbase() {
        for (String alias : new String[]{
                "oceanbase",
                "oceanbase-mysql",
                "oceanbase-oracle",
                "oceanbase-system",
                "ob",
                "ob-mysql",
                "ob-oracle",
                "ob-system"
        }) {
            EDbVendor v = TGSqlParser.getDBVendorByName(checksyntax.stripOBTenantSuffix(alias));
            assertEquals("alias '" + alias + "' should resolve to dbvoceanbase",
                    EDbVendor.dbvoceanbase, v);
        }
    }

    // ---------- end-to-end parse behavior with the resolved mode ------------

    /**
     * Helper that mirrors checksyntax.main(): resolve /t, create parser, set mode,
     * parse, return the int result.
     */
    private int checkSyntax(String typeArg, String sql) {
        EDbVendor vendor = TGSqlParser.getDBVendorByName(checksyntax.stripOBTenantSuffix(typeArg));
        EOBTenantMode mode = checksyntax.resolveOBTenantMode(typeArg);
        TGSqlParser parser = new TGSqlParser(vendor);
        if (vendor == EDbVendor.dbvoceanbase) {
            parser.setOBTenantMode(mode);
        }
        parser.sqltext = sql;
        return parser.parse();
    }

    public void test_oceanbase_oracle_parses_merge_into() {
        String sql = "MERGE INTO t USING s ON (t.id=s.id) WHEN MATCHED THEN UPDATE SET t.x=1";
        assertEquals("MERGE should parse in oceanbase-oracle mode",
                0, checkSyntax("oceanbase-oracle", sql));
    }

    public void test_oceanbase_oracle_parses_select_from_dual() {
        assertEquals(0, checkSyntax("oceanbase-oracle", "SELECT 1 FROM dual"));
    }

    public void test_oceanbase_mysql_default_parses_show_tables() {
        assertEquals(0, checkSyntax("oceanbase", "SHOW TABLES"));
    }

    public void test_oceanbase_system_parses_show_tenant() {
        assertEquals(0, checkSyntax("oceanbase-system", "SHOW TENANT"));
    }

    public void test_oceanbase_system_parses_create_resource_pool() {
        String sql =
                "CREATE RESOURCE POOL p_app UNIT = 'u_large', UNIT_NUM = 2";
        assertEquals(0, checkSyntax("oceanbase-system", sql));
    }

    /**
     * OceanBase MySQL mode supports MERGE INTO as an OceanBase extension
     * (not standard MySQL, but documented OceanBase syntax).
     */
    public void test_oceanbase_default_accepts_merge() {
        String sql = "MERGE INTO t USING s ON (t.id=s.id) WHEN MATCHED THEN UPDATE SET t.x=1";
        assertEquals("MySQL-mode OB should accept MERGE (OceanBase extension)",
                0, checkSyntax("oceanbase", sql));
    }
}
