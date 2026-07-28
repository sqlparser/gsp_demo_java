# analyze_sp fixtures

The four SQL Server scripts here are the input for
`gudusoft.gsqlparser.demosTest.analyzespTest`, which exercises the `analyzesp`
demo (`Analyze_SP`) and its stored-procedure relation output.

They are copied verbatim, byte for byte, from the parser library's shared test
corpus:

```
gsp_java/gsp_java_core/gsp_sqlfiles/TestCases/private/sqlscripts/analyze_sp/
```

| file | exercises |
|---|---|
| `sample1.sql` | temp tables, cursors, `Create` / `Insert` / `Update` / `Read` / `Drop` relations |
| `sample6.sql` | one table written three ways, plus a `Delete` |
| `sample7.sql` | dynamic SQL that yields no static relations at all |
| `sample8.sql` | joins across four tables feeding a temp table |

## Why they are checked in here

They used to be read from that corpus over a relative path, which only resolved
when `gsp_java` happened to be checked out beside this repository. It also had
the path one directory level short, so it resolved nowhere: `Analyze_SP` never
found an input file, returned an empty string, and the comparison against the
expected output failed. Three of these tests were red for a long time and were
documented as the parser's output having drifted from stale golden strings.
They had not drifted. The expected strings match the current parser's output
exactly, character for character.

Checking the scripts in removes the sibling-checkout requirement entirely, so
the tests run on CI and in a plain clone.

## Editing them

The expected output in `analyzespTest` is matched exactly. Changing a table,
column or procedure name here changes the relation strings the test compares
against, so regenerate those in the same commit. `sample7.sql` is expected to
produce **no** output; that is the assertion, not an oversight.
