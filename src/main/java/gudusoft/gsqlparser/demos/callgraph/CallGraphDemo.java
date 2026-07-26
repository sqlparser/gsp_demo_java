package gudusoft.gsqlparser.demos.callgraph;

/**
 * Generates a call graph from procedural SQL source files.
 * <p>
 * This demo uses the IR Phase A infrastructure (IBoundIRBuilder + CallGraph)
 * to analyze procedural SQL code and produce a JSON call graph conforming to the
 * callgraph-json-schema.json schema (v1.1).
 * <p>
 * Supported database vendors:
 * <ul>
 *   <li>Oracle (PL/SQL) - default</li>
 *   <li>SQL Server (T-SQL) - use /t mssql</li>
 * </ul>
 * <p>
 * The output includes:
 * <ul>
 *   <li>boundProgram: routine declarations, object/column ref counts, routine call references</li>
 *   <li>callGraph: nodes (routines with table accesses) and edges (call relationships)</li>
 * </ul>
 * <p>
 * Usage:
 * <pre>
 *   java CallGraphDemo /f &lt;path_to_sql_file&gt; [/o &lt;output_json_file&gt;] [/t &lt;database_type&gt;]
 *                      [/no-anchors] [/no-evidence]
 *
 *   /f &lt;file&gt;       - Procedural SQL source file to analyze (required)
 *   /o &lt;file&gt;       - Output JSON file (optional; defaults to stdout)
 *   /t &lt;type&gt;       - Database type: oracle (default), mssql
 *   /no-anchors     - Omit sourceAnchor fields from output
 *   /no-evidence    - Omit evidence fields from output
 * </pre>
 *
 * Exit codes:
 * <pre>
 *   0 - Success
 *   1 - Error (invalid arguments, file not found, parse failure)
 * </pre>
 */

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.analyzer.v2.AnalysisResult;
import gudusoft.gsqlparser.analyzer.v2.AnalyzerV2Config;
import gudusoft.gsqlparser.analyzer.v2.AnalyzerV2Facade;
import gudusoft.gsqlparser.ir.builder.IBoundIRBuilder;
import gudusoft.gsqlparser.ir.builder.mssql.MssqlBoundIRBuilder;
import gudusoft.gsqlparser.ir.builder.oracle.OracleBoundIRBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

public class CallGraphDemo {

    public static void main(String[] args) {
        List<String> argList = Arrays.asList(args);

        if (args.length < 2 || !argList.contains("/f")) {
            printUsage();
            System.exit(1);
            return;
        }

        // Parse arguments
        int fIndex = argList.indexOf("/f");
        if (fIndex + 1 >= args.length) {
            System.err.println("Error: /f requires a file path argument.");
            System.exit(1);
            return;
        }
        String inputFilePath = args[fIndex + 1];

        String outputFilePath = null;
        int oIndex = argList.indexOf("/o");
        if (oIndex != -1) {
            if (oIndex + 1 >= args.length) {
                System.err.println("Error: /o requires a file path argument.");
                System.exit(1);
                return;
            }
            outputFilePath = args[oIndex + 1];
        }

        EDbVendor vendor = EDbVendor.dbvoracle;
        int tIndex = argList.indexOf("/t");
        if (tIndex != -1 && tIndex + 1 < args.length) {
            vendor = TGSqlParser.getDBVendorByName(args[tIndex + 1]);
        }

        boolean includeAnchors = !argList.contains("/no-anchors");
        boolean includeEvidence = !argList.contains("/no-evidence");

        // Validate input file
        File inputFile = new File(inputFilePath);
        if (!inputFile.exists() || !inputFile.isFile()) {
            System.err.println("Error: File not found: " + inputFilePath);
            System.exit(1);
            return;
        }

        // Read SQL text from file
        String sqlText;
        try {
            sqlText = new String(Files.readAllBytes(inputFile.toPath()), StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            System.exit(1);
            return;
        }

        if (sqlText.trim().isEmpty()) {
            System.err.println("Error: Input file is empty.");
            System.exit(1);
            return;
        }

        // Configure analyzer
        AnalyzerV2Config config = AnalyzerV2Config.createIREnabled();
        config.includeSourceAnchors = includeAnchors;
        config.includeEvidence = includeEvidence;

        // Analyze
        IBoundIRBuilder builder = createBuilder(vendor);
        AnalyzerV2Facade analyzer = new AnalyzerV2Facade(builder);
        AnalysisResult result = analyzer.analyze(sqlText, vendor, config);

        if (result.getIrProgram() == null
                || result.getIrProgram().getBoundProgram() == null) {
            System.err.println("Error: Analysis produced no results. Check that the input contains valid procedural SQL.");
            System.exit(1);
            return;
        }

        // Export JSON
        String json = result.exportJson();

        // Output
        if (outputFilePath != null) {
            try (Writer writer = new OutputStreamWriter(
                    new FileOutputStream(outputFilePath), StandardCharsets.UTF_8)) {
                writer.write(json);
                System.err.println("Call graph written to: " + outputFilePath);
            } catch (IOException e) {
                System.err.println("Error writing output file: " + e.getMessage());
                System.exit(1);
                return;
            }
        } else {
            System.out.print(json);
        }
    }

    private static IBoundIRBuilder createBuilder(EDbVendor vendor) {
        switch (vendor) {
            case dbvoracle:
                return new OracleBoundIRBuilder();
            case dbvmssql:
                return new MssqlBoundIRBuilder();
            default:
                System.err.println("Error: Unsupported vendor '" + vendor
                        + "' for call graph analysis.");
                System.err.println("Supported vendors: oracle, mssql");
                System.exit(1);
                return null; // unreachable
        }
    }

    private static void printUsage() {
        System.out.println("CallGraphDemo - Generate call graph from procedural SQL source");
        System.out.println();
        System.out.println("Usage: java CallGraphDemo /f <sql_file> [/o <output_json>] [/t <db_type>]");
        System.out.println("                          [/no-anchors] [/no-evidence]");
        System.out.println();
        System.out.println("  /f <file>       Procedural SQL source file to analyze (required)");
        System.out.println("  /o <file>       Output JSON file (default: stdout)");
        System.out.println("  /t <type>       Database type: oracle (default), mssql");
        System.out.println("  /no-anchors     Omit sourceAnchor fields from output");
        System.out.println("  /no-evidence    Omit evidence fields from output");
        System.out.println();
        System.out.println("Output conforms to callgraph-json-schema.json (v1.1).");
        System.out.println();
        System.out.println("Examples:");
        System.out.println("  java CallGraphDemo /f my_package.sql");
        System.out.println("  java CallGraphDemo /f my_package.sql /o callgraph.json");
        System.out.println("  java CallGraphDemo /f my_package.sql /t mssql /no-anchors");
    }
}
