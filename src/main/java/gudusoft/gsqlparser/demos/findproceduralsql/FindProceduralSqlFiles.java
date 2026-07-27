package gudusoft.gsqlparser.demos.findproceduralsql;

import gudusoft.gsqlparser.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

import static gudusoft.gsqlparser.ESqlStatementType.*;

/**
 * Scans a source directory recursively for SQL files containing procedural
 * statements (Oracle PL/SQL or SQL Server T-SQL) and copies them to an output directory.
 *
 * <p>Oracle PL/SQL statements detected include:
 * <ul>
 *   <li>CREATE [OR REPLACE] PROCEDURE / FUNCTION / PACKAGE / TRIGGER / TYPE</li>
 *   <li>Anonymous PL/SQL blocks (DECLARE...BEGIN...END)</li>
 *   <li>Other PL/SQL constructs (EXECUTE IMMEDIATE, FORALL, PIPE ROW, etc.)</li>
 * </ul>
 *
 * <p>SQL Server T-SQL statements detected include:
 * <ul>
 *   <li>CREATE PROCEDURE / FUNCTION / TRIGGER</li>
 *   <li>BEGIN...END blocks, IF, WHILE, TRY...CATCH</li>
 *   <li>DECLARE, EXEC/EXECUTE, PRINT, THROW, RETURN, etc.</li>
 * </ul>
 *
 * <p>Usage: {@code FindProceduralSqlFiles <dbvendor> <source-sql-dir> <output-sql-dir>}
 * <p>Where {@code dbvendor} is one of: {@code oracle}, {@code mssql}, {@code sqlserver}
 *
 * <p>If a file with the same name already exists in the output directory and has the
 * same size, the copy is skipped. If the sizes differ, a numeric suffix is appended
 * to avoid overwriting (e.g., {@code myfile_1.sql}).
 */
public class FindProceduralSqlFiles {

    /**
     * Oracle PL/SQL statement types.
     */
    private static final Set<ESqlStatementType> PLSQL_TYPES = EnumSet.of(
            // Anonymous PL/SQL blocks (DECLARE...BEGIN...END, BEGIN...END)
            sst_plsql_block,

            // Top-level PL/SQL CREATE statements
            sstplsql_createprocedure,
            sstplsql_createfunction,
            sstplsql_createpackage,
            sstplsql_createtrigger,
            sstplsql_createtype,
            sstplsql_createtypebody,
            sstplsql_createtype_placeholder,
            sstcreate_plsql_procedure,
            sstcreate_plsql_function,
            sstplsql_packages,
            sstplsql_objecttype,

            // Internal PL/SQL statements (indicate procedural code)
            sstplsql_dummystmt,
            sstplsql_execimmestmt,
            sstplsql_forallstmt,
            sstplsql_gotostmt,
            sstplsql_nullstmt,
            sstplsql_pragmadecl,
            sstplsql_procbasicstmt,
            sstplsql_procedurespec,
            sstplsql_recordtypedef,
            sstplsql_sqlstmt,
            sstplsql_proceduredecl,
            sstplsql_tabletypedef,
            sstplsql_vardecl,
            sstplsql_varraytypedef,
            sstplsql_piperowstmt,
            ssstplsqlContinue
    );

    /**
     * SQL Server T-SQL statement types indicating procedural code.
     */
    private static final Set<ESqlStatementType> TSQL_TYPES = EnumSet.of(
            // Top-level CREATE/ALTER statements
            sstmssqlcreateprocedure,
            sstmssqlcreatefunction,
            sstmssqlcreatetrigger,
            sstmssqlalterprocedure,
            sstmssqlalterfunction,
            sstmssqlaltertrigger,

            // Block and control flow
            sstmssqlblock,
            sstmssqlif,
            sstmssqlwhile,
            sstmssqlTryCatch,
            sstmssqlgoto,
            sstmssqllabel,
            sstmssqlreturn,
            sstmssqlcontinue,

            // T-SQL procedural statements
            sstmssqldeclare,
            sstmssqlexec,
            sstmssqlexecuteas,
            sstmssqlrevert,
            sstmssqlprint,
            sstmssqlthrow,
            sstmssqlset,
            sstmssqlsetrowcount,
            sstmssqlwaitfor,
            sstmssqldummystmt,
            sstmssqlstmtstub,

            // Cursor operations
            sstmssqlopen,
            sstmssqlfetch,
            sstmssqlclose,
            sstmssqldeallocate,

            // Transaction control
            sstmssqlbegintran,
            sstmssqlcommit,
            sstmssqlrollback,
            sstmssqlsavetran
    );

    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("Usage: FindProceduralSqlFiles <dbvendor> <source-sql-dir> <output-sql-dir>");
            System.out.println("  dbvendor: oracle, mssql, or sqlserver");
            System.exit(1);
        }

        String vendorArg = args[0].toLowerCase();
        EDbVendor vendor;
        Set<ESqlStatementType> proceduralTypes;
        String label;

        switch (vendorArg) {
            case "oracle":
                vendor = EDbVendor.dbvoracle;
                proceduralTypes = PLSQL_TYPES;
                label = "PL/SQL";
                break;
            case "mssql":
            case "sqlserver":
                vendor = EDbVendor.dbvmssql;
                proceduralTypes = TSQL_TYPES;
                label = "T-SQL";
                break;
            default:
                System.err.println("Unsupported vendor: " + vendorArg
                        + ". Use oracle, mssql, or sqlserver.");
                System.exit(1);
                return;
        }

        File sourceDir = new File(args[1]);
        File outputDir = new File(args[2]);

        if (!sourceDir.isDirectory()) {
            System.err.println("Source directory does not exist: " + sourceDir);
            System.exit(1);
        }

        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        long startTime = System.currentTimeMillis();

        TGSqlParser parser = new TGSqlParser(vendor);

        List<File> sqlFiles = new ArrayList<>();
        collectSqlFiles(sourceDir, sqlFiles, vendor);

        int total = 0, matchCount = 0, copiedCount = 0, skippedCount = 0;

        for (File sqlFile : sqlFiles) {
            total++;
            parser.sqlfilename = sqlFile.getAbsolutePath();
            parser.parse();

            if (containsProceduralCode(parser, proceduralTypes)) {
                matchCount++;
                int result = copyFile(sqlFile, outputDir);
                if (result > 0) {
                    copiedCount++;
                } else if (result == 0) {
                    skippedCount++;
                }
            }
        }

        long elapsed = System.currentTimeMillis() - startTime;
        System.out.println("\nSummary:");
        System.out.println("  Total SQL files scanned:    " + total);
        System.out.println("  Files containing " + label + ": " + matchCount);
        System.out.println("  Files copied:               " + copiedCount);
        System.out.println("  Files skipped (same size):  " + skippedCount);
        System.out.println("  Time elapsed:               " + elapsed + " ms");
    }

    /**
     * Recursively collects SQL files from the given directory.
     */
    private static void collectSqlFiles(File dir, List<File> result, EDbVendor vendor) {
        File[] files = dir.listFiles();
        if (files == null) return;
        Arrays.sort(files);
        for (File file : files) {
            if (file.isDirectory()) {
                collectSqlFiles(file, result, vendor);
            } else if (isSqlFile(file, vendor)) {
                result.add(file);
            }
        }
    }

    private static boolean isSqlFile(File file, EDbVendor vendor) {
        String name = file.getName().toLowerCase();
        if (name.endsWith(".sql")) return true;

        if (vendor == EDbVendor.dbvoracle) {
            return name.endsWith(".pls") || name.endsWith(".plsql")
                    || name.endsWith(".pck") || name.endsWith(".pkb") || name.endsWith(".pks")
                    || name.endsWith(".trg") || name.endsWith(".fnc") || name.endsWith(".prc");
        } else if (vendor == EDbVendor.dbvmssql) {
            return name.endsWith(".tsql");
        }
        return false;
    }

    /**
     * Checks whether the parser's current statement list contains any procedural statements.
     */
    private static boolean containsProceduralCode(TGSqlParser parser,
                                                   Set<ESqlStatementType> proceduralTypes) {
        if (parser.sqlstatements == null) return false;
        for (int i = 0; i < parser.sqlstatements.size(); i++) {
            ESqlStatementType type = parser.sqlstatements.get(i).sqlstatementtype;
            if (proceduralTypes.contains(type)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Copies the source file to the output directory.
     *
     * @return 1 if copied, 0 if skipped (same size), -1 on error
     */
    private static int copyFile(File sourceFile, File outputDir) {
        String filename = sourceFile.getName();
        File destFile = new File(outputDir, filename);

        if (destFile.exists()) {
            if (destFile.length() == sourceFile.length()) {
                System.out.println("  SKIP (same size): " + filename);
                return 0;
            }
            // Different size — append a number to avoid overwriting
            String baseName = filename;
            String extension = "";
            int dotIdx = filename.lastIndexOf('.');
            if (dotIdx > 0) {
                baseName = filename.substring(0, dotIdx);
                extension = filename.substring(dotIdx);
            }
            int counter = 1;
            do {
                destFile = new File(outputDir, baseName + "_" + counter + extension);
                counter++;
            } while (destFile.exists());
        }

        try {
            Files.copy(sourceFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            System.out.println("  COPY: " + sourceFile.getPath() + " -> " + destFile.getName());
            return 1;
        } catch (IOException e) {
            System.err.println("  ERROR copying " + sourceFile.getPath() + ": " + e.getMessage());
            return -1;
        }
    }
}
