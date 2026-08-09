package gudusoft.gsqlparser.demos.formatsql;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.pp.para.GFmtOpt;
import gudusoft.gsqlparser.pp.para.GFmtOptFactory;
import gudusoft.gsqlparser.pp.stmtformatter.FormatterFactory;
import gudusoft.gsqlparser.pp2.FormatDiagnostic;
import gudusoft.gsqlparser.pp2.FormatStatus;
import gudusoft.gsqlparser.pp2.Pp2FormatOptions;
import gudusoft.gsqlparser.pp2.Pp2FormatResult;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * Formats a SQL file with either the traditional AST formatter or Java's
 * fault-tolerant pp2 formatter.
 *
 * <p>The default mode preserves the demo's original parse-then-format flow.
 * Add {@code /tolerant} to format valid, partially invalid, or completely
 * unparseable SQL without requiring {@link TGSqlParser#parse()} to succeed.
 */
public class formatsql {

    public static void main(String[] args) {
        run(args, System.out, System.err);
    }

    /**
     * Testable command-line entry point.
     *
     * @return zero when formatting produced usable output; one for invalid
     *         arguments, an unreadable file, a strict parse failure, or an
     *         unrecoverable pp2 result
     */
    public static int run(String[] args, PrintStream out, PrintStream err) {
        if (args == null || args.length < 1) {
            printUsage(out);
            return 1;
        }

        File file = new File(args[0]);
        if (!file.isFile()) {
            err.println("File does not exist: " + args[0]);
            return 1;
        }

        EDbVendor vendor = EDbVendor.dbvoracle;
        boolean tolerant = false;
        for (int i = 1; i < args.length; i++) {
            if ("/t".equalsIgnoreCase(args[i])) {
                if (i + 1 >= args.length) {
                    err.println("/t requires a database type, for example /t oracle");
                    return 1;
                }
                vendor = TGSqlParser.getDBVendorByName(args[++i]);
            } else if ("/tolerant".equalsIgnoreCase(args[i])) {
                tolerant = true;
            } else {
                err.println("Unknown option: " + args[i]);
                printUsage(err);
                return 1;
            }
        }

        String sql;
        try {
            sql = readSqlFile(file);
        } catch (IOException e) {
            err.println("Could not read SQL file: " + e.getMessage());
            return 1;
        }
        GFmtOpt style = GFmtOptFactory.newInstance(
                "formatsql." + Long.toString(System.nanoTime()));

        if (tolerant) {
            Pp2FormatResult result = formatTolerantly(sql, vendor, style);
            out.println(result.getText());
            printRecoveryDiagnostics(result, err);
            return result.getStatus() == FormatStatus.FAILED ? 1 : 0;
        }

        TGSqlParser parser = new TGSqlParser(vendor);
        parser.sqltext = sql;
        if (parser.parse() != 0) {
            err.println(parser.getErrormessage());
            err.println("Tip: add /tolerant to format this file with pp2 recovery.");
            return 1;
        }

        out.println(FormatterFactory.pp(parser, style));
        return 0;
    }

    /**
     * Formats SQL through the parse-independent pp2 recovery pipeline.
     * The parser carries only the SQL text and database dialect; callers do
     * not need to invoke {@code parse()} first.
     */
    public static Pp2FormatResult formatTolerantly(String sql,
                                                    EDbVendor vendor,
                                                    GFmtOpt style) {
        TGSqlParser parser = new TGSqlParser(vendor);
        parser.sqltext = sql;
        return FormatterFactory.pp2(parser, Pp2FormatOptions.from(style));
    }

    private static void printRecoveryDiagnostics(Pp2FormatResult result,
                                                  PrintStream err) {
        if (result.getStatus() == FormatStatus.OK
                && result.getDiagnostics().isEmpty()) {
            return;
        }

        err.println("Formatter status: " + result.getStatus());
        for (FormatDiagnostic diagnostic : result.getDiagnostics()) {
            err.println("  " + diagnostic.getSeverity()
                    + " [" + diagnostic.getStartOffset()
                    + ".." + diagnostic.getEndOffset() + "]: "
                    + diagnostic.getMessage());
        }
    }

    /** Reads UTF-8 or BOM-marked UTF-16 without trimming source whitespace. */
    private static String readSqlFile(File file) throws IOException {
        byte[] bytes = Files.readAllBytes(file.toPath());
        int offset = 0;
        Charset charset = StandardCharsets.UTF_8;
        if (bytes.length >= 3
                && bytes[0] == (byte) 0xEF
                && bytes[1] == (byte) 0xBB
                && bytes[2] == (byte) 0xBF) {
            offset = 3;
        } else if (bytes.length >= 2
                && bytes[0] == (byte) 0xFE
                && bytes[1] == (byte) 0xFF) {
            offset = 2;
            charset = StandardCharsets.UTF_16BE;
        } else if (bytes.length >= 2
                && bytes[0] == (byte) 0xFF
                && bytes[1] == (byte) 0xFE) {
            offset = 2;
            charset = StandardCharsets.UTF_16LE;
        }
        return new String(bytes, offset, bytes.length - offset, charset);
    }

    private static void printUsage(PrintStream out) {
        out.println("Usage: java formatsql <sqlfile.sql> [/t <database type>] [/tolerant]");
        out.println("  /t <type>  Database dialect; default: oracle");
        out.println("  /tolerant  Format malformed SQL with Java's pp2 recovery formatter");
    }
}
