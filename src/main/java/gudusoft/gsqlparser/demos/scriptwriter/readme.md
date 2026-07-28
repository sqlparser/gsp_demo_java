## Description

Rebuilds a SQL statement from its parse tree: parse, then write the tree back
out with `toScript()`.

This is the round-trip that any AST rewrite depends on: if the tree can be
regenerated faithfully, it can be modified first. See `modifysql` and
`sqlrefactor` for the modifying half.

## Usage

```
scriptwriter [<file.sql>] [/t <database type>]
```

With no arguments it uses a built-in Oracle query — a large, densely formatted
statement with optimiser hints, correlated scalar subqueries, `CASE`
expressions and a `UNION`, chosen to be harder than a toy example.

> ⚠️ **That built-in query is about 49 KB, and the trial parser refuses input
> over 10,000 bytes.** With the published `com.gudusoft:gsqlparser` jar the demo
> reports the limit and stops:
>
> ```text
> Failed to parse the built-in demo query:
> trial version can only process query with size of at most 10000 bytes, ...
> ```
>
> This is the trial restriction, not a parser bug. Pass your own smaller file,
> or use a licensed build.

So the runnable form is:

```bash
mvn -q exec:java -Dexec.mainClass=gudusoft.gsqlparser.demos.scriptwriter.scriptwriter \
    -Dexec.args="samples/dlineage/demo.sql"
```

```text
insert  into deptsal(dept_no,dept_name,salary)
 select
d.deptno,d.dname,SUM(e.sal+Nvl(e.comm,0))  as sal
 from
dept d
 left  join ( select
*
 from
emp
 where hiredate >  date '1980-01-01') e on e.deptno = d.deptno
 group  by d.deptno,d.dname;
```

(Run from the repository root, since the path is relative.)

Use `/t` for another dialect, e.g. `/t mssql`, `/t postgresql`. The default is
`oracle`.

## History

Until 2026-07-28 this demo took no arguments and went straight to
`sqlstatements.get(0)` without checking what `parse()` returned. Against the
trial parser that meant the size limit above surfaced as an
`IndexOutOfBoundsException` on an empty list — a stack trace that named neither
the real cause nor the fix. `.github/scripts/run-all-demos.sh` now fails any
demo that throws when run with no arguments.
