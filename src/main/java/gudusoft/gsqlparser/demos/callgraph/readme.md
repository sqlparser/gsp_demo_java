## Description

Builds a call graph for a PL/SQL package: every routine it declares, how those
routines call each other, and where each one sits in the source. Emitted as
JSON so it can feed a visualiser or an impact-analysis step.

## Usage

```
java CallGraphDemo /f <path_to_sql_file> [/o <output file path>]
```

```bash
mvn -q exec:java -Dexec.mainClass=gudusoft.gsqlparser.demos.callgraph.CallGraphDemo \
    -Dexec.args="/f samples/callgraph/sample_package.sql" -Dexec.classpathScope=compile
```

```json
{
  "boundProgram": {
    "routines": [
      {"routineId": "emp_pkg.log_action/NP(1)", "kind": "NESTED_PROCEDURE", "name": "log_action",
       "package": "emp_pkg", "paramCount": 1,
       "sourceAnchor": {"startLine": 3, "startCol": 5, "endLine": 6, "endCol": 19}},
      ...
    ],
    "objectRefs": 5,
```

`routineId` encodes package, name, kind (`NP` nested procedure, `NF` nested
function) and arity, so overloads stay distinct. `sourceAnchor` is the routine's
span in the input file.

`samples/callgraph/sample_package.sql` is a small Oracle package kept for this
demo.
