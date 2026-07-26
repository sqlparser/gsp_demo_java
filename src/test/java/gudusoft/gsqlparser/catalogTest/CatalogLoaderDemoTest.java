package gudusoft.gsqlparser.catalogTest;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.EResolverType;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.catalog.diagnostic.CatalogException;
import gudusoft.gsqlparser.catalog.input.CatalogInputKind;
import gudusoft.gsqlparser.catalog.input.CatalogInputReader;
import gudusoft.gsqlparser.catalog.input.CatalogInputReaders;
import gudusoft.gsqlparser.catalog.input.CatalogInputSource;
import gudusoft.gsqlparser.catalog.input.CatalogInputSources;
import gudusoft.gsqlparser.catalog.input.CatalogLoadOptions;
import gudusoft.gsqlparser.catalog.input.CatalogLoadResult;
import gudusoft.gsqlparser.catalog.input.CatalogLoaders;
import gudusoft.gsqlparser.catalog.input.CatalogLoadingMode;
import gudusoft.gsqlparser.catalog.input.model.UnifiedCatalogModel;
import gudusoft.gsqlparser.catalog.runtime.CatalogProvider;
import gudusoft.gsqlparser.catalog.runtime.CatalogProviderConfig;
import gudusoft.gsqlparser.catalog.runtime.CatalogProviderId;
import gudusoft.gsqlparser.catalog.runtime.CatalogQualifiedName;
import gudusoft.gsqlparser.catalog.runtime.CatalogQuery;
import gudusoft.gsqlparser.catalog.runtime.CatalogRuntime;
import gudusoft.gsqlparser.catalog.runtime.CatalogSnapshot;
import gudusoft.gsqlparser.catalog.runtime.ModelBackedCatalogProvider;
import gudusoft.gsqlparser.resolver2.TSQLResolver2;
import gudusoft.gsqlparser.resolver2.TSQLResolver2ResultFormatter;
import gudusoft.gsqlparser.resolver2.TSQLResolverConfig;
import gudusoft.gsqlparser.sqlenv.TDDLSQLEnv;
import gudusoft.gsqlparser.sqlenv.TSQLEnv;
import gudusoft.gsqlparser.sqlenv.compat.CatalogRuntimeToSQLEnvBridge;
import gudusoft.gsqlparser.sqlenv.compat.LazyCatalogSqlEnv;
import gudusoft.gsqlparser.sqlenv.compat.SqlEnvCatalogBridge;
import junit.framework.TestCase;

import java.util.HashSet;
import java.util.Set;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * End-to-end demo from {@code gsp_demo_java} that exercises the design §5.1, §5.2, §5.3
 * usage shapes against a real {@link TGSqlParser}. The catalog manifest is
 * {@code src/test/resources/catalog/hr-oracle.manifest.json}; the SQL parses through the
 * eager bridge to confirm {@code HR.EMPLOYEES} resolves and {@code EMPLOYEE_ID} is linked.
 *
 * <p>Plan §13.4 / §17 acceptance step 6.</p>
 */
public class CatalogLoaderDemoTest extends TestCase {

    private static final String SQL =
        "SELECT e.employee_id, e.last_name, d.department_name "
            + "FROM employees e JOIN departments d ON e.department_id = d.department_id";

    private Path manifestPath() throws Exception {
        URL url = getClass().getResource("/catalog/hr-oracle.manifest.json");
        assertNotNull("manifest must exist on classpath", url);
        return Paths.get(url.toURI());
    }

    private Path multiSchemaManifestPath() throws Exception {
        URL url = getClass().getResource("/catalog/multi-schema-oracle.manifest.json");
        assertNotNull("multi-schema manifest must exist on classpath", url);
        return Paths.get(url.toURI());
    }

    private CatalogLoadOptions oracleOptions() {
        return CatalogLoadOptions.builder()
            .vendor(EDbVendor.dbvoracle)
            .defaultCatalog("ORCL")
            .defaultSchema("HR")
            .loadingMode(CatalogLoadingMode.EAGER)
            .normalizeOnLoad(true)
            .build();
    }

    /** Design §5.1: multi-step (reader.read() → loadToSQLEnv). */
    public void testDesign_5_1_MultiStep() throws Exception {
        CatalogLoadOptions options = oracleOptions();
        CatalogInputSource source = CatalogInputSources.fromPath(
            manifestPath(), CatalogInputKind.JSON_MANIFEST);
        CatalogInputReader reader = CatalogInputReaders.forSource(source, options);
        UnifiedCatalogModel model = reader.read(source, options);

        TSQLEnv env = SqlEnvCatalogBridge.from(model, options);
        String resolved = parseAndFormat(env, SQL);
        assertResolvedHrEmployeesAndDepartments(resolved);
    }

    /** Design §5.2: one-step convenience CatalogLoaders.loadToSQLEnv. */
    public void testDesign_5_2_OneStep() throws Exception {
        CatalogInputSource source = CatalogInputSources.fromPath(
            manifestPath(), CatalogInputKind.JSON_MANIFEST);
        TSQLEnv env = CatalogLoaders.loadToSQLEnv(source, oracleOptions());
        String resolved = parseAndFormat(env, SQL);
        assertResolvedHrEmployeesAndDepartments(resolved);
    }

    /** Design §5.3: load into an existing user-managed TSQLEnv. */
    public void testDesign_5_3_LoadIntoExistingEnv() throws Exception {
        CatalogLoadOptions options = oracleOptions();
        CatalogInputSource source = CatalogInputSources.fromPath(
            manifestPath(), CatalogInputKind.JSON_MANIFEST);
        UnifiedCatalogModel model = CatalogInputReaders.forSource(source, options)
            .read(source, options);

        TSQLEnv env = new TDDLSQLEnv(null, null, null, EDbVendor.dbvoracle, null);
        CatalogLoadResult result = CatalogLoaders.loadIntoSQLEnv(env, model, options);
        assertTrue("load should be ok: " + result.diagnostics(), result.ok());

        String resolved = parseAndFormat(env, SQL);
        assertResolvedHrEmployeesAndDepartments(resolved);
    }

    /**
     * Plan §17 step 6 acceptance: parse {@code SELECT employee_id FROM employees} and
     * confirm the resolved table is {@code HR.EMPLOYEES} and the resolved column is
     * {@code EMPLOYEE_ID}. The defaults take care of the unqualified names.
     */
    public void testPlanAcceptanceUnqualifiedSelectResolvesToHrEmployees() throws Exception {
        CatalogInputSource source = CatalogInputSources.fromPath(
            manifestPath(), CatalogInputKind.JSON_MANIFEST);
        TSQLEnv env = CatalogLoaders.loadToSQLEnv(source, oracleOptions());

        String resolved = parseAndFormat(env, "SELECT employee_id FROM employees");
        // Plan §17 step 6 requires the resolved field to read as the table linked to
        // its column, not just substring presence on the broader output (codex Round
        // 5 finding 5). Look for the exact "table.column" form in the Fields: block.
        // The formatter normalizes display case independent of vendor-folding rules,
        // so we match case-insensitively.
        String lower = resolved.toLowerCase();
        assertTrue("Expected resolved table EMPLOYEES, got:\n" + resolved,
            hasLine(lower, "employees"));
        assertTrue("Expected resolved field employees.employee_id, got:\n" + resolved,
            hasLine(lower, "employees.employee_id"));
    }

    /**
     * Plan §17 step 6 second half — same demo through {@link LazyCatalogSqlEnv}
     * produces identical output without touching unreferenced schemas. The spy
     * provider records every fully-qualified name the runtime asks the provider
     * for; the test asserts that ACCOUNTING and AUDITING (the two schemas the SQL
     * does NOT touch) are absent from the request log.
     */
    public void testPlanAcceptanceLazyBridgeDoesNotTouchUnreferencedSchemas() throws Exception {
        // Read the multi-schema manifest into a model. This mirrors what
        // CatalogLoaders.loadRuntime would do, but we want to install our own spy
        // ModelBackedCatalogProvider so we can observe the requested names.
        CatalogLoadOptions options = CatalogLoadOptions.builder()
            .vendor(EDbVendor.dbvoracle)
            .defaultCatalog("ORCL")
            .defaultSchema("HR")
            .loadingMode(CatalogLoadingMode.LAZY)
            .normalizeOnLoad(true)
            .build();
        CatalogInputSource source = CatalogInputSources.fromPath(
            multiSchemaManifestPath(), CatalogInputKind.JSON_MANIFEST);
        UnifiedCatalogModel model = CatalogInputReaders.forSource(source, options)
            .read(source, options);

        SpyProvider spy = new SpyProvider(new ModelBackedCatalogProvider(model));
        spy.open(CatalogProviderConfig.empty());
        CatalogRuntime runtime = CatalogRuntime.builder()
            .provider(spy)
            .vendor(EDbVendor.dbvoracle)
            .loadingMode(CatalogLoadingMode.LAZY) // no initial snapshot
            .maxFetchesPerAnalysis(64)
            .build();
        TSQLEnv lazyEnv = LazyCatalogSqlEnv.from(runtime, options);

        // Same SQL as the eager demo — joins HR.EMPLOYEES and HR.DEPARTMENTS only.
        // ACCOUNTING and AUDITING are present in the manifest but should never be
        // requested by the lazy bridge during this parse.
        String resolved = parseAndFormat(lazyEnv, SQL);
        assertResolvedHrEmployeesAndDepartments(resolved);

        // The spy should have seen requests for the HR tables only. Each schema
        // segment is at index `size-2` (counting from the end) of the qualified name —
        // for 3-part names that's index 1 (catalog.SCHEMA.object), for 4-part names
        // index 2 (server.catalog.SCHEMA.object). We assert size>=3 so a future
        // refactor that produces 2-part names doesn't silently pass with the table
        // name in the schema slot. Codex Round 5 P2 finding.
        Set<String> schemasTouched = new HashSet<>();
        for (CatalogQualifiedName q : spy.requestedNames) {
            assertTrue(
                "Expected fully-qualified (>=3 segments) name from the lazy bridge; got " + q,
                q.size() >= 3);
            // Schema lives at the second-to-last position before the local name. For
            // 3-part names: catalog[0].schema[1].object[2]. For 4-part: server[0].
            // catalog[1].schema[2].object[3].
            schemasTouched.add(q.normalized().get(q.size() - 2).toUpperCase());
        }
        // HR is required (the SQL touches HR.EMPLOYEES and HR.DEPARTMENTS).
        assertTrue("Expected HR schema to be touched: " + schemasTouched,
            schemasTouched.contains("HR"));
        // ACCOUNTING and AUDITING must NOT be touched — that is the whole point of
        // lazy mode for a multi-schema catalog.
        assertFalse("Lazy bridge unexpectedly touched ACCOUNTING: " + schemasTouched,
            schemasTouched.contains("ACCOUNTING"));
        assertFalse("Lazy bridge unexpectedly touched AUDITING: " + schemasTouched,
            schemasTouched.contains("AUDITING"));
    }

    /**
     * Spy wrapper around a real {@link CatalogProvider} that records every requested
     * qualified name across all {@code snapshot()} calls. Used by the lazy-mode demo
     * to prove that unreferenced schemas are never asked about.
     */
    private static final class SpyProvider implements CatalogProvider {
        private final CatalogProvider delegate;
        final Set<CatalogQualifiedName> requestedNames = new HashSet<>();

        SpyProvider(CatalogProvider delegate) {
            this.delegate = delegate;
        }

        @Override public CatalogProviderId id() { return delegate.id(); }

        @Override public void open(CatalogProviderConfig config) throws CatalogException {
            delegate.open(config);
        }

        @Override
        public CatalogSnapshot snapshot(CatalogQuery query) throws CatalogException {
            requestedNames.addAll(query.requestedNames());
            return delegate.snapshot(query);
        }

        @Override public CatalogSnapshot refresh(CatalogQuery query) throws CatalogException {
            requestedNames.addAll(query.requestedNames());
            return delegate.refresh(query);
        }

        @Override public void close() throws CatalogException {
            delegate.close();
        }
    }

    /**
     * Match a line in the formatter output exactly (case-insensitively, after
     * trimming). The formatter prints one item per line; substring-containment
     * across the whole blob can mask resolution misses (e.g., a fallback prints
     * the column without a parent table). This helper enforces the line-level
     * shape the plan §17 assertion expects.
     */
    private static boolean hasLine(String lowerOutput, String expectedLowerLine) {
        for (String line : lowerOutput.split("\n")) {
            if (line.trim().equals(expectedLowerLine)) return true;
        }
        return false;
    }

    private static String parseAndFormat(TSQLEnv env, String sql) {
        TGSqlParser parser = new TGSqlParser(EDbVendor.dbvoracle);
        parser.setResolverType(EResolverType.RESOLVER2);
        parser.setSqlEnv(env);
        parser.sqltext = sql;
        int rc = parser.parse();
        assertEquals("parse should succeed for: " + sql + "\nerr=" + parser.getErrormessage(),
            0, rc);
        TSQLResolver2 resolver = parser.getResolver2();
        assertNotNull("resolver2 should be available after parse", resolver);
        TSQLResolver2ResultFormatter fmt = new TSQLResolver2ResultFormatter(
            resolver,
            resolver.getConfig() != null ? resolver.getConfig() : TSQLResolverConfig.createDefault());
        fmt.setShowDatatype(false);
        fmt.setListStarColumn(false);
        return fmt.format();
    }

    private static void assertResolvedHrEmployeesAndDepartments(String formatted) {
        // Codex Round 5 finding 5: line-level assertions catch silent resolution
        // misses where the formatter falls back to printing a column without its
        // parent table. We require both the bare table line and the table.column
        // linkage line.
        String lower = formatted.toLowerCase();
        assertTrue("Expected resolved table EMPLOYEES, got:\n" + formatted,
            hasLine(lower, "employees"));
        assertTrue("Expected resolved table DEPARTMENTS, got:\n" + formatted,
            hasLine(lower, "departments"));
        assertTrue("Expected resolved field employees.employee_id, got:\n" + formatted,
            hasLine(lower, "employees.employee_id"));
        assertTrue("Expected resolved field departments.department_name, got:\n" + formatted,
            hasLine(lower, "departments.department_name"));
    }
}
