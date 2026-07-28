# Maintenance notes

Why this repository is shaped the way it is.

Most of what follows documents a concentrated cleanup between **2026-07-26 and
2026-07-28**, when this tree was merged with the library-side demo tree and then
made to build, test and run for someone outside Gudu. Almost every item here is
a bug that was invisible until something ran it, which is the recurring lesson:

> **Anything CI does not run is load-bearing only by luck.** Building is not
> running. If you add a script, a POM, or a workflow path, wire it into CI in
> the same commit — and make the check assert on output, not on exit status.

None of this is needed to *use* the demos; start with the
[README](../README.md). It is here for whoever maintains the repository next,
and because several of these mistakes are easy to make again.

## Contents

- [The 2026-07 cleanup at a glance](#the-2026-07-cleanup-at-a-glance)
- [One package root](#one-package-root)
- [The library-side test tree](#the-library-side-test-tree)
- [The "3 known failures" that were not](#the-3-known-failures-that-were-not)
- [samples/ and src/main/resources/](#samples-and-srcmainresources)
- [The vendored expression library](#the-vendored-expression-library)
- [Demos that needed a live database](#demos-that-needed-a-live-database)
- [dbConnect was removed](#dbconnect-was-removed)
- [Dependencies and security advisories](#dependencies-and-security-advisories)
- [pom_dlineage.xml: four breakages and a merge](#pom_dlineagexml-four-breakages-and-a-merge)
- [What putting the .bat scripts under CI found](#what-putting-the-bat-scripts-under-ci-found)
- [Two demos that crashed instead of explaining themselves](#two-demos-that-crashed-instead-of-explaining-themselves)
- [Change log](#change-log)
- [Appendix: organising multiple demos as Maven modules](#appendix-organising-multiple-demos-as-maven-modules)

## The 2026-07 cleanup at a glance

| | before | after |
|---|---|---|
| `.java` under `src/main/java` | 273 | 190 |
| files whose path contradicts their `package` | 263 | **0** |
| package roots | 4 | **1** |
| open Dependabot advisories | 28 | **0** |
| committed `.jar` files | 25 | **1** (in `lib-repo/`, as a resolvable artifact) |
| POMs building this tree | 2 | **1** |
| tests | 123 run, **3 failing** and documented as expected | **144 run, 0 failing, 0 skipped** |
| workflows | 1, build-only | 2, and they *run* what they build |

## One package root

`src/main/java` had grown four competing package roots — `demos.*`,
`gudusoft.*`, `gsp.demos.dlineage` and a stray `com.gudusoft.gsqlparser.demo`
(`App.java`, sitting loose at the very top of the source tree). Java's one hard
rule is that a file's directory path mirrors its package, and **263 of 273
files broke it**. Two packages were even split across both roots, so their
halves shared package-private access from unrelated directories.

There is now exactly one root, `gudusoft/`, and **path equals package for all
190 files**:

| | session start | now |
|---|---|---|
| `.java` under `src/main/java` | 273 | 190 |
| files whose path contradicts their `package` | 263 | **0** |
| package roots | 4 | **1** |
| packages split across roots | 2 | **0** |
| JDBC driver `.jar` files inside `src/main/java` | 10 | **0** |
| Maven projects nested in the compile root | 1 | **0** |

Everything moved to `gudusoft.gsqlparser.demos.<demo>`, which is the naming the
repository had been half-migrated toward for years. **This changes every
`-Dexec.mainClass` value**, so older instructions that say
`demos.checksyntax.checksyntax` need `gudusoft.gsqlparser.demos.checksyntax.checksyntax`.
The per-demo `readme.md` files, the `.bat` scripts and the CI workflow were all
updated with it.

A side effect worth knowing: the `.bat` scripts now work again. They had been
stale twice over — compiling `src\main\java\demos\<demo>\` and `cd`-ing up five
levels, both correct only before the demos moved under
`gudusoft/gsqlparser/demos/`. Since the rename had to touch them anyway, their
paths and directory depths were corrected to match where each script actually
sits (7 levels for most, 8 for the nested ones).


## The library-side test tree

`src/test/java/gudusoft/gsqlparser/` used to hold 222 files, but 204 of them
were a stale copy of the library's own test tree, carried in by the 2026/7/26
merge. They have been returned to `gsp_java_core`, which is where they belong
and where they actually work:

- They test the parser, not any demo here — none referenced demo code.
- They resolve fixtures through `gspCommon.BASE_SQL_DIR`, which points at
  `../gsp_java_core/gsp_sqlfiles/TestCases/`. That path does not exist relative
  to this repository, so their data was never found here.
- Most are named `testXxx.java` (lowercase), which Surefire's default include
  patterns (`Test*.java`, `*Test.java`, `*TestCase.java`) do not match. Only 3
  of the 204 were ever executed by `mvn test` here. `gsp_java_core`'s POM adds
  `**/test*.java`, so they run there.

169 were byte-identical to, or an older revision of, a file already in
`gsp_java_core` and were simply deleted; 35 existed only here and were moved
over. What remains are the 18 files that genuinely belong to this repository:
13 tests that import demo classes, `commonTest/SqlFileList.java` and
`commonTest/gspCommon.java` which those tests use, and three files kept back
because moving them would have required adding `org.jdom` and
`com.alibaba.fastjson` to the library's POM (`commonTest/testXmlXSD.java`,
`sqlenvTest/TJSONSQLEnv.java`, `sqlenvTest/testJSONEnv.java`).

`src/test/java/demos/visitors/` — the last test package still sitting outside
the `gudusoft/gsqlparser/` tree — has since been folded into it as
`gudusoft/gsqlparser/visitorsTest/`, following the `<demoName>Test` convention
the other directories already use (`joinConvertTest` covers the `joinConvert`
demo, `antiSQLInjectionTest` covers `antiSQLInjection`, and so on). All 20 test
files now live under one root.


## The "3 known failures" that were not

Until 2026-07-28 the README said three tests were expected to fail. They were
not.

> **Correction.** Until 2026-07-28 the README said three tests in
> `gudusoft.gsqlparser.demosTest.analyzespTest` failed because their expected
> output was written for an older parser and had since drifted, and that they
> were kept red on purpose as a drift signal. That was wrong, and it sent
> people looking at parser output for a bug that was never there.
>
> Those tests read stored procedures from the library's SQL corpus over a
> relative path, `gspCommon.BASE_SQL_DIR`. The path read `../gsp_java_core/`,
> one directory level short: it named a sibling of this checkout rather than
> the module inside `gsp_java`, so it resolved to nothing at all. `Analyze_SP`
> never found an input file, returned an empty string, and comparing that with
> the expected output failed. The expected strings match the current parser's
> output exactly, character for character, on all three. `testSample7` "passed"
> throughout only because it expects empty output, which is also what a missing
> file produces, so it was asserting nothing.
>
> The four scripts now live in `src/test/resources/sqlscripts/analyze_sp/`,
> copied byte for byte from the corpus, which removes the sibling-checkout
> requirement rather than just correcting it. See that directory's `readme.md`.

## samples/ and src/main/resources/

`src/main/java` is a *source* root: Maven compiles what is in it and does not
put anything else on the classpath. 229 files in there were not `.java`, and
that turned out to be hiding a real bug.

**`snowflake.js` was broken.** `SnowflakeSQLExtractor` loads it with
`getResourceAsStream("snowflake.js")`, but the file sat in `src/main/java`, so
Maven never copied it to `target/classes` — the call returned `null` and the
demo died on an NPE. It is the only classpath resource in the repository, and
it now lives at
`src/main/resources/gudusoft/gsqlparser/demos/snowflake/sqlextract/snowflake.js`,
mirroring its package so the same lookup resolves. (The demo also uses Nashorn,
removed from the JDK in 15, so it still needs Java 8–14 to run.)

**Sample SQL moved to `samples/`.** The 85 `.sql` files were input data passed
on the command line, never read from the classpath, so `src/main/resources` is
the wrong home for them too — they belong outside `src/` entirely:

```
samples/dlineageBasic/{oracle,mysql,mssql}/…   81 files
samples/tracedatalineage/                       2
samples/dlineage/                               1
samples/callgraph/                              1
```

**Build output deleted.** `demos/dlineage/class/` was created and `rm -rf`'d by
`buildJar.sh` on every run, and `data-lineage-result.xml` was a generated
lineage report nothing referenced. Both are now in `.gitignore`.

`buildJar.sh`, `buildJar.bat` and `demos/dlineage/MANIFEST.MF` went with them on
2026-07-28. All three existed to hand-assemble the same `data_flow_analyzer.jar`
that `maven-shade-plugin` now produces from the normal build, and all three
named files that do not exist: `buildJar.sh` copied
`../../../../../lib/gudusoft.gsqlparser.jar`, which was never in `lib/` under
that name, and the manifest's `Class-Path` listed
`lib/gudusoft.gsqlparser.jar` and `lib/sqlflow-exporter.jar`, neither of which
survived. Nothing in CI ran any of them, which is why nobody noticed.

What deliberately stays next to its demo: each demo's `readme.md`, its
`compile_*.bat` / `run_*.bat` (which `cd` relative to their own location, so
they cannot move), and a handful of per-demo assets (`tree-view.xsl` and
`tree-view.css`, referenced by relative href from generated XML;
`sqlflow-settings.png`; the dlineage PDF). Co-locating documentation and
per-demo tooling with the demo is the point of this repository's layout.


## The vendored expression library

`src/main/java/gudusoft/gsqlparser/demos/antiSQLInjection/` used to carry 365
`.java` files of a third-party expression evaluator under `org/boris/expr/`.
That was **57% of every source file in this repository**, and it duplicated
`lib/expr4j.jar`, which was already a declared dependency: the jar holds the
same 365 top-level classes and nothing else, so the library shipped twice and
javac quietly compiled the sources while the jar sat unused.

The sources are gone; the jar supplies `org.boris.expr`. Only one file ever
imported it (`GEval.java`), the `antiSQLInjection` tests cover the path, and
they pass against the jar. One consequence worth knowing: **`mvn package` no
longer puts `org/boris/expr/**` inside the project jar.** Nothing here consumes
that jar as a library, so this only matters if you start doing so. (The shaded
`-dlineage` jar does contain it, along with every other runtime dependency —
that is what an uber jar is.)

The dependency's coordinates were also wrong: it was declared as
`tk.pratanumandal:expr4j`, a different library entirely, which would have
pointed SBOM and vulnerability tooling at the wrong project. It now names what
is actually on disk, with a checksum recorded alongside it, since the jar
carries no version metadata of its own.

It is also the one dependency here with no coordinate in any public repository —
searched and confirmed — so it is served from `lib-repo/`, a file-based Maven
repository inside this checkout, laid out exactly like `~/.m2/repository`. See
[`lib-repo/readme.md`](../lib-repo/readme.md). It was a `<scope>system</scope>` jar
under `lib/` until 2026-07-28, which is worse than it sounds: `system` scope is
deprecated, invisible to Dependabot, **and skipped by `maven-shade-plugin`,
`maven-assembly-plugin` and `dependency:copy-dependencies` alike**, so it could
never appear in a packaged artifact or a generated classpath. That last part is
most of why the dlineage demo needed a hand-assembled `java -cp`
([#46](https://github.com/sqlparser/gsp_demo_java/issues/46)).

If Gudu ever uploads it to `https://www.sqlparser.com/maven/`, delete `lib-repo/`
and declare it normally — publishing to a repository you control is the better
answer, and this project already resolves from that one.


## Demos that needed a live database

Two demos used to be excluded because they read metadata straight out of a
running database over JDBC, and so needed a driver and a live server to do
anything. **Both are now in the build**, since 2026-07-28: they take that
metadata offline instead, which also let the two vendored jars behind it leave
`lib/`.

- `gettablecolumns/runGetTableColumn.java` — dropped its `/h /P /u /p` flags;
  supply a `TSQLEnv` in code or from JSON if you want column disambiguation
- `columninspect/ColumnInspect.java` — `/jdbc /u /p` became `/metadata <file>`,
  reading the same JSON the connection used to return. Every line of the
  inspection already worked off that JSON string, so nothing else changed.
  `samples/columninspect/` has a runnable metadata file and script

## dbConnect was removed

`dbConnect` was a **complete Maven project nested inside `src/main/java`** — its
own `pom.xml`, its own `src/main/java`, and 10 JDBC driver jars, all sitting in
the compile root of the project that contains it. It was moved out to
`connector/dbConnect/` and then deleted outright, because it had stopped being
revivable:

- It is written against `gudusoft.gsqlparser.sqlenv.util`, which the public
  parser no longer ships, so it needs an API migration rather than a rebuild.
- `pom.xml` excluded it from the build, and nothing else built it either.
- Its own parser dependency was pinned to `<version>latest</version>`, which
  Maven cannot resolve.
- Its 10 vendored JDBC drivers accounted for **16 of this repository's 28
  Dependabot advisories**, and none of them could be patched by upgrading a
  declared version, because the jars were files in git rather than resolvable
  dependencies.

Deleting it removed all 16 advisories, 10 binaries and 83 Java files in one go.
It is in git history if it ever needs reviving, but reviving it means the API
migration, not a checkout.

Moving it out first also resolved one of the two split packages: 16 of
`demos.sqlenv`'s files were dbConnect's and 1 (`runSQLEnv.java`) was not.


## Dependencies and security advisories

GitHub reported 28 open Dependabot advisories against this repository. They came
from one root cause: dependencies declared with `<scope>system</scope>` and a
`<systemPath>` into `lib/`. **Dependabot cannot patch those** — a system-scope
dependency is a file on disk, not something Maven resolves, so there is no
version for a bot to bump.

All 28 are now closed:

| where | count | what was done |
|---|---|---|
| `pom.xml` — `junrar` | 4 | `0.7` system-scope → `7.5.10` from Maven Central, `test` scope |
| `pom.xml` — `jdom` | 1 | every version of `org.jdom:jdom` is affected; migrated to the successor artifact `org.jdom:jdom2:2.0.6.1`, whose classes are `org.jdom2.*` |
| `pom.xml` — `junit` | 1 | `4.12` → `4.13.2` |
| `connector/snowflakeConnector` | 6 | `snowflake-jdbc` `3.12.9` → `4.3.2`, resolved from Central instead of a jar you drop into `lib/` |
| `connector/dbConnect` | 16 | module deleted ([see below](#dbconnect-was-removed)) |

Two things worth keeping in mind for next time:

- **`junrar` and `jdom` were only ever used by tests**, so they are `test` scope
  now. Both jars are gone from `lib/`.
- `fastjson` is pinned at `1.2.83` and is **not** flagged — that is the final
  1.x release, and it is where the 1.x deserialization advisories were fixed.
  Leave it, or move to `fastjson2`; do not "upgrade" it within 1.x.

**`lib/` is now gone entirely, and with it the last `system`-scope dependency.**
Three jars were left in it, believed to have no public coordinate. Two of them
did:

| jar | outcome |
|---|---|
| `simple-xml-2.7.1.jar` | on Maven Central as `org.simpleframework:simple-xml:2.7.1`, and **byte-for-byte identical** to the committed copy (`sha256 7a43d2d5…f4e429f9`). Declared normally; jar deleted. |
| `fastjson-1.2.83.jar` | on Maven Central as `com.alibaba:fastjson:1.2.83`, likewise byte-identical (`sha256 641a4d65…5fe692631d`). Declared normally; jar deleted. |
| `expr4j.jar` | genuinely not on Central under any groupId. Moved to `lib-repo/` as a real artifact, `org.boris:expr:0.0.0-vendored`. |

Since the two Central jars are the same bytes, that swap changed nothing at
runtime and gained three things: Dependabot can see and patch them, packaging
plugins stop skipping them, and their **transitive** dependencies now resolve —
`simple-xml` pulls in `stax` and `xpp3`, which under `system` scope were simply
absent, waiting to surface as a `NoClassDefFoundError` on some code path nobody
had exercised yet.

The rule that follows: **add a dependency by coordinate, not by file.** Check
Maven Central first — two of the three "no public coordinate" jars here were on
it all along. If it truly has no coordinate, publishing it to Gudu's own Maven
repository beats committing it; `lib-repo/` is the fallback for when nobody can
upload one.

`sqlflow-exporter.jar` and `sqlflow-library.jar` were dropped on 2026-07-28.
They supplied `gudusoft.dbadapter.T<Vendor>SQLDataSource`, which two demos used
to read table metadata out of a running database — so both demos needed a live
server and a JDBC driver, and `pom.xml` excluded them from the build for exactly
that reason. Both now take their metadata offline instead
(`runGetTableColumn` via `TSQLEnv`, `ColumnInspect` via a `/metadata` JSON file),
so both build and run like every other demo, and the jars had nothing left
holding them in. Two dead helpers went with them, `dlineage/SqlflowIngester.java`
and `dlineage/DataSourceProvider.java`: neither had a `main`, and the only
reference to either in the tree was a commented-out call in `DataFlowAnalyzer`.

Eight more jars used to sit alongside them, 5.5 MB of the directory's 7.1 MB.
None was declared by any POM, imported by any source, or named by any `.bat`
script, so nothing resolved them — `lib/` was just where they had been dropped:

| removed | why it was dead |
|---|---|
| `lib/jdbc/ojdbc-1.1.1.jar`, `lib/jdbc/sqljdbc4-4.0.jar` | the connector POMs resolve `${project.basedir}/lib/`, which is `connector/<module>/lib/` — never this directory. Download the driver into the module's own `lib/`, as its readme says. |
| `lib/proguard/proguard.jar`, `proguard.pro` | byte-identical to `gsp_java_core/proguard/`, and the `.pro` obfuscates `gudusoft.gsqlparser.jar` — the *library* artifact. Release tooling for the other repository. |
| `lib/jdk1.5/junit-4.5.jar`, `junit.jar` | JUnit comes from Maven at `4.13.2`. |
| `lib/commons-logging-1.1.3.jar` | referenced only by `fastjson`'s `support/spring/*` adapters; there is no Spring here. |
| `lib/jarLoader.jar` | Eclipse's jar-in-jar export loader, used by the IDE wizard, not by any build. |

Vendored driver jars in particular should not come back. They are invisible to
Dependabot for the same reason the system-scope entries above were, so they age
in place with no bot to flag them.

The one exception, `lib-repo/`, is deliberately not a place to drop jars: it is a
Maven repository with a strict layout, every artifact needs a hand-written
`.pom`, and its readme says to check Central and Gudu's own repository first.
The friction is the point.


## pom_dlineage.xml: four breakages and a merge

Until 2026-07-28 this was a second POM, `pom_dlineage.xml`, built with
`mvn -f pom_dlineage.xml package`. It broke four times, and **every one of them
was invisible to a green build**, because until the very end nothing in CI ran
the thing — and for most of that time nothing even built it.

1. **It pinned a parser jar from 2019.** It declared
   `lib/gsqlparser-3.1.1.0.jar` on `system` scope while `DataFlowAnalyzer` had
   moved on to `getOption().setTraceTablePosition(...)` and
   `ProcessUtility.generateColumnLevelLineageCsvSimple(...)`, neither of which
   that jar has.
2. **It kept a `<parent>` published nowhere**, `gudusoft:gsp_java:1.0-SNAPSHOT`,
   the private library reactor. Anyone without that POM in their local
   repository got `Non-resolvable parent POM` before Maven read a line of the
   file. It looked fine to everyone at Gudu, who all have the library checked
   out. The root `pom.xml` had been cut loose from that parent years earlier;
   this one was missed.
3. **[#46](https://github.com/sqlparser/gsp_demo_java/issues/46) — the
   documented run command could not work.** It put `external_lib/*` on the
   classpath, a directory only the Windows `.bat` route ever creates, so anyone
   following the Maven instructions in order hit `NoClassDefFoundError` on the
   first command in the section. The jar Maven had actually downloaded sat in
   `~/.m2`, under a filename containing a version number that changes with every
   release, so it could not be guessed either.
4. **[#47](https://github.com/sqlparser/gsp_demo_java/issues/47) — it was
   missing the JAXB dependency `pom.xml` already had.** JAXB left the JDK after
   Java 8; `pom.xml` had carried `jakarta.xml.bind-api` and `jaxb-runtime` for
   ages, and `pom_dlineage.xml` had neither. Since `pom.xml` *excluded*
   `DataFlowAnalyzer` from its own build, this second POM was the only thing
   that ever compiled the class, so no other build path could catch it.

Merging it into `pom.xml` removes the category rather than the four instances.
There is no second POM to drift, no second parser version to forget, no second
dependency list to fall behind — and no
[#39](https://github.com/sqlparser/gsp_demo_java/issues/39) either, the bug
where the second POM's incremental-compile cleanup deleted the root build's
`target/classes` because both POMs shared `${project.basedir}`. One POM cannot
collide with itself, so `target-dlineage/` is gone too.

The exclusion that made all this necessary was circular: `DataFlowAnalyzer` was
excluded from the root build *because* a separate POM compiled it, and that POM
existed to produce a standalone jar. `maven-shade-plugin` produces the
standalone jar from the one build, so nothing is excluded from `pom.xml` any
more.

**What actually keeps it working now is that CI runs it.**
`.github/scripts/smoke-dlineage-jar.sh` executes the jar on
`samples/dlineage/demo.sql` in both `build.yml` and `nightly.yml`, on JDK 8 and
21, and asserts on the *output*: non-empty, parses, and contains lineage
relationships. That last part is not pedantry — in #47 the process created
`lineage.json`, died before writing a byte into it, and still exited `0`. A file
that exists and an exit code of zero were both, at that moment, lies. It checks
XML as well as JSON, because those are separate code paths and the JAXB one is
the one that broke.


## What putting the .bat scripts under CI found

They had been stale for years — compiling `src\main\java\demos\<demo>\` and
`cd`-ing up five levels, both correct only before the demos moved under
`gudusoft/gsqlparser/demos/`. Nothing noticed, because nothing ran them. Putting
them under CI turned up five faults, none of which anything else would have
caught:

| fault | scripts |
|-------|---------|
| Still compiled `src\main\java\demos\*.java`, a directory the package rename deleted | 2 |
| Doubled path `analyzesp\sybase\sybase\` | 1 |
| Named a package that no longer existed after `ColumnImpact` moved | 2 |
| Passed only their own folder to `javac`, so cross-demo imports failed to resolve — fixed with `-sourcepath src\main\java` | 6 |
| No `-encoding`, so Windows `javac` used the platform codepage against UTF-8 sources | 39 |

The last one is the reason a real Windows runner was worth the trouble. `javac`
there defaults to `Cp1252`, and two demos failed with `unmappable character for
encoding Cp1252`. `pom.xml` has always declared `project.build.sourceEncoding`
as UTF-8, so Maven was never affected, and on Linux the default is UTF-8 anyway
— simulating all 39 scripts there passed cleanly. It only reproduces with the
encoding forced.

Also deleted along the way: `TGetTableColumn_notUsed.java`, which redefined
three classes from the `TGetTableColumn.java` beside it and so broke any
wildcard compile of that folder, which is exactly how
`compile_gettablecolumns.bat` compiles it.


## Two demos that crashed instead of explaining themselves

Found on 2026-07-28 while documenting them, both invisible to CI at the time.

**`scriptwriter`** went straight to `sqlstatements.get(0)` without checking what
`parse()` returned. Its built-in query is a deliberately hard ~49 KB Oracle
statement, and **the trial parser refuses input over 10,000 bytes** — so with
the published jar the parse failed, the statement list was empty, and the demo
died with `IndexOutOfBoundsException: Index 0 out of bounds for length 0`. That
message names neither the real cause (a licensing limit) nor the fix. It now
checks the return code, prints the parser's own error, says so explicitly when
the input is over the limit, and takes an optional `<file.sql>` so it can
actually demonstrate something with the trial jar.

This is also why the README no longer claims the trial build is "enough for
every demo here". It is enough for every demo but this one.

**`events/ProcessSQLStatement`** had this as its input:

```java
String sqlfile = "C:\\Users\\DELL\\Downloads\\20240311110800487_mssql_sql\\data.sql";
```

A hardcoded absolute path into one developer's Downloads folder, so the demo
could not run anywhere else. The file was missing, `parse()` failed, and the
error branch then called `getErrormessage().substring(0, 1000)` unconditionally
on a message shorter than 1000 characters — throwing
`StringIndexOutOfBoundsException` from the code whose job was to report the
error. It now takes the file as an argument, prints a usage line without one,
and bounds the substring.

**The general check.** `run-all-demos.sh` matched only linkage failures
(`NoClassDefFoundError` and friends), so both of these passed. It now also fails
any demo that throws `Exception in thread "main"` when run with **no
arguments**: with nothing to work on, a demo should print its usage line, not a
stack trace. All 77 pass it.

A caution for whoever checks this next: the first survey run for this said "0 of
77 throw", and it was wrong — it used a relative `-cp target/classes` from
another working directory, so every demo failed to load and matched no pattern.
`run-all-demos.sh` builds an absolute classpath for exactly this reason.

## Change log

Newest first.

- **[2026/7/28]** `pom_dlineage.xml` merged into `pom.xml`; the standalone
  lineage tool is now a `maven-shade-plugin` uber jar produced by the normal
  build, and CI *runs* it on JDK 8 and 21 rather than only building it. Closed
  [#46](https://github.com/sqlparser/gsp_demo_java/issues/46) and
  [#47](https://github.com/sqlparser/gsp_demo_java/issues/47). See
  [pom_dlineage.xml: four breakages and a merge](#pom_dlineagexml-four-breakages-and-a-merge).
- **[2026/7/28]** `lib/` deleted. `simple-xml` and `fastjson` became ordinary
  Maven Central dependencies (byte-identical to the committed copies);
  `org.boris:expr`, the only one with no public coordinate, moved to
  `lib-repo/`. Last `<scope>system</scope>` dependency gone, so
  `-Dexec.classpathScope=compile` is no longer needed by any demo.
- **[2026/7/28]** `jaxb-runtime` `2.3.3` → `2.3.9`: 2.3.3 calls
  `sun.misc.Unsafe.defineClass`, removed in JDK 11, so XML lineage output
  silently produced a 0-byte file on every supported JDK except 8.
- **[2026/7/28]** `scriptwriter` and `events/ProcessSQLStatement` fixed: both
  crashed with an unhandled exception rather than reporting why.
  `run-all-demos.sh` now fails any demo that throws when run with no arguments.
- **[2026/7/28]** The "3 known failures" were a wrong fixture path, not parser
  drift. Fixtures checked in; the suite is 144 passing, 0 skipped, and
  self-contained. `connector/dbConnect` deleted, closing 16 advisories.
- **[2026/7/27]** One package root: all 190 sources are now
  `gudusoft.gsqlparser.demos.<demo>`, and path equals package everywhere. **This
  changed every `-Dexec.mainClass` value.** `snowflake.js` moved to
  `src/main/resources/`, where `getResourceAsStream` can actually find it.
- **[2026/7/26]** Merged with the library-side demo tree, which had diverged.
  That tree is authoritative, so its version wins wherever the two differed (61
  files). Nothing was dropped: the 282 files that existed only here — 143 under
  `demos/dbConnect`, 130 tests, and a few data files — were kept. Test count
  went from 123 to 153. Most demos moved to `src/main/java/gudusoft/gsqlparser/demos/`.
- **[2026/7/26]** The build no longer inherits from the private `gudusoft:gsp_java`
  parent POM, which was why nobody outside Gudu could build this repository.
  It now resolves `com.gudusoft:gsqlparser` from
  <https://www.sqlparser.com/maven/> and builds standalone.
- **[2024/9/17]** Test packages moved from `src/test/java/` to
  `src/test/java/gudusoft/gsqlparser/`, so unit tests now live under package
  names like `gudusoft.gsqlparser.xxxTest`. The demos followed later, in the
  2026/7/26 merge above, and that move is now complete — see
  [One package root](#one-package-root).

## Appendix: organising multiple demos as Maven modules

Design note carried over from the library-side tree. This describes a structure
the repository does **not** currently use; it is kept as guidance.

When a project contains multiple demos that need to be built separately, the
usual approach is a Maven multi-module project: one parent `pom.xml` with
`<packaging>pom</packaging>` and a `<modules>` section, plus a child `pom.xml`
per demo declaring its own dependencies and inheriting shared versions from the
parent's `<dependencyManagement>`.

```
gsp_demo_java/
├── pom.xml               <-- parent POM, manages the modules
├── dlineage-demo/
│   ├── pom.xml
│   └── src/main/java/demos/dlineage/DataFlowAnalyzer.java
└── another-demo/
    ├── pom.xml
    └── src/main/java/demos/another/AnotherDemo.java
```

Build everything with `mvn clean package` from the root, or a single module with
`mvn -pl dlineage-demo clean package`.
