package gudusoft.gsqlparser.commonTest;

import java.io.File;

/**
 * Locations of the shared SQL corpus the tests read their inputs from.
 *
 * <p>The corpus is not in this repository. It lives in the parser library
 * repository, {@code gsp_java}, under {@code gsp_java_core/gsp_sqlfiles/}, and
 * is reached by a relative path from this checkout, so it only resolves when
 * the two repositories sit side by side:
 *
 * <pre>
 *   github/
 *     gsp_java/          &lt;- library, owns gsp_java_core/gsp_sqlfiles/
 *     gsp_demo_java/     &lt;- this repository
 * </pre>
 *
 * <p>The path below used to read {@code ../gsp_java_core/}, one level short: it
 * named a sibling of this checkout rather than the library's module inside
 * {@code gsp_java}, so it resolved to nothing. The three {@code analyzespTest}
 * cases that read from here therefore got no input file, {@code Analyze_SP}
 * returned an empty string, and comparing that with the expected output failed.
 * Those failures were written up in the README as output drift against an older
 * parser build, which pointed everyone at the wrong thing: the expected strings
 * match the current parser's output exactly, character for character.
 * Corrected on 2026-07-28.
 *
 * <p>Tests reading from here must call {@link #sqlFilesAvailable()} and skip
 * themselves when it returns false. The corpus sits under a directory named
 * {@code private} and is not published, so it is absent on CI and in any clone
 * that does not also have the library checked out beside it.
 */
public class gspCommon {
    public static String BASE_SQL_DIR = "../gsp_java/gsp_java_core/gsp_sqlfiles/TestCases/";
    public static String BASE_SQL_DIR_PUBLIC = BASE_SQL_DIR+"public/";
    public static String BASE_SQL_DIR_PRIVATE = BASE_SQL_DIR+"private/";
	public static String BASE_SQL_DIR_PUBLIC_ALLVERSIONS = BASE_SQL_DIR_PUBLIC+"allversions/";
    public static String BASE_SQL_DIR_PUBLIC_JAVA = BASE_SQL_DIR_PUBLIC+"java/";
    public static String BASE_SQL_DIR_PRIVATE_ALLVERSIONS = BASE_SQL_DIR_PRIVATE+"allversions/";
    public static String BASE_SQL_DIR_PRIVATE_JAVA = BASE_SQL_DIR_PRIVATE+"java/";

    /** Whether the shared SQL corpus is reachable from this checkout. */
    public static boolean sqlFilesAvailable() {
        return new File(BASE_SQL_DIR).isDirectory();
    }

    /** Skip message, naming what is missing and how to get it. */
    public static String whySqlFilesMissing() {
        return "shared SQL corpus not found at " + new File(BASE_SQL_DIR).getAbsolutePath()
                + " -- clone the gsp_java library repository beside this one to run these tests";
    }
}
