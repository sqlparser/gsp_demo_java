# The demo programs

One directory per topic — syntax checking, formatting, lineage, AST traversal,
stored-procedure analysis, rewriting, dialect translation, and so on. Most
carry their own `readme.md`. Start from [the root README](../../../../../../README.md)
for the full tour; this page is only about how to run what is in here.

**The directory path under `src/main/java/` is the package.** Every class here
is `gudusoft.gsqlparser.demos.<demo>.<Class>`, so you can read the
`-Dexec.mainClass` value straight off the file's location without opening it.

## Maven (any platform)

Nothing to edit first. The parser and every other dependency resolve from
Maven, so a fresh clone runs a demo directly:

```bash
mvn package -DskipTests

mvn -q exec:java \
    -Dexec.mainClass=gudusoft.gsqlparser.demos.checksyntax.OfflineSyntaxCheck \
    -Dexec.args="/f samples/checksyntax/valid-mssql.sql /t mssql"
```

Argument conventions differ per demo — `checksyntax` takes `/f <file> /t <vendor>`,
`formatsql` takes a bare filename. **Run a demo with no arguments to see its
usage line.** Sample SQL lives in `samples/` at the repository root.

The one demo that does not run this way is `dlineage/DataFlowAnalyzer`: it
marshals its output with JAXB, which collides with `exec:java`'s classloader.
Run it from the packaged uber jar instead — see
[its readme](./dlineage/readme.md).

## Windows `.bat` scripts

The original no-Maven workflow, still maintained and exercised in CI on
`windows-latest`. Each demo directory has a `compile_<demo>.bat` and a
`run_<demo>.bat` — 39 and 50 of them respectively:

```
src\main\java\gudusoft\gsqlparser\demos\checksyntax\compile_checksyntax.bat
src\main\java\gudusoft\gsqlparser\demos\checksyntax\run_checksyntax.bat /f C:\data.sql /t oracle
```

`setenv\setenv.bat` bootstraps itself: it keeps an existing `JAVA_HOME`, and
when `external_lib\` holds no parser it calls `setenv\fetch-parser.bat`, which
runs `mvn dependency:copy-dependencies` to populate it. So Maven is needed once,
to fetch jars; after that the scripts are plain `javac`/`java`. No version is
named in any script — they read `pom.xml`.

> This page used to describe a third route: commenting out a `<parent>` block,
> switching a list of `system`-scope dependencies to `external_lib\`, and adding
> a vendored `lib/gudusoft.gsqlparser-3.0.1.5.jar`. None of that applies. The
> private parent POM, the `system` scopes, the vendored parser and the
> `sqlflow-*` jars were all removed during the 2026-07 and 2026-08 cleanups, and
> the `.bat` paths it quoted pointed into a second `demos` package root that no
> longer exists. Dependencies are ordinary Maven coordinates now; there
> is nothing to edit before building.
