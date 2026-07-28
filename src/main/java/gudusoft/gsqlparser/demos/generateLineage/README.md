# generateLineage

`GenerateLineageExpected` reads a YAML file of lineage test cases and
regenerates the expected-result blocks in it, so the library's lineage tests can
be re-baselined after parser output changes.

## Usage

```bash
mvn -q exec:java \
    -Dexec.mainClass=gudusoft.gsqlparser.demos.generateLineage.GenerateLineageExpected \
    -Dexec.args="<path to a *_lineage_test_cases.yaml>" \
    -Dexec.classpathScope=compile
```

The YAML it operates on is the library's own lineage fixture data
(`dlineageTest/*_lineage_test_cases.yaml` in `gsp_java_core`), not anything
shipped here, so you need a checkout of the library to give it real input.

> This folder used to carry its own `pom.xml`, and this file used to be a single
> `java -cp` line invoking `target/generateLineage-1.0.jar` alongside
> `../../../../../../sqlflow_java_library/target/gsqlparser-3.1.0.2.jar`, with a
> hardcoded `C:\depot\gitee\…` YAML path. None of it resolves: there is no
> `sqlflow_java_library` beside this repository and no `3.1.0.2` parser jar in
> it. The POM has been removed; the demo compiles with the root build like every
> other demo.
