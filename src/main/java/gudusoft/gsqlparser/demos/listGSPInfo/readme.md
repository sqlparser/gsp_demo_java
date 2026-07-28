## Description

Prints which parser build you are actually running: version, release date,
whether it is the full or trial edition, and every SQL dialect it supports.

Useful as a first check when a demo behaves unexpectedly, since most surprises
come down to the parser version or the trial-vs-full distinction.

## Usage

Takes no arguments.

```bash
mvn -q exec:java -Dexec.mainClass=gudusoft.gsqlparser.demos.listGSPInfo.listGSPInfo \
    -Dexec.classpathScope=compile
```

```text
Version: 4.1.5.15,Release date: 2026-07-12, Full version:false
Supported DBs: 40/45,[dbvathena, dbvazuresql, dbvbigquery, ... dbvvertica]
```

`Full version:false` means the trial build. `40/45` is dialects implemented out
of dialects defined in `EDbVendor`.
