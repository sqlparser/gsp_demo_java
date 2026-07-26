# Call Graph Verification Workflow

> **Purpose**: Step-by-step procedure for verifying and improving the GSP IR-based
> call graph analyzer. Designed to be automated as a skill for iterative quality
> improvement.

---

## Overview

This workflow validates that the call graph JSON produced by `CallGraphDemo`
accurately reflects all call relationships in the input procedural SQL source.
When discrepancies are found, the root cause is diagnosed in the IR builder code
and fixed, then the cycle repeats until the output is correct.

**Supported vendors:**
- **Oracle** (PL/SQL) — default, use `/t oracle`
- **SQL Server** (T-SQL) — use `/t mssql`

```
 ┌─────────────────────────────────────────────────────────────────┐
 │                      Iterative Verification Loop                │
 │                                                                 │
 │   ┌──────────┐    ┌──────────┐    ┌──────────┐    ┌──────────┐ │
 │   │  Step 1   │───▶│  Step 2   │───▶│  Step 3   │───▶│  Step 4   │ │
 │   │ Generate  │    │ Validate  │    │ Verify    │    │ Diagnose  │ │
 │   │ call graph│    │ schema   │    │ content   │    │ & fix     │ │
 │   └──────────┘    └──────────┘    └──────────┘    └─────┬──────┘ │
 │        ▲                                                │       │
 │        └────────────────────────────────────────────────┘       │
 │                     Repeat until correct                        │
 └─────────────────────────────────────────────────────────────────┘
```

---

## Prerequisites

- GSP Java project built: `mvn install -N && mvn clean install -pl gsp_java_core -Pquick_install`
- Demo module compiled: `mvn compile -pl gsp_demo_java`
- Python 3 with `jsonschema` package: `pip3 install jsonschema`
- Input: one or more procedural SQL `.sql` files (PL/SQL or T-SQL)

---

## Step 1: Generate the Call Graph

Run `CallGraphDemo` against the target procedural SQL file.

```bash
mvn -pl gsp_demo_java exec:java \
  -Dexec.mainClass="gudusoft.gsqlparser.demos.callgraph.CallGraphDemo" \
  -Dexec.args="/f <INPUT_SQL_FILE> /o <OUTPUT_JSON_FILE> [/t <vendor>]" \
  -q
```

**Examples:**
```bash
# Oracle PL/SQL (default)
mvn -pl gsp_demo_java exec:java \
  -Dexec.mainClass="gudusoft.gsqlparser.demos.callgraph.CallGraphDemo" \
  -Dexec.args="/f /home/ubuntu/tmp/plsql/zin.sql /o /tmp/zin_callgraph.json" \
  -q

# SQL Server T-SQL
mvn -pl gsp_demo_java exec:java \
  -Dexec.mainClass="gudusoft.gsqlparser.demos.callgraph.CallGraphDemo" \
  -Dexec.args="/f /home/ubuntu/tmp/mssql/pro1.sql /o /tmp/pro1_callgraph.json /t mssql" \
  -q
```

**Expected output:** A message `Call graph written to: <OUTPUT_JSON_FILE>` on stderr.
If the tool exits with code 1, check stderr for parse errors or file-not-found.

### Flags

| Flag | Effect |
|------|--------|
| `/f <file>` | Input procedural SQL file (required) |
| `/o <file>` | Output JSON file (default: stdout) |
| `/t <type>` | Database vendor: `oracle` (default), `mssql` |
| `/no-anchors` | Omit `sourceAnchor` fields (smaller output) |
| `/no-evidence` | Omit `evidence` fields |

---

## Step 2: Validate JSON Against Schema

Verify the output is well-formed JSON and conforms to the call graph JSON schema.

```bash
python3 -c "
import json, jsonschema

schema = json.load(open('gsp_java_core/doc/v5/callgraph-json-schema.json'))
data = json.load(open('<OUTPUT_JSON_FILE>'))

# 1. Check JSON syntax (implicitly done by json.load)
print('JSON syntax: OK')

# 2. Check top-level keys
print('Top-level keys:', list(data.keys()))

# 3. Validate against schema
try:
    jsonschema.validate(instance=data, schema=schema)
    print('Schema validation: PASSED')
except jsonschema.ValidationError as e:
    print('Schema validation: FAILED')
    print('Error:', e.message)
    print('Path:', list(e.absolute_path))
"
```

### What to Check

| Check | Pass Criteria |
|-------|---------------|
| JSON syntax | `json.load()` succeeds without error |
| Top-level keys | Contains `boundProgram` and `callGraph` |
| Schema validation | `jsonschema.validate()` passes with no errors |
| `routineId` format | Internal: `[pkg.]name/KIND(N)` where KIND in {P,F,T,A,NP,NF,TM} |
| External node IDs | Free-form string, does NOT match the internal RoutineId pattern |
| Enum values | `kind` in {PROCEDURE,FUNCTION,...}, `status` in {EXACT,AMBIGUOUS,...}, `access` in {READ,WRITE,READ_WRITE} |

### Common Schema Violations to Watch For

- Missing `routineId` or `name` on CallGraphNode
- `resolvedTo` missing when `status` is `EXACT`
- `sourceAnchor` with missing required fields (`startLine`, `startCol`, `endLine`, `endCol`)
- Negative line/column numbers

---

## Step 3: Verify Call Relationships Against Source

This is the core verification step. It compares the call graph edges against the
actual procedure/function calls in the source code.

### 3.1 Extract Summary Statistics

```bash
python3 << 'PYEOF'
import json
from collections import Counter

data = json.load(open('<OUTPUT_JSON_FILE>'))
bp = data['boundProgram']
cg = data['callGraph']

print(f"Routines declared: {len(bp['routines'])}")
print(f"Routine refs total: {len(bp['routineRefs'])}")
print(f"Call graph nodes: {len(cg['nodes'])}")
print(f"Call graph edges: {len(cg['edges'])}")
print(f"Object refs (tables): {bp['objectRefs']}")

# Binding status distribution
statuses = Counter(r['status'] for r in bp['routineRefs'])
for s, c in sorted(statuses.items()):
    print(f"  {s}: {c}")

# Count internal vs external nodes
internal = [n for n in cg['nodes'] if '/' in n['routineId'] and '(' in n['routineId']]
external = [n for n in cg['nodes'] if '/' not in n['routineId'] or '(' not in n['routineId']]
print(f"Internal nodes: {len(internal)}, External nodes: {len(external)}")
PYEOF
```

### 3.2 Cross-Reference Procedures Declared vs Source

Verify that every `PROCEDURE`/`FUNCTION` declared in the source appears
in `boundProgram.routines`.

```bash
# Extract procedure/function names from source
# Oracle PL/SQL:
grep -n '^\s*PROCEDURE\|^\s*FUNCTION' <INPUT_SQL_FILE>
# SQL Server T-SQL:
grep -ni '^\s*CREATE\s\+PROC\|^\s*CREATE\s\+FUNCTION' <INPUT_SQL_FILE>

# Compare against routines in JSON
python3 -c "
import json
data = json.load(open('<OUTPUT_JSON_FILE>'))
for r in data['boundProgram']['routines']:
    print(f\"{r['routineId']:50s}  kind={r['kind']}  params={r['paramCount']}\")
"
```

**Check:** Every procedure/function in the `grep` output should have a
corresponding entry in the routines list. If a routine is missing, the
symbol collector visitor is not visiting that AST node type
(`PlsqlSymbolCollector` for Oracle, `MssqlSymbolCollector` for SQL Server).

### 3.3 Spot-Check Call Edges for Key Procedures

For each procedure (start with the most important ones — entry points, orchestrators),
extract the actual call statements from source and compare against the call graph edges.

```bash
# Get all callees for a specific procedure
python3 -c "
import json
data = json.load(open('<OUTPUT_JSON_FILE>'))
caller_id = 'pkg_name.proc_name/NP(N)'  # <-- change this
edges = [e for e in data['callGraph']['edges'] if e['caller'] == caller_id]
unique_callees = sorted(set(e['callee'] for e in edges))
print(f'Edges from {caller_id}: {len(edges)} total, {len(unique_callees)} unique callees')
for c in unique_callees:
    count = sum(1 for e in edges if e['callee'] == c)
    print(f'  -> {c}  (x{count})')
"
```

Then read the corresponding procedure body in the source and manually identify
every call statement. Compare the two lists.

**What counts as a call:**
- Bare procedure call: `proc_name(arg1, arg2);`
- Qualified call: `pkg.proc_name(arg1);` (Oracle) or `schema.proc_name` (MSSQL)
- Function call in expression: `v := func_name(arg1);`
- Function call in SQL: `SELECT func_name(col) FROM ...`
- **Parameterless function call: `v := pkg.func_name;`** (no parentheses — valid PL/SQL, Oracle only)
- CALL statement: `CALL proc_name(arg1);`
- EXEC/EXECUTE statement: `EXEC proc_name @p1 = val;` (MSSQL)

**What does NOT count as a call:**
- Variable references: `v_local_var`
- Record field access: `rec.field_name`
- Type references: `%TYPE`, `%ROWTYPE`

### 3.4 Systematic Call Extraction from Source

For thorough verification, extract all calls programmatically:

```bash
# Find all procedure call patterns in a specific procedure body
# (between PROCEDURE name and its END name;)
sed -n '/PROCEDURE proc_name/,/END proc_name/p' <INPUT_SQL_FILE> | \
  grep -oP '[a-zA-Z_][a-zA-Z0-9_]*(\.[a-zA-Z_][a-zA-Z0-9_]*)*\s*\(' | \
  sort | uniq -c | sort -rn
```

**Note:** This regex-based extraction will miss parameterless function calls
(no parentheses). Those must be identified by reading the assignment statements
in the source.

### 3.5 Record Findings

For each procedure verified, record:

| Procedure | Edges in Graph | Calls in Source | Missing | Spurious | Notes |
|-----------|---------------|----------------|---------|----------|-------|
| `pkg.main` | 6 | 6 | 0 | 0 | Complete |
| `pkg.process` | 18 | 19 | 1 | 0 | Missing: `pkg.get_msg` (parameterless) |

---

## Step 4: Diagnose and Fix Problems

When missing or incorrect edges are found, diagnose the root cause in the IR
builder code and fix it.

### 4.1 Problem Classification

| Problem Type | Symptom | Likely Root Cause |
|-------------|---------|-------------------|
| Missing internal call | Edge not present for call to a known routine | Symbol collector not visiting the AST node type |
| Missing external call | Edge not present for call to external package | Same as above |
| Parameterless function call not detected | `v := pkg.func;` not captured (Oracle only) | AST represents this as a field access, not a `TFunctionCall` |
| Duplicate edges | Same caller→callee edge appears multiple times | Multiple call sites (correct), or visitor visiting same node twice (bug) |
| Wrong caller attribution | Edge attributed to wrong enclosing routine | `owningRoutineId` not correctly tracked on the scope stack |
| Missing routine declaration | Routine in source but not in `boundProgram.routines` | Missing `preVisit`/`postVisit` for the routine's AST type |
| Wrong parameter count | `paramCount` doesn't match source | Parameter extraction logic in `preVisit(TPlsqlCreateProcedure)` |

### 4.2 Key Source Files

**Oracle (PL/SQL):**

| File | Role | When to Modify |
|------|------|----------------|
| `gsp_java_core/.../ir/builder/oracle/PlsqlSymbolCollector.java` | AST visitor that collects symbols and refs | Missing calls, wrong attribution |
| `gsp_java_core/.../ir/builder/oracle/RoutineRefResolver.java` | Matches refs to symbols | Wrong resolution, EXACT vs UNRESOLVED |
| `gsp_java_core/.../ir/builder/oracle/OracleBoundIRBuilder.java` | Orchestrates building | Phase ordering issues |

**SQL Server (T-SQL):**

| File | Role | When to Modify |
|------|------|----------------|
| `gsp_java_core/.../ir/builder/mssql/MssqlSymbolCollector.java` | AST visitor that collects symbols and refs | Missing calls, wrong attribution |
| `gsp_java_core/.../ir/builder/mssql/MssqlRoutineRefResolver.java` | Matches refs to symbols | Wrong resolution, EXACT vs UNRESOLVED |
| `gsp_java_core/.../ir/builder/mssql/MssqlBoundIRBuilder.java` | Orchestrates building | Phase ordering issues |

**Shared (all vendors):**

| File | Role | When to Modify |
|------|------|----------------|
| `gsp_java_core/.../analyzer/v2/callgraph/CallGraph.java` | Builds graph from BoundProgram | Missing/wrong edges in graph construction |
| `gsp_java_core/.../analyzer/v2/callgraph/TableAccessExtractor.java` | Extracts table access info | Wrong READ/WRITE classification |
| `gsp_java_core/.../analyzer/v2/AnalyzerJsonExporter.java` | JSON serialization | Output format issues |
| `gsp_demo_java/.../demos/callgraph/CallGraphDemo.java` | CLI entry point, vendor dispatch | Adding new vendor support |

### 4.3 Diagnosis Workflow

1. **Identify the AST node type** for the missed call:
   ```java
   // Add temporary debug logging in the vendor's symbol collector
   // (PlsqlSymbolCollector for Oracle, MssqlSymbolCollector for MSSQL)
   @Override
   public void preVisit(TBasicStmt node) {
       System.err.println("DEBUG TBasicStmt: " + node.toString().substring(0, Math.min(80, node.toString().length())));
       // ... existing code
   }
   ```

2. **Check if the AST even produces the expected node type.** Write a small test:
   ```java
   // Oracle example:
   TGSqlParser parser = new TGSqlParser(EDbVendor.dbvoracle);
   parser.sqltext = "CREATE OR REPLACE PACKAGE BODY pkg AS\n"
       + "  PROCEDURE p IS v VARCHAR2(10);\n"
       + "  BEGIN v := other_pkg.func_no_parens; END p;\n"
       + "END pkg;";
   parser.parse();
   // Walk the AST and print all node types

   // MSSQL example:
   TGSqlParser parser = new TGSqlParser(EDbVendor.dbvmssql);
   parser.sqltext = "CREATE PROCEDURE dbo.my_proc AS\n"
       + "BEGIN\n  EXEC dbo.other_proc @p1 = 1;\nEND;";
   parser.parse();
   ```

3. **Determine the fix.** Common fixes:

   - **Add a new `preVisit` override** if a node type is not being visited
   - **Handle assignment RHS expressions** — walk the expression tree to find
     dotted names that are function calls without parentheses
   - **Fix scope tracking** if `owningRoutineId` is wrong

4. **Implement the fix** in the appropriate source file.

5. **Rebuild:**
   ```bash
   mvn install -N && mvn clean install -pl gsp_java_core -Pquick_install
   mvn compile -pl gsp_demo_java
   ```

### 4.4 Example: Parameterless Function Calls (Oracle Only)

This is a known gap discovered during our initial Oracle verification.

**Symptom:**
```sql
-- In PL/SQL source:
v_msg := zn_msg.get_last_message;   -- no parentheses
```
This call is **not captured** in the call graph. Meanwhile:
```sql
zn_msg.set_token('X', 'Y');        -- with parentheses
```
This call **is captured** correctly.

**Root cause:**

PL/SQL allows calling zero-parameter functions without parentheses. The Oracle
parser represents `pkg.func` (without parens) differently from `pkg.func()`
(with parens):

- `pkg.func()` → AST produces `TFunctionCall` node → captured by
  `preVisit(TFunctionCall)` or via `TBasicStmt` with `function_t` expression
- `pkg.func` (no parens) → AST produces a dotted-name `TExpression`
  (`objectaccess_t`) → looks identical to a record field access like
  `my_record.field`, so `PlsqlSymbolCollector` cannot distinguish them

When the expression appears on the RHS of an assignment (`v := pkg.func;`),
it is part of an `TAssignStmt`, not a `TBasicStmt`. The collector's
`preVisit(TBasicStmt)` never fires for assignment targets.

**Fix approach:**

Add a `preVisit(TAssignStmt)` to `PlsqlSymbolCollector` that examines the RHS
expression. When the RHS is a dotted-name expression (`objectaccess_t`) matching
the pattern `identifier.identifier` (and there is a known routine with that
qualified name), emit a `BoundRoutineRef` for it.

Alternatively, since we cannot always distinguish `pkg.func` from
`record.field` at parse time, emit a low-confidence `BoundRoutineRef` for all
dotted-name expressions in assignment RHS, and let the `RoutineRefResolver`
upgrade the confidence if a matching routine symbol is found.

**Files to modify:**
- `PlsqlSymbolCollector.java` — add `preVisit(TAssignStmt)` or enhance
  expression walking
- Potentially `RoutineRefResolver.java` — handle low-confidence candidates

---

## Step 5: Re-run and Re-verify

After fixing, repeat from Step 1:

```bash
# 1. Rebuild
mvn install -N && mvn clean install -pl gsp_java_core -Pquick_install
mvn compile -pl gsp_demo_java

# 2. Re-generate call graph
mvn -pl gsp_demo_java exec:java \
  -Dexec.mainClass="gudusoft.gsqlparser.demos.callgraph.CallGraphDemo" \
  -Dexec.args="/f <INPUT_SQL_FILE> /o <OUTPUT_JSON_FILE>" -q

# 3. Re-validate schema
python3 -c "
import json, jsonschema
schema = json.load(open('gsp_java_core/doc/v5/callgraph-json-schema.json'))
data = json.load(open('<OUTPUT_JSON_FILE>'))
jsonschema.validate(instance=data, schema=schema)
print('Schema validation: PASSED')
"

# 4. Re-verify content (focus on previously-missing edges)
python3 -c "
import json
data = json.load(open('<OUTPUT_JSON_FILE>'))
# Check if the previously-missing edge now exists
edges = data['callGraph']['edges']
missing_check = [e for e in edges
    if e['caller'] == 'pkg.caller/NP(0)'
    and e['callee'] == 'other_pkg.func_name']
print(f'Previously missing edge: {\"FOUND\" if missing_check else \"STILL MISSING\"}'  )
"
```

### Termination Criteria

The loop terminates when:

1. **Schema validation passes** with zero errors
2. **All declared routines** in the source appear in `boundProgram.routines`
3. **All call relationships** identified in the source appear as edges in `callGraph.edges`
4. **No spurious edges** — every edge corresponds to an actual call in the source
5. **Table accesses are correct** — READ for SELECT, WRITE for INSERT/DELETE,
   READ_WRITE for UPDATE

---

## Appendix A: Verification Script Template

A single script that automates Steps 1-3:

```bash
#!/bin/bash
# callgraph_verify.sh - Generate and verify call graph
# Usage: ./callgraph_verify.sh <input_sql> [vendor] [output_json]
# vendor: oracle (default), mssql

set -e

INPUT_SQL="${1:?Usage: $0 <input_sql> [vendor] [output_json]}"
VENDOR="${2:-oracle}"
OUTPUT_JSON="${3:-/tmp/callgraph_output.json}"
SCHEMA="gsp_java_core/doc/v5/callgraph-json-schema.json"

echo "=== Step 1: Generate call graph (vendor=$VENDOR) ==="
mvn -pl gsp_demo_java exec:java \
  -Dexec.mainClass="gudusoft.gsqlparser.demos.callgraph.CallGraphDemo" \
  -Dexec.args="/f $INPUT_SQL /o $OUTPUT_JSON /t $VENDOR" -q 2>&1

echo "=== Step 2: Validate schema ==="
python3 -c "
import json, jsonschema, sys
schema = json.load(open('$SCHEMA'))
data = json.load(open('$OUTPUT_JSON'))
try:
    jsonschema.validate(instance=data, schema=schema)
    print('Schema validation: PASSED')
except jsonschema.ValidationError as e:
    print('Schema validation: FAILED')
    print('Error:', e.message)
    sys.exit(1)
"

echo "=== Step 3: Summary ==="
python3 << 'PYEOF'
import json
from collections import Counter

data = json.load(open('$OUTPUT_JSON'))
bp = data['boundProgram']
cg = data['callGraph']

print(f"Routines: {len(bp['routines'])}")
print(f"Routine refs: {len(bp['routineRefs'])}")
print(f"Nodes: {len(cg['nodes'])}")
print(f"Edges: {len(cg['edges'])}")
print(f"Object refs: {bp['objectRefs']}")

statuses = Counter(r['status'] for r in bp['routineRefs'])
for s, c in sorted(statuses.items()):
    print(f"  {s}: {c}")

# List unique internal callers
internal_callers = sorted(set(e['caller'] for e in cg['edges']))
print(f"\nInternal callers ({len(internal_callers)}):")
for c in internal_callers:
    edge_count = sum(1 for e in cg['edges'] if e['caller'] == c)
    unique_callees = len(set(e['callee'] for e in cg['edges'] if e['caller'] == c))
    print(f"  {c:50s}  edges={edge_count:3d}  unique_callees={unique_callees}")
PYEOF

echo ""
echo "Output: $OUTPUT_JSON"
echo "Next: verify edges against source (Step 3.3)"
```

---

## Appendix B: Known Issues and Fixes Log

Record each issue found and its resolution for future reference.

| # | Date | Input File | Issue | Root Cause | Fix | Status |
|---|------|-----------|-------|------------|-----|--------|
| 1 | 2026-03-03 | zin.sql | `zn_msg.get_last_message` (14 call sites across 11 procedures) not captured | PL/SQL parameterless function calls produce `objectaccess_t` expression instead of `TFunctionCall` node; `PlsqlSymbolCollector` only handles `TFunctionCall` and `TBasicStmt` with `function_t` | Add handling for dotted-name expressions in assignment RHS | Open |

---

## Appendix C: Skill Automation Notes

When converting this workflow into an automated skill, the skill should:

1. **Accept inputs:** Procedural SQL file path, optional output path, db vendor (`oracle` or `mssql`)
2. **Execute Steps 1-3 automatically** (generate, validate, extract summary)
3. **Perform semantic verification** by:
   - Parsing the source to extract all `PROCEDURE`/`FUNCTION` declarations
   - For each procedure body, extracting all call statements (with and without
     parentheses, qualified and unqualified)
   - Comparing against the call graph edges
   - Reporting missing/spurious edges with source line numbers
4. **Diagnose root causes** by classifying missing edges into known categories
   (see Step 4.1) and suggesting which source files to modify
5. **Optionally implement fixes** in the IR builder code
6. **Re-run the loop** until termination criteria are met (Step 5)

The skill should track its progress in a findings table (like Appendix B) and
produce a final report with pass/fail status per procedure.
