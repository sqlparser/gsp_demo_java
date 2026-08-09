package gudusoft.gsqlparser.demosTest;

import gudusoft.gsqlparser.demos.formatsql.formatsql;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FormatSqlDemoTest {

    @Test
    public void tolerantModeFormatsValidAndInvalidRegions() throws Exception {
        File sqlFile = File.createTempFile("gsp-formatsql-invalid-", ".sql");
        try {
            Files.write(sqlFile.toPath(),
                    "select 1 from dual; select from where; select 2 from dual;"
                            .getBytes(StandardCharsets.UTF_8));

            ByteArrayOutputStream stdout = new ByteArrayOutputStream();
            ByteArrayOutputStream stderr = new ByteArrayOutputStream();
            int exitCode = formatsql.run(
                    new String[]{sqlFile.getAbsolutePath(), "/t", "oracle", "/tolerant"},
                    new PrintStream(stdout, true, "UTF-8"),
                    new PrintStream(stderr, true, "UTF-8"));

            String formatted = stdout.toString("UTF-8").replace("\r\n", "\n");
            String diagnostics = stderr.toString("UTF-8");
            assertEquals(0, exitCode);
            assertTrue(formatted.contains("SELECT 1\nFROM   dual;"));
            assertTrue(formatted.contains("SELECT FROM\nWHERE;"));
            assertTrue(formatted.contains("SELECT 2\nFROM   dual;"));
            assertTrue(diagnostics.contains("Formatter status: OK_WITH_RECOVERY"));
        } finally {
            Files.deleteIfExists(sqlFile.toPath());
        }
    }
}
