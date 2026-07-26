package gudusoft.gsqlparser.demos.checksyntax;

/**
 * This demo illustrates how to use General SQL Parser to check syntax of SQL script.
 * You can download more demos from official site: http://www.sqlparser.com
 *
 * Usage modes:
 *   /f <file>      - Check syntax of a single SQL file
 *   /d <directory> - Check syntax of all .sql files in a directory
 *   /s             - Read SQL from stdin (for piping SQL text)
 *   /t <type>      - Specify database type (default: oracle)
 *                    For OceanBase, tenant-mode aliases are supported:
 *                      oceanbase / oceanbase-mysql / ob  — MySQL tenant (default)
 *                      oceanbase-oracle / ob-oracle      — Oracle tenant
 *                      oceanbase-system / ob-system      — system tenant
 *   /try_all_dbvendors - When parsing fails with the specified vendor, try all
 *                        other implemented vendors. If any succeeds, treat the
 *                        file as passed and report the actual vendor.
 *
 * Exit codes (for /s mode):
 *   0 - SQL syntax is correct
 *   1 - SQL syntax is incorrect
 */

import gudusoft.gsqlparser.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class checksyntax {

    private static File[] listFiles(File sqlFiles) {
        List<File> children = new ArrayList<File>();
        if (sqlFiles != null)
            listFiles(sqlFiles, children);
        return children.toArray(new File[0]);
    }

    private static void listFiles(File rootFile, List<File> children) {
        if (rootFile.isFile())
            children.add(rootFile);
        else {
            File[] files = rootFile.listFiles();
            for (int i = 0; i < files.length; i++) {
                listFiles(files[i], children);
            }
        }
    }

    private static String readFromStdin() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        return sb.toString();
    }

    public static void main(String args[]) {
        List<String> argList = Arrays.asList(args);

        if (args.length < 1) {
            System.out.println("Usage: java checksyntax [/f <path_to_sql_file>] [/d <path_to_directory_includes_sql_files>] [/s] [/t <database type>] [/try_all_dbvendors]");
            System.out.println("  /f <file>      - Check syntax of a single SQL file");
            System.out.println("  /d <directory> - Check syntax of all .sql files in a directory");
            System.out.println("  /s             - Read SQL from stdin");
            System.out.println("  /t <type>      - Specify database type (default: oracle)");
            System.out.println("                   OceanBase tenant-mode aliases:");
            System.out.println("                     oceanbase | oceanbase-mysql | ob     (MySQL tenant, default)");
            System.out.println("                     oceanbase-oracle | ob-oracle        (Oracle tenant)");
            System.out.println("                     oceanbase-system | ob-system       (system tenant)");
            System.out.println("  /try_all_dbvendors - Try all vendors if default fails");
            System.out.println("\nExit codes (for /s mode):");
            System.out.println("  0 - SQL syntax is correct");
            System.out.println("  1 - SQL syntax is incorrect");
            System.exit(1);
            return;
        }

        EDbVendor vendor = EDbVendor.dbvoracle;
        EOBTenantMode obTenantMode = EOBTenantMode.MYSQL;
        int index = argList.indexOf("/t");
        if (index != -1 && args.length > index + 1) {
            String typeArg = args[index + 1];
            obTenantMode = resolveOBTenantMode(typeArg);
            vendor = TGSqlParser.getDBVendorByName(stripOBTenantSuffix(typeArg));
        }

        boolean tryAllVendors = argList.contains("/try_all_dbvendors");

        // Check for stdin mode
        if (argList.contains("/s")) {
            checkSyntaxFromStdin(vendor, obTenantMode, tryAllVendors);
            return;
        }

        // File or directory mode
        checkSyntaxFromFileOrDirectory(args, argList, vendor, obTenantMode, tryAllVendors);
    }

    /**
     * Resolve an OceanBase tenant-mode alias from the /t argument.
     * Non-OceanBase vendors (or plain {@code oceanbase}/{@code ob}) return
     * {@link EOBTenantMode#MYSQL} (the OceanBase parser default).
     */
    public static EOBTenantMode resolveOBTenantMode(String typeArg) {
        if (typeArg == null) return EOBTenantMode.MYSQL;
        String lower = typeArg.toLowerCase();
        if (lower.equals("oceanbase-oracle") || lower.equals("ob-oracle")) {
            return EOBTenantMode.ORACLE;
        }
        if (lower.equals("oceanbase-system") || lower.equals("ob-system")) {
            return EOBTenantMode.SYSTEM;
        }
        return EOBTenantMode.MYSQL;
    }

    /**
     * Strip the {@code -oracle}/{@code -system}/{@code -mysql} suffix from an
     * OceanBase tenant-mode alias so {@link TGSqlParser#getDBVendorByName(String)}
     * still resolves it to {@code dbvoceanbase}. Non-OceanBase args pass through
     * unchanged.
     */
    public static String stripOBTenantSuffix(String typeArg) {
        if (typeArg == null) return null;
        String lower = typeArg.toLowerCase();
        if (lower.equals("oceanbase-oracle") || lower.equals("oceanbase-system")
                || lower.equals("oceanbase-mysql")) {
            return "oceanbase";
        }
        if (lower.equals("ob-oracle") || lower.equals("ob-system") || lower.equals("ob-mysql")) {
            return "ob";
        }
        return typeArg;
    }

    /**
     * Construct a TGSqlParser for the given vendor, applying the OceanBase
     * tenant mode when the vendor is {@code dbvoceanbase}. For all other
     * vendors the mode is ignored.
     */
    private static TGSqlParser newParser(EDbVendor vendor, EOBTenantMode obTenantMode) {
        TGSqlParser parser = new TGSqlParser(vendor);
        if (vendor == EDbVendor.dbvoceanbase && obTenantMode != null) {
            parser.setOBTenantMode(obTenantMode);
        }
        return parser;
    }

    /**
     * Try parsing SQL text with all implemented vendors except the specified one.
     * Returns the first vendor that succeeds, or null if none do.
     */
    private static EDbVendor tryParseWithOtherVendors(String sqlText, EDbVendor excludeVendor) {
        for (EDbVendor v : EDbVendor.values()) {
            if (v == excludeVendor || !v.isImplemented()) continue;
            try {
                TGSqlParser parser = new TGSqlParser(v);
                parser.sqltext = sqlText;
                if (parser.parse() == 0) {
                    return v;
                }
            } catch (Exception | StackOverflowError e) {
                // Skip vendors that crash during parsing
            }
        }
        return null;
    }

    /**
     * Try parsing a SQL file with all implemented vendors except the specified one.
     * Returns the first vendor that succeeds, or null if none do.
     */
    private static EDbVendor tryParseFileWithOtherVendors(String filePath, EDbVendor excludeVendor) {
        for (EDbVendor v : EDbVendor.values()) {
            if (v == excludeVendor || !v.isImplemented()) continue;
            try {
                TGSqlParser parser = new TGSqlParser(v);
                parser.sqlfilename = filePath;
                if (parser.parse() == 0) {
                    return v;
                }
            } catch (Exception | StackOverflowError e) {
                // Skip vendors that crash during parsing
            }
        }
        return null;
    }

    private static void checkSyntaxFromStdin(EDbVendor vendor, EOBTenantMode obTenantMode, boolean tryAllVendors) {
        String sqlText;
        try {
            sqlText = readFromStdin();
        } catch (IOException e) {
            System.out.println("Error reading from stdin: " + e.getMessage());
            System.exit(1);
            return;
        }

        if (sqlText.trim().isEmpty()) {
            System.out.println("Error: No SQL input provided");
            System.exit(1);
            return;
        }

        TGSqlParser sqlparser = newParser(vendor, obTenantMode);
        sqlparser.sqltext = sqlText;

        int ret = sqlparser.parse();
        if (ret == 0) {
            System.exit(0);
        } else {
            if (tryAllVendors) {
                EDbVendor actualVendor = tryParseWithOtherVendors(sqlText, vendor);
                if (actualVendor != null) {
                    System.out.println("PASS_WITH_VENDOR:" + actualVendor.getPrimaryAlias());
                    System.exit(0);
                    return;
                }
            }
            System.out.println(sqlparser.getErrormessage());
            System.exit(1);
        }
    }

    private static void checkSyntaxFromFileOrDirectory(String[] args, List<String> argList, EDbVendor vendor, EOBTenantMode obTenantMode, boolean tryAllVendors) {
        long t = System.currentTimeMillis();
        File sqlFiles = null;

        if (argList.indexOf("/f") != -1
                && argList.size() > argList.indexOf("/f") + 1) {
            sqlFiles = new File(args[argList.indexOf("/f") + 1]);
            if (!sqlFiles.exists() || !sqlFiles.isFile()) {
                System.out.println(sqlFiles + " is not a valid file.");
                System.exit(1);
                return;
            }
        } else if (argList.indexOf("/d") != -1
                && argList.size() > argList.indexOf("/d") + 1) {
            sqlFiles = new File(args[argList.indexOf("/d") + 1]);
            if (!sqlFiles.exists() || !sqlFiles.isDirectory()) {
                System.out.println(sqlFiles + " is not a valid directory.");
                System.exit(1);
                return;
            }
        } else {
            System.out.println("Please specify a sql file path, directory path, or use /s for stdin.");
            System.exit(1);
            return;
        }

        File[] children = listFiles(sqlFiles);
        int total_sql_files = 0, error_sql_files = 0, vendor_mismatch_files = 0;

        for (int i = 0; i < children.length; i++) {
            File child = children[i];
            if (child.isDirectory())
                continue;
            if (child.getAbsolutePath().endsWith(".sql")) {
                total_sql_files++;
                try {
                    TGSqlParser sqlparser = newParser(vendor, obTenantMode);
                    sqlparser.sqlfilename = child.getAbsolutePath();

                    int ret = sqlparser.parse();
                    if (ret != 0) {
                        if (tryAllVendors) {
                            EDbVendor actualVendor = tryParseFileWithOtherVendors(child.getAbsolutePath(), vendor);
                            if (actualVendor != null) {
                                vendor_mismatch_files++;
                                System.out.println("PASS_WITH_VENDOR:" + child.getAbsolutePath() + "\t" + actualVendor.getPrimaryAlias());
                                continue;
                            }
                        }
                        error_sql_files++;
                        System.out.println(child.getAbsolutePath() + System.getProperty("line.separator") + sqlparser.getErrormessage());
                        System.out.println("");
                    }
                } catch (Exception | StackOverflowError | OutOfMemoryError e) {
                    error_sql_files++;
                    System.out.println(child.getAbsolutePath() + System.getProperty("line.separator")
                            + "Internal error: " + e.getClass().getSimpleName() + ": " + e.getMessage());
                    System.out.println("");
                    if (e instanceof OutOfMemoryError) {
                        // Force GC after OOM to try to recover for remaining files
                        System.gc();
                    }
                }
            }
        }

        System.out.println("Time Escaped: " + (System.currentTimeMillis() - t) + ", file processed: " + total_sql_files + ", syntax errors: " + error_sql_files
                + (vendor_mismatch_files > 0 ? ", vendor mismatch: " + vendor_mismatch_files : ""));
        if (error_sql_files > 0) {
            System.out.println("Selected SQL dialect: " + vendor.toString());
            System.exit(1);
        } else {
            System.exit(0);
        }
    }
}