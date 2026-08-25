Nothing needs to be downloaded here any more.

The JDBC driver used to be a `system`-scope dependency pointing into this
directory, so building meant fetching a jar by hand first. It is an ordinary
Maven Central coordinate now, declared in the module's `pom.xml`, and Maven
fetches it. This directory only survives because the jar plugin's manifest
still names `lib/` as its runtime classpath prefix, for assembling a runnable
jar by hand.

See `../../README.md`: these modules need a licensed parser regardless of the
driver.
