package gudusoft.gsqlparser.demos.dumpir;

import gudusoft.gsqlparser.EDbVendor;
import gudusoft.gsqlparser.TGSqlParser;
import gudusoft.gsqlparser.TStatementList;
import gudusoft.gsqlparser.analyzer.v2.AnalyzerV2Config;
import gudusoft.gsqlparser.ir.bound.BoundColumnRef;
import gudusoft.gsqlparser.ir.bound.BoundObjectRef;
import gudusoft.gsqlparser.ir.bound.BoundProgram;
import gudusoft.gsqlparser.ir.bound.BoundRoutineRef;
import gudusoft.gsqlparser.ir.bound.BoundRoutineSymbol;
import gudusoft.gsqlparser.ir.bound.BoundScope;
import gudusoft.gsqlparser.ir.bound.BoundSymbol;
import gudusoft.gsqlparser.ir.builder.IBoundIRBuilder;
import gudusoft.gsqlparser.ir.builder.mssql.MssqlBoundIRBuilder;
import gudusoft.gsqlparser.ir.builder.mssql.MssqlRoutineRefResolver;
import gudusoft.gsqlparser.ir.builder.oracle.OracleBoundIRBuilder;
import gudusoft.gsqlparser.ir.builder.postgresql.PostgresqlBoundIRBuilder;
import gudusoft.gsqlparser.ir.common.SourceAnchor;
import gudusoft.gsqlparser.ir.semantic.AnalysisResult;
import gudusoft.gsqlparser.ir.semantic.Diagnostic;
import gudusoft.gsqlparser.ir.semantic.SqlSemanticAnalyzer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Capture helper for the GSP IR article series (plan §15).
 *
 * <p>Reads a SQL file plus a vendor flag and writes per-layer JSON
 * captures (Bound, Logical, Semantic) into a target directory. Article
 * authors run this through the {@code docs/ir/tools/dump-ir.sh} wrapper
 * instead of editing {@code gsp_java_core/src/test/…} to print IR.
 *
 * <p>This class deliberately lives in {@code gsp_demo_java} so that
 * the core parser module stays read-only for the article series.
 * The serializers are minimal hand-rolled JSON — adding a JSON
 * dependency is forbidden by the plan.
 *
 * <p>Layer semantics today:
 * <ul>
 *   <li><b>Bound IR</b> — emitted when the requested vendor has a
 *       checked-in {@link IBoundIRBuilder}. The plan whitelists
 *       PostgreSQL, Oracle, MSSQL/Azure SQL. Other vendors get a
 *       structured "not emitted yet" stub.</li>
 *   <li><b>Logical IR</b> — no concrete {@code ILogicalIRBuilder} in
 *       tree yet emits a relational plan from raw SQL; the layer is
 *       always a stub at S0 time. Articles must say "Not emitted by
 *       this path yet" per plan §11 STEP 5.</li>
 *   <li><b>Semantic IR</b> — emitted via
 *       {@link SqlSemanticAnalyzer#analyze(String, EDbVendor)}; on
 *       failure the file contains a diagnostics envelope so callers
 *       can show structured reject reasons.</li>
 * </ul>
 *
 * <p>Exit codes match plan §15.2:
 * <pre>
 *   0  — all requested layers written
 *   1  — parse or analyzer error for at least one requested layer
 *   2  — bad CLI args or missing input file
 * </pre>
 */
public final class DumpIR {

    /** JSON schema tag emitted by the hand-rolled serializers. */
    private static final String CAPTURE_SCHEMA = "ir-capture/1";

    private DumpIR() {
        // CLI entry point — no instances.
    }

    public static void main(String[] args) {
        Args parsed;
        try {
            parsed = Args.parse(args);
        } catch (ArgError e) {
            System.err.println("dump-ir: " + e.getMessage());
            System.err.println();
            System.err.println(Args.usage());
            System.exit(2);
            return;
        }

        try {
            int rc = run(parsed);
            System.exit(rc);
        } catch (IOException e) {
            System.err.println("dump-ir: I/O error: " + e.getMessage());
            System.exit(2);
        }
    }

    /**
     * Visible-for-testing entry point. Returns the would-be process
     * exit code instead of calling {@link System#exit}.
     */
    static int run(Args args) throws IOException {
        String sql = new String(Files.readAllBytes(args.sqlPath), StandardCharsets.UTF_8);
        // Best-effort directory creation; on partial failure mid-batch
        // some captured-*.json files may exist and others may not. The
        // wrapper's non-zero exit code is the signal authors look at;
        // article authors typically rerun the helper on the failing
        // case rather than relying on transactional out-dir writes.
        Files.createDirectories(args.outDir);

        boolean anyError = false;

        if (args.layers.contains("bound")) {
            BoundCapture cap = captureBound(sql, args.vendor);
            writeJson(args.outDir.resolve("bound.captured.json"), cap.json);
            if (cap.failed) {
                anyError = true;
                System.err.println("dump-ir: bound: " + cap.failureReason);
            }
        }

        if (args.layers.contains("logical")) {
            String json = captureLogical(sql, args.vendor);
            writeJson(args.outDir.resolve("logical.captured.json"), json);
            // Logical IR is intentionally a stub today; "emitted=false"
            // is the documented S0 state and does NOT count as a failure.
        }

        if (args.layers.contains("semantic")) {
            SemanticCapture semantic = captureSemantic(sql, args.vendor);
            writeJson(args.outDir.resolve("semantic.captured.json"), semantic.json);
            if (semantic.failed) {
                anyError = true;
                for (Diagnostic d : semantic.diagnostics) {
                    System.err.println("dump-ir: semantic: "
                            + d.getSeverity() + " " + d.getCode() + " — " + d.getMessage());
                }
            }
        }

        return anyError ? 1 : 0;
    }

    // ----- Bound IR -----------------------------------------------------

    private static BoundCapture captureBound(String sql, EDbVendor vendor) {
        IBoundIRBuilder builder = boundBuilderFor(vendor);
        if (builder == null) {
            // No builder is a known coverage gap, not a parse error —
            // articles for vendors outside the PG/Oracle/MSSQL trio say
            // "Not emitted by this path yet" per plan §15.6.
            return BoundCapture.notEmitted(notEmittedJson("bound",
                    "No IBoundIRBuilder is registered for vendor " + vendor.name()
                            + " in tree. Only PostgreSQL, Oracle, and MSSQL/Azure SQL "
                            + "ship Bound IR builders today."));
        }
        TGSqlParser parser = new TGSqlParser(vendor);
        parser.sqltext = sql;
        int rc = parser.parse();
        if (rc != 0) {
            String reason = "parser returned non-zero rc=" + rc
                    + (parser.getErrormessage() != null && !parser.getErrormessage().isEmpty()
                            ? "; " + parser.getErrormessage() : "");
            return BoundCapture.failed(
                    parseFailedJson("bound", parser.getErrormessage(), rc), reason);
        }
        AnalyzerV2Config config = AnalyzerV2Config.createIREnabled();
        TStatementList stmts = parser.sqlstatements;
        BoundProgram program = builder.build(stmts, config);
        if (builder instanceof MssqlBoundIRBuilder) {
            MssqlRoutineRefResolver.resolve(program);
        }
        return BoundCapture.ok(BoundJsonWriter.toJson(program, vendor));
    }

    private static IBoundIRBuilder boundBuilderFor(EDbVendor vendor) {
        if (vendor == null) return null;
        switch (vendor) {
            case dbvpostgresql:
                return new PostgresqlBoundIRBuilder();
            case dbvoracle:
                return new OracleBoundIRBuilder();
            case dbvmssql:
            case dbvazuresql:
                return new MssqlBoundIRBuilder();
            default:
                return null;
        }
    }

    // ----- Logical IR ---------------------------------------------------

    private static String captureLogical(String sql, EDbVendor vendor) {
        return notEmittedJson("logical",
                "No concrete ILogicalIRBuilder is wired to translate parsed "
                        + "SQL into a LogicalProgram in tree today. The "
                        + "ir/logical RelNode / RexNode hierarchy exists "
                        + "and is exercised by LogicalIRTest, but the "
                        + "AST → Logical IR builder remains a Phase 2 "
                        + "scaffold per IRTranslator.java.");
    }

    // ----- Semantic IR --------------------------------------------------

    private static SemanticCapture captureSemantic(String sql, EDbVendor vendor) {
        AnalysisResult result;
        try {
            result = SqlSemanticAnalyzer.analyze(sql, vendor);
        } catch (RuntimeException e) {
            String envelope = analyzerErrorJson(e);
            return new SemanticCapture(envelope, true,
                    java.util.Collections.<Diagnostic>emptyList());
        }
        if (result.isSuccessful() && result.getJson() != null) {
            return new SemanticCapture(result.getJson(), false, result.getDiagnostics());
        }
        return new SemanticCapture(diagnosticsEnvelopeJson(result),
                true, result.getDiagnostics());
    }

    // ----- Hand-rolled JSON helpers ------------------------------------

    private static String notEmittedJson(String layer, String reason) {
        JsonOut out = new JsonOut();
        out.openObject();
        out.writeString("captureSchema", CAPTURE_SCHEMA);
        out.writeString("layer", layer);
        out.writeBool("emitted", false);
        out.writeString("reason", reason);
        out.closeObject();
        return out.finish();
    }

    private static String parseFailedJson(String layer, String errMsg, int rc) {
        JsonOut out = new JsonOut();
        out.openObject();
        out.writeString("captureSchema", CAPTURE_SCHEMA);
        out.writeString("layer", layer);
        out.writeBool("emitted", false);
        out.writeString("reason", "parser returned non-zero rc=" + rc
                + (errMsg != null && !errMsg.isEmpty() ? "; " + errMsg : ""));
        out.closeObject();
        return out.finish();
    }

    private static String diagnosticsEnvelopeJson(AnalysisResult result) {
        JsonOut out = new JsonOut();
        out.openObject();
        out.writeString("captureSchema", CAPTURE_SCHEMA);
        out.writeString("layer", "semantic");
        out.writeBool("emitted", false);
        out.writeRaw("schemaVersion", "\"" + JsonOut.escape(result.getSchemaVersion()) + "\"");
        out.openArray("diagnostics");
        for (Diagnostic d : result.getDiagnostics()) {
            out.arrayItemBeginObject();
            out.writeString("code", d.getCode() == null ? null : d.getCode().name());
            out.writeString("severity", d.getSeverity() == null ? null : d.getSeverity().name());
            out.writeString("message", d.getMessage());
            out.arrayItemEndObject();
        }
        out.closeArray();
        out.closeObject();
        return out.finish();
    }

    private static String analyzerErrorJson(RuntimeException e) {
        JsonOut out = new JsonOut();
        out.openObject();
        out.writeString("captureSchema", CAPTURE_SCHEMA);
        out.writeString("layer", "semantic");
        out.writeBool("emitted", false);
        out.writeString("reason", "Analyzer threw "
                + e.getClass().getName() + ": " + e.getMessage());
        out.closeObject();
        return out.finish();
    }

    private static void writeJson(Path file, String body) throws IOException {
        String contents = body.endsWith("\n") ? body : body + "\n";
        Files.write(file, contents.getBytes(StandardCharsets.UTF_8));
    }

    // ----- Bound JSON writer -------------------------------------------

    private static final class BoundJsonWriter {

        static String toJson(BoundProgram program, EDbVendor vendor) {
            JsonOut out = new JsonOut();
            out.openObject();
            out.writeString("captureSchema", CAPTURE_SCHEMA);
            out.writeString("layer", "bound");
            out.writeBool("emitted", true);
            out.writeString("vendor", vendor.name());

            out.openArray("scopes");
            for (BoundScope scope : program.getScopes()) {
                writeScope(out, scope);
            }
            out.closeArray();

            out.openArray("objectRefs");
            for (BoundObjectRef ref : program.getAllObjectRefs()) {
                writeObjectRef(out, ref);
            }
            out.closeArray();

            out.openArray("columnRefs");
            for (BoundColumnRef ref : program.getAllColumnRefs()) {
                writeColumnRef(out, ref);
            }
            out.closeArray();

            out.openArray("routineRefs");
            for (BoundRoutineRef ref : program.getAllRoutineRefs()) {
                writeRoutineRef(out, ref);
            }
            out.closeArray();

            out.openArray("routines");
            for (Map.Entry<String, BoundRoutineSymbol> entry : program.getRoutineIndex().entrySet()) {
                BoundRoutineSymbol sym = entry.getValue();
                out.arrayItemBeginObject();
                out.writeString("routineId", entry.getKey());
                out.writeString("name", sym.getName());
                out.writeString("kind", sym.getSymbolKind().name());
                out.arrayItemEndObject();
            }
            out.closeArray();

            out.closeObject();
            return out.finish();
        }

        private static void writeScope(JsonOut out, BoundScope scope) {
            out.arrayItemBeginObject();
            out.writeLong("scopeId", scope.getScopeId());
            out.writeString("kind", scope.getKind() == null ? null : scope.getKind().name());
            if (scope.getParent() != null) {
                out.writeLong("parentScopeId", scope.getParent().getScopeId());
            }
            writeAnchor(out, "anchor", scope.getAnchor());
            out.openArray("symbols");
            for (Map.Entry<String, BoundSymbol> sym : scope.getSymbols().entrySet()) {
                out.arrayItemBeginObject();
                out.writeString("name", sym.getKey());
                BoundSymbol bs = sym.getValue();
                out.writeString("kind", bs.getSymbolKind() == null ? null : bs.getSymbolKind().name());
                writeAnchor(out, "declarationAnchor", bs.getDeclarationAnchor());
                out.arrayItemEndObject();
            }
            out.closeArray();
            out.arrayItemEndObject();
        }

        private static void writeObjectRef(JsonOut out, BoundObjectRef ref) {
            out.arrayItemBeginObject();
            out.writeString("originalText", ref.getOriginalText());
            out.writeStringArray("nameParts", ref.getNameParts());
            out.writeString("refKind", ref.getRefKind() == null ? null : ref.getRefKind().name());
            out.writeString("bindingStatus",
                    ref.getBindingStatus() == null ? null : ref.getBindingStatus().name());
            BoundSymbol resolved = ref.getResolvedSymbol();
            if (resolved != null) {
                out.writeString("resolvedSymbolName", resolved.getName());
                out.writeString("resolvedSymbolKind",
                        resolved.getSymbolKind() == null ? null : resolved.getSymbolKind().name());
            }
            out.arrayItemEndObject();
        }

        private static void writeColumnRef(JsonOut out, BoundColumnRef ref) {
            out.arrayItemBeginObject();
            out.writeString("originalText", ref.getOriginalText());
            out.writeStringArray("nameParts", ref.getNameParts());
            out.writeString("qualifierKind",
                    ref.getQualifierKind() == null ? null : ref.getQualifierKind().name());
            out.writeString("bindingStatus",
                    ref.getBindingStatus() == null ? null : ref.getBindingStatus().name());
            BoundObjectRef relation = ref.getResolvedRelation();
            if (relation != null) {
                out.writeString("resolvedRelation", relation.getOriginalText());
            }
            out.writeString("resolvedColumnName", ref.getResolvedColumnName());
            out.arrayItemEndObject();
        }

        private static void writeRoutineRef(JsonOut out, BoundRoutineRef ref) {
            out.arrayItemBeginObject();
            out.writeString("originalText", ref.getOriginalText());
            out.writeStringArray("nameParts", ref.getNameParts());
            out.writeString("bindingStatus",
                    ref.getBindingStatus() == null ? null : ref.getBindingStatus().name());
            BoundRoutineSymbol resolved = ref.getResolvedRoutine();
            if (resolved != null) {
                out.writeString("resolvedRoutineId", resolved.getRoutineId());
                out.writeString("resolvedRoutineName", resolved.getName());
            }
            out.arrayItemEndObject();
        }

        private static void writeAnchor(JsonOut out, String key, SourceAnchor anchor) {
            if (anchor == null) return;
            out.openObject(key);
            if (anchor.fileId != null && !anchor.fileId.isEmpty()) {
                out.writeString("fileId", anchor.fileId);
            }
            out.writeLong("startLine", anchor.startLine);
            out.writeLong("startCol", anchor.startCol);
            out.writeLong("endLine", anchor.endLine);
            out.writeLong("endCol", anchor.endCol);
            out.closeObject();
        }
    }

    // ----- Tiny JSON writer (2-space indent, ordered keys) -------------

    static final class JsonOut {

        private final StringBuilder buf = new StringBuilder(256);
        private final java.util.ArrayDeque<Frame> stack = new java.util.ArrayDeque<Frame>();
        private boolean done;

        private enum FrameKind { OBJECT, ARRAY }

        private static final class Frame {
            final FrameKind kind;
            int count;
            Frame(FrameKind kind) { this.kind = kind; }
        }

        JsonOut() {
            // root frame is opened explicitly by callers via openObject().
        }

        void openObject() {
            beforeValue();
            buf.append('{');
            stack.push(new Frame(FrameKind.OBJECT));
        }

        void openObject(String key) {
            startKey(key);
            buf.append('{');
            stack.push(new Frame(FrameKind.OBJECT));
        }

        void closeObject() {
            Frame f = stack.pop();
            if (f.kind != FrameKind.OBJECT) {
                throw new IllegalStateException("not in object");
            }
            if (f.count > 0) {
                buf.append('\n');
                indent();
            }
            buf.append('}');
            afterValue();
        }

        void openArray(String key) {
            startKey(key);
            buf.append('[');
            stack.push(new Frame(FrameKind.ARRAY));
        }

        void closeArray() {
            Frame f = stack.pop();
            if (f.kind != FrameKind.ARRAY) {
                throw new IllegalStateException("not in array");
            }
            if (f.count > 0) {
                buf.append('\n');
                indent();
            }
            buf.append(']');
            afterValue();
        }

        void arrayItemBeginObject() {
            Frame f = peekArray();
            if (f.count++ > 0) buf.append(',');
            buf.append('\n');
            indent();
            buf.append('{');
            stack.push(new Frame(FrameKind.OBJECT));
        }

        void arrayItemEndObject() {
            Frame f = stack.pop();
            if (f.kind != FrameKind.OBJECT) {
                throw new IllegalStateException("not in object");
            }
            if (f.count > 0) {
                buf.append('\n');
                indent();
            }
            buf.append('}');
        }

        void writeString(String key, String value) {
            startKey(key);
            appendStringValue(value);
            afterValue();
        }

        void writeStringArray(String key, List<String> values) {
            startKey(key);
            buf.append('[');
            if (values != null) {
                for (int i = 0; i < values.size(); i++) {
                    if (i > 0) buf.append(", ");
                    appendStringValue(values.get(i));
                }
            }
            buf.append(']');
            afterValue();
        }

        void writeBool(String key, boolean value) {
            startKey(key);
            buf.append(value ? "true" : "false");
            afterValue();
        }

        void writeLong(String key, long value) {
            startKey(key);
            buf.append(value);
            afterValue();
        }

        void writeRaw(String key, String rawJson) {
            startKey(key);
            buf.append(rawJson);
            afterValue();
        }

        String finish() {
            if (!stack.isEmpty()) {
                throw new IllegalStateException("unclosed JSON frames: " + stack.size());
            }
            done = true;
            return buf.toString();
        }

        // ---- internals ----

        private void beforeValue() {
            if (!stack.isEmpty()) {
                throw new IllegalStateException("root object already open");
            }
            if (done) {
                throw new IllegalStateException("JSON already finished");
            }
        }

        private void afterValue() {
            // counts are bumped in startKey() / arrayItemBeginObject(); nothing to do.
        }

        private void startKey(String key) {
            Frame f = peekObject();
            if (f.count++ > 0) buf.append(',');
            buf.append('\n');
            indent();
            appendStringValue(key);
            buf.append(": ");
        }

        private Frame peekObject() {
            Frame f = stack.peek();
            if (f == null || f.kind != FrameKind.OBJECT) {
                throw new IllegalStateException("not in object");
            }
            return f;
        }

        private Frame peekArray() {
            Frame f = stack.peek();
            if (f == null || f.kind != FrameKind.ARRAY) {
                throw new IllegalStateException("not in array");
            }
            return f;
        }

        private void indent() {
            int depth = stack.size();
            for (int i = 0; i < depth; i++) buf.append("  ");
        }

        private void appendStringValue(String s) {
            if (s == null) {
                buf.append("null");
                return;
            }
            buf.append('"');
            buf.append(escape(s));
            buf.append('"');
        }

        static String escape(String s) {
            if (s == null) return "";
            StringBuilder out = new StringBuilder(s.length() + 8);
            for (int i = 0; i < s.length(); i++) {
                char c = s.charAt(i);
                switch (c) {
                    case '\\': out.append("\\\\"); break;
                    case '"':  out.append("\\\""); break;
                    case '\n': out.append("\\n"); break;
                    case '\r': out.append("\\r"); break;
                    case '\t': out.append("\\t"); break;
                    case '\b': out.append("\\b"); break;
                    case '\f': out.append("\\f"); break;
                    default:
                        if (c < 0x20) {
                            out.append(String.format("\\u%04x", (int) c));
                        } else {
                            out.append(c);
                        }
                }
            }
            return out.toString();
        }
    }

    // ----- Helpers -----------------------------------------------------

    private static final class SemanticCapture {
        final String json;
        final boolean failed;
        final List<Diagnostic> diagnostics;
        SemanticCapture(String json, boolean failed, List<Diagnostic> diagnostics) {
            this.json = json;
            this.failed = failed;
            this.diagnostics = diagnostics;
        }
    }

    /**
     * Bound IR capture outcome. Three constructors with intentionally
     * distinct semantics:
     * <ul>
     *   <li>{@link #ok(String)} — Bound IR was built and serialized.</li>
     *   <li>{@link #notEmitted(String)} — known coverage gap (no
     *       {@link IBoundIRBuilder} for this vendor). NOT a failure;
     *       articles for such vendors say "Not emitted by this path
     *       yet" per plan §15.6.</li>
     *   <li>{@link #failed(String, String)} — parser rc!=0; counts as
     *       a failure and propagates to the wrapper's exit code per
     *       plan §15.2.</li>
     * </ul>
     * Only {@code failed} sets {@code failed=true}; {@code ok} and
     * {@code notEmitted} both encode {@code failed=false} on purpose
     * because the contract is "did anything go wrong?", not "was data
     * produced?". The JSON written to disk distinguishes the two via
     * its {@code "emitted"} key.
     */
    private static final class BoundCapture {
        final String json;
        final boolean failed;
        final String failureReason;

        private BoundCapture(String json, boolean failed, String failureReason) {
            this.json = json;
            this.failed = failed;
            this.failureReason = failureReason;
        }

        static BoundCapture ok(String json) { return new BoundCapture(json, false, null); }
        static BoundCapture notEmitted(String json) { return new BoundCapture(json, false, null); }
        static BoundCapture failed(String json, String reason) {
            return new BoundCapture(json, true, reason);
        }
    }

    // ----- CLI args parsing --------------------------------------------

    static final class Args {
        final Path sqlPath;
        final EDbVendor vendor;
        final Path outDir;
        final Set<String> layers;

        Args(Path sqlPath, EDbVendor vendor, Path outDir, Set<String> layers) {
            this.sqlPath = sqlPath;
            this.vendor = vendor;
            this.outDir = outDir;
            this.layers = layers;
        }

        static String usage() {
            return "Usage:\n"
                    + "  dump-ir.sh --sql <path> --vendor <ed-vendor> --out-dir <dir> [--layers <list>]\n"
                    + "\n"
                    + "Required:\n"
                    + "  --sql       Path to a UTF-8 file containing SQL.\n"
                    + "  --vendor    EDbVendor enum value (e.g. dbvpostgresql, dbvoracle, dbvmssql).\n"
                    + "  --out-dir   Directory to receive bound.captured.json /\n"
                    + "              logical.captured.json / semantic.captured.json.\n"
                    + "\n"
                    + "Optional:\n"
                    + "  --layers    Comma-separated subset of {bound,logical,semantic}.\n"
                    + "              Default: bound,logical,semantic.\n";
        }

        static Args parse(String[] args) {
            String sqlArg = null;
            String vendorArg = null;
            String outArg = null;
            String layersArg = null;

            int i = 0;
            while (i < args.length) {
                String a = args[i];
                switch (a) {
                    case "--sql":     sqlArg = requireValue(args, ++i, "--sql"); break;
                    case "--vendor":  vendorArg = requireValue(args, ++i, "--vendor"); break;
                    case "--out-dir": outArg = requireValue(args, ++i, "--out-dir"); break;
                    case "--layers":  layersArg = requireValue(args, ++i, "--layers"); break;
                    case "-h":
                    case "--help":
                        throw new ArgError("help requested\n" + usage());
                    default:
                        throw new ArgError("unknown flag: " + a);
                }
                i++;
            }
            if (sqlArg == null)    throw new ArgError("--sql is required");
            if (vendorArg == null) throw new ArgError("--vendor is required");
            if (outArg == null)    throw new ArgError("--out-dir is required");

            Path sqlPath = Paths.get(sqlArg);
            if (!Files.isRegularFile(sqlPath)) {
                throw new ArgError("--sql does not refer to a regular file: " + sqlArg);
            }
            EDbVendor vendor = parseVendor(vendorArg);
            Path outDir = Paths.get(outArg);

            Set<String> layers = parseLayers(layersArg);
            return new Args(sqlPath, vendor, outDir, layers);
        }

        private static String requireValue(String[] args, int idx, String flag) {
            if (idx >= args.length) throw new ArgError(flag + " requires a value");
            return args[idx];
        }

        private static EDbVendor parseVendor(String raw) {
            String norm = raw.trim().toLowerCase(Locale.ROOT);
            if (!norm.startsWith("dbv")) {
                norm = "dbv" + norm;
            }
            for (EDbVendor v : EDbVendor.values()) {
                if (v.name().equalsIgnoreCase(norm)) return v;
            }
            throw new ArgError("unknown vendor: " + raw
                    + " (expected an EDbVendor enum value such as dbvpostgresql)");
        }

        private static Set<String> parseLayers(String raw) {
            Set<String> all = new LinkedHashSet<String>(Arrays.asList("bound", "logical", "semantic"));
            if (raw == null || raw.trim().isEmpty()) return all;
            Set<String> picked = new LinkedHashSet<String>();
            for (String part : raw.split(",")) {
                String norm = part.trim().toLowerCase(Locale.ROOT);
                if (norm.isEmpty()) continue;
                if (!all.contains(norm)) {
                    throw new ArgError("unknown layer: " + part
                            + " (expected one or more of bound,logical,semantic)");
                }
                picked.add(norm);
            }
            if (picked.isEmpty()) {
                throw new ArgError("--layers cannot be empty");
            }
            return picked;
        }
    }

    /** Unchecked CLI parse error; main() translates these to exit-code 2. */
    static final class ArgError extends RuntimeException {
        ArgError(String msg) { super(msg); }
    }

    // ----- Static utility for tests / programmatic callers -------------

    /** Capture a single SQL string in-process; returns the captured JSON strings. */
    public static java.util.Map<String, String> captureAll(String sql, EDbVendor vendor) {
        java.util.LinkedHashMap<String, String> map = new java.util.LinkedHashMap<String, String>();
        map.put("bound", captureBound(sql, vendor).json);
        map.put("logical", captureLogical(sql, vendor));
        SemanticCapture sem = captureSemantic(sql, vendor);
        map.put("semantic", sem.json);
        return map;
    }

    // Suppress "unused" warning on this list constructor.
    @SuppressWarnings("unused")
    private static List<String> defaultLayers() {
        return new ArrayList<String>(Arrays.asList("bound", "logical", "semantic"));
    }
}
