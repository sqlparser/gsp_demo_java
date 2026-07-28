## Description

The parse tree is walked with `TParseTreeVisitor`: you implement the visit
methods for the node types you care about and the parser drives the traversal.
This folder is a catalogue of that pattern, one small program per thing you
might want to find.

| Class | Finds |
|-------|-------|
| `SearchSelect` | `SELECT` statements, including nested ones |
| `searchStatement` | Statements by type |
| `searchSubquery` | Subqueries |
| `searchFunction` | Function calls |
| `searchDatatype` | Data types |
| `searchSQLObject` | Referenced database objects |
| `searchNode` | Arbitrary node types |
| `searchColumnInResultColumn` | Columns inside select-list items |
| `visitStarColumn` | `*` and `t.*` expansions |
| `searchCallInSP` | Calls made inside a stored procedure |
| `toXml` | Serialises the whole tree to XML |
| `toXmlOldVersion` | Earlier XML form, kept for comparison |

## Usage

Most take a file and an optional `/t`; run one with no arguments for its own
usage line.

```bash
mvn -q exec:java -Dexec.mainClass=gudusoft.gsqlparser.demos.visitors.SearchSelect \
    -Dexec.args="q.sql /t oracle" -Dexec.classpathScope=compile
```

```text
Selected SQL dialect: dbvoracle
SELECT a.id, b.name, 100 AS n FROM ta a JOIN tb b ON a.id = b.id WHERE a.x > 1 AND 'k' = 'k';
Time Escaped: 1354
```

`toXml` writes beside its input instead of to stdout:

```text
q.sql.xml was generated!
```

`tree-view.xsl` and `tree-view.css` in this folder style that XML for a browser;
the generated file references them by relative path, so keep them together.
`sqlschema.xsd` describes the XML shape.

There is a schema-validation test for `toXml` at
`src/test/java/gudusoft/gsqlparser/visitorsTest/`, currently excluded from the
build because it reads an XSD from outside this repository. See the root
`README.md`.
