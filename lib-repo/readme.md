# `lib-repo/` — a file-based Maven repository

This directory is a Maven repository, laid out exactly like `~/.m2/repository`
or Maven Central: `<groupId as path>/<artifactId>/<version>/<files>`. It is
declared in the root `pom.xml` as

```xml
<repository>
    <id>in-project-vendored</id>
    <url>${project.baseUri}lib-repo</url>
</repository>
```

It holds exactly one artifact, and it exists for exactly one reason: that
artifact has no coordinate in any public repository.

| coordinate | file | why it is here |
|---|---|---|
| `org.boris:expr:0.0.0-vendored` | `expr4j.jar`, renamed to the coordinate | not on Maven Central under any groupId; searched and confirmed |

## Why this and not `system` scope

These jars used to be `<scope>system</scope>` dependencies pointing at
`lib/*.jar`. That is deprecated, and it actively broke things:

- **`system`-scope artifacts are omitted from every packaging plugin.**
  `maven-shade-plugin`, `maven-assembly-plugin` and
  `dependency:copy-dependencies` all skip them. So no packaged jar and no
  generated classpath could ever contain them, which is why running the
  dlineage demo needed a hand-assembled `java -cp` with a version number in it
  ([#46](https://github.com/sqlparser/gsp_demo_java/issues/46)).
- **They are invisible to Dependabot**, so they age with nothing to flag them.
- They contribute no transitive dependencies, so a library that needs one
  silently gets a `NoClassDefFoundError` at runtime instead of a resolution
  error at build time.

Declared as a real repository, the artifact resolves, packages, and appears on
generated classpaths like anything else. Dependabot still cannot advise on it —
nothing can advise on a jar with no upstream identity — but that is now the only
remaining downside rather than one of four.

## Why not the other two jars that used to live in `lib/`

`simple-xml-2.7.1.jar` and `fastjson-1.2.83.jar` are **on Maven Central, and
byte-for-byte identical to the copies this repository was carrying**:

```
7a43d2d5e488e0dac57a6de0284df4413a4733365239649309c4b693f4e429f9  simple-xml-2.7.1.jar
641a4d65ab32fbfdccd9c718e3f83ebc4caabdb5e4fe5b3d51527c5fe692631d  fastjson-1.2.83.jar
```

so they are declared as ordinary `org.simpleframework:simple-xml:2.7.1` and
`com.alibaba:fastjson:1.2.83` dependencies and their vendored copies are gone.
Nothing belongs in here that has a public coordinate — check before adding.

## If you add something

Don't, if you can avoid it: prefer a public coordinate, and failing that,
prefer publishing to Gudu's own Maven repository at
`https://www.sqlparser.com/maven/`, which this project already resolves from.
That is where a jar like this really belongs; an in-project repository is the
fallback for when nobody can upload one.

If you must, the layout has to be exact or Maven will not see it:

```
lib-repo/<groupId with . replaced by />/<artifactId>/<version>/
    <artifactId>-<version>.jar
    <artifactId>-<version>.pom      # hand-written; list its own dependencies
    *.sha1  *.md5                   # optional, but Maven warns without them
```

Regenerate the checksums after any change:

```bash
cd lib-repo/org/boris/expr/0.0.0-vendored
for f in *.jar *.pom; do
    sha1sum "$f" | cut -d' ' -f1 > "$f.sha1"
    md5sum  "$f" | cut -d' ' -f1 > "$f.md5"
done
```
