package gudusoft.gsqlparser.demos.checksyntax;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Validates SQL syntax in the application process without a database connection.
 *
 * <p>The public {@link #validate(String, EDbVendor)} method is the reusable part
 * of the demo. The command-line entry point adds file loading, dialect selection,
 * actionable output, and exit codes suitable for CI or shell scripts.</p>
 */
public final class OfflineSyntaxCheck {

    public static final String SAMPLE_SQL =
            "SELECT o.order_id,\n"
                    + "       o.customer_id,\n"
                    + "       o.total_amount\n"
                    + "FROM sales.orders o\n"
                    + "WHERE o.status = 'OPEN'\n"
                    + "ORDER BY o.created_at DESC";

    private OfflineSyntaxCheck() {
    }

    public static void main(String[] args) {
        try {
            Options options = Options.parse(args);
            String sql = options.file == null
                    ? SAMPLE_SQL
                    : new String(Files.readAllBytes(options.file), StandardCharsets.UTF_8);
            String input = options.file == null
                    ? "built-in sample"
                    : options.file.toAbsolutePath().toString();

            ValidationResult result = validate(sql, options.vendor);
            printResult(input, result);

            if (!result.isValid()) {
                System.exit(1);
            }
        } catch (IllegalArgumentException | IOException error) {
            System.err.println("Input error: " + error.getMessage());
            printUsage();
            System.exit(2);
        }
    }

    /**
     * Parses a complete SQL string with a fresh parser instance.
     *
     * @param sql SQL text to validate
     * @param vendor grammar that must be used for the input SQL
     * @return an immutable validation result containing the parser diagnostic
     */
    public static ValidationResult validate(String sql, EDbVendor vendor) {
        if (vendor == null) {
            throw new IllegalArgumentException("A database dialect is required.");
        }
        if (sql == null || sql.trim().isEmpty()) {
            return ValidationResult.rejected(vendor, "SQL input is empty.");
        }

        TGSqlParser parser = new TGSqlParser(vendor);
        parser.sqltext = sql;
        int parseCode = parser.parse();

        if (parseCode == 0) {
            return ValidationResult.accepted(vendor, parser.sqlstatements.size());
        }
        return ValidationResult.rejected(vendor, parser.getErrormessage());
    }

    /** Resolves a user-facing dialect alias and rejects unknown or unavailable grammars. */
    public static EDbVendor resolveVendor(String alias) {
        EDbVendor vendor = EDbVendor.fromAlias(alias);
        if (vendor == null || !vendor.isImplemented()) {
            throw new IllegalArgumentException("Unsupported database dialect: " + alias);
        }
        return vendor;
    }

    private static void printResult(String input, ValidationResult result) {
        System.out.println("Offline SQL syntax validation");
        System.out.println("Input: " + input);
        System.out.println("Dialect: " + result.getVendor().getPrimaryAlias());
        System.out.println("Database connection used: no");

        if (result.isValid()) {
            System.out.println("Result: ACCEPTED");
            System.out.println("Statements parsed: " + result.getStatementCount());
        } else {
            System.out.println("Result: REJECTED");
            System.out.println("Parser diagnostic:");
            System.out.println(result.getErrorMessage());
        }
    }

    private static void printUsage() {
        System.err.println("Usage: OfflineSyntaxCheck [/f <sql-file>] [/t <dialect>]");
        System.err.println("Without /f, the demo validates a built-in Oracle query.");
    }

    private static final class Options {
        private final EDbVendor vendor;
        private final Path file;

        private Options(EDbVendor vendor, Path file) {
            this.vendor = vendor;
            this.file = file;
        }

        private static Options parse(String[] args) {
            EDbVendor vendor = EDbVendor.dbvoracle;
            Path file = null;

            for (int index = 0; index < args.length; index++) {
                String argument = args[index];
                if ("/t".equalsIgnoreCase(argument)) {
                    vendor = resolveVendor(requireValue(args, ++index, "/t"));
                } else if ("/f".equalsIgnoreCase(argument)) {
                    file = Paths.get(requireValue(args, ++index, "/f"));
                    if (!Files.isRegularFile(file)) {
                        throw new IllegalArgumentException("SQL file does not exist: " + file);
                    }
                } else {
                    throw new IllegalArgumentException("Unknown argument: " + argument);
                }
            }

            return new Options(vendor, file);
        }

        private static String requireValue(String[] args, int index, String flag) {
            if (index >= args.length || args[index].trim().isEmpty()) {
                throw new IllegalArgumentException(flag + " requires a value.");
            }
            return args[index];
        }
    }

    public static final class ValidationResult {
        private final boolean valid;
        private final EDbVendor vendor;
        private final int statementCount;
        private final String errorMessage;

        private ValidationResult(boolean valid,
                                 EDbVendor vendor,
                                 int statementCount,
                                 String errorMessage) {
            this.valid = valid;
            this.vendor = vendor;
            this.statementCount = statementCount;
            this.errorMessage = errorMessage;
        }

        private static ValidationResult accepted(EDbVendor vendor, int statementCount) {
            return new ValidationResult(true, vendor, statementCount, null);
        }

        private static ValidationResult rejected(EDbVendor vendor, String errorMessage) {
            String diagnostic = errorMessage == null || errorMessage.trim().isEmpty()
                    ? "The parser rejected the SQL without a diagnostic."
                    : errorMessage;
            return new ValidationResult(false, vendor, 0, diagnostic);
        }

        public boolean isValid() {
            return valid;
        }

        public EDbVendor getVendor() {
            return vendor;
        }

        public int getStatementCount() {
            return statementCount;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }
}
