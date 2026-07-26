package gudusoft.gsqlparser.demos.checksyntax;

import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.EDbVendor;
import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Batch SQL syntax checker - validates multiple SQL samples in a single JVM invocation.
 * Usage: BatchCheckSyntax <input_file> [output_file]
 * Input: SQL samples separated by ---SEPARATOR---
 * Output: Failed SQL samples written to output file
 */
public class BatchCheckSyntax {
    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.err.println("Usage: BatchCheckSyntax <input_file> [output_file]");
            System.exit(1);
        }

        String inputFile = args[0];
        String outputFile = args.length > 1 ? args[1] : "/tmp/bigquery_failed_sql.txt";
        String content = new String(Files.readAllBytes(Paths.get(inputFile)), "UTF-8");
        String[] blocks = content.split("---SEPARATOR---");

        int passed = 0, failed = 0, skipped = 0;
        StringBuilder failedOutput = new StringBuilder();
        List<String> failedSummaries = new ArrayList<>();

        for (String block : blocks) {
            block = block.trim();
            if (block.isEmpty()) { skipped++; continue; }

            // Extract metadata comments and SQL
            String[] lines = block.split("\n");
            StringBuilder sql = new StringBuilder();
            StringBuilder metadata = new StringBuilder();
            for (String line : lines) {
                if (line.startsWith("-- Source:") || line.startsWith("-- Category:") || line.startsWith("-- Feature:")) {
                    metadata.append(line).append("\n");
                } else {
                    sql.append(line).append("\n");
                }
            }

            String sqlText = sql.toString().trim();
            if (sqlText.isEmpty()) { skipped++; continue; }
            if (sqlText.length() < 5) { skipped++; continue; }

            TGSqlParser parser = new TGSqlParser(EDbVendor.dbvbigquery);
            parser.sqltext = sqlText;
            int result = parser.parse();

            if (result == 0) {
                passed++;
            } else {
                failed++;
                String errorMsg = parser.getErrormessage();
                failedOutput.append("---FAILED---\n");
                failedOutput.append(metadata.toString());
                failedOutput.append("ERROR: ").append(errorMsg).append("\n");
                failedOutput.append("SQL:\n").append(sqlText).append("\n\n");

                // Summary line
                String preview = sqlText.length() > 100 ? sqlText.substring(0, 100) + "..." : sqlText;
                preview = preview.replace("\n", " ");
                failedSummaries.add(String.format("FAIL #%d: %s | Error: %s", failed, preview, errorMsg.split("\n")[0]));
            }
        }

        // Print summary
        System.out.println("=== BigQuery SQL Batch Validation Summary ===");
        System.out.println("TOTAL:   " + (passed + failed + skipped));
        System.out.println("PASSED:  " + passed);
        System.out.println("FAILED:  " + failed);
        System.out.println("SKIPPED: " + skipped);
        System.out.println("PASS RATE: " + (passed + failed > 0 ? String.format("%.1f%%", 100.0 * passed / (passed + failed)) : "N/A"));
        System.out.println();

        if (!failedSummaries.isEmpty()) {
            System.out.println("=== Failed SQL Summary ===");
            for (String s : failedSummaries) {
                System.out.println(s);
            }
        }

        // Write detailed failures to output file
        Files.write(Paths.get(outputFile), failedOutput.toString().getBytes("UTF-8"));
        System.out.println("\nDetailed failures written to: " + outputFile);
    }
}
