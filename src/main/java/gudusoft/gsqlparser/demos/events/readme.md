## Description

Two callbacks the parser offers, letting you intervene *during* a parse rather
than walking the tree afterwards.

| Class | Callback | Purpose |
|-------|----------|---------|
| `ProcessSQLStatement` | `ISQLStatementHandle` | Fires per statement as a script is parsed, so a large script can be processed incrementally instead of held whole |
| `processTokenList` | `ITokenListHandle` | Fires on the token list before parsing, so text that is not legal SQL can be rewritten into something that is |

`processTokenList` exists for templated SQL. A script full of
`${tx_date_yyyymm}` placeholders will not parse, so the handler merges `$`, `{`,
name, `}` into a single identifier token first.

## Usage

Neither takes arguments; both are configured inline.

```bash
mvn -q exec:java -Dexec.mainClass=gudusoft.gsqlparser.demos.events.processTokenList \
    -Dexec.classpathScope=compile
```

> **Both demos fail as shipped.**
>
> `ProcessSQLStatement` reads a hardcoded path,
> `C:\Users\DELL\Downloads\20240311110800487_mssql_sql\data.sql`, left over from
> whoever wrote it. Point `sqlfile` at something real before running it.
>
> `processTokenList` currently stops with `syntax error … near: $`, so its token
> merging is not taking effect against this parser build. The callback wiring is
> still worth reading as an example of `setTokenListHandle`; the result is not.
