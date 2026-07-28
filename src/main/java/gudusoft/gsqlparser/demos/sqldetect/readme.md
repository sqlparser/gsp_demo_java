## Description

Breaks a script into its statements, queries and table references, each with
source positions and the action performed on it. The output is
pipe-delimited and grouped by record type, so it is easy to consume from
another tool rather than read by eye.

## Usage

```
java SQLDetect <scriptfile> [/o <output file path>] [/t <database type>]
```

`/o` writes to a file instead of stdout; `/t` defaults to `oracle`.

```bash
mvn -q exec:java -Dexec.mainClass=gudusoft.gsqlparser.demos.sqldetect.SQLDetect \
    -Dexec.args="q.sql /t oracle" -Dexec.classpathScope=compile
```

```text
sql(id|startpos|endpos|sqltype|)
1|1,1|1,94|SELECT|

query(id|startpos|endpos|sqlid|)
1|1,1|1,94|1|

table(id|tablename|tablealias|tablepos|action|queryid|)
1|ta|a|1,36|SELECT|1|
```

Positions are `line,column`. `queryid` links a table back to the query that
references it, and `sqlid` links a query back to its statement.
