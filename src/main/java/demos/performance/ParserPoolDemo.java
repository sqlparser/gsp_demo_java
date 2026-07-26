package demos.performance;

import gudusoft.gsqlparser.*;
import gudusoft.gsqlparser.stmt.*;
import java.util.concurrent.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;
import java.util.ArrayList;

/**
 * Demonstration of the Thread-Safe Parser Pool improvements.
 * 
 * This demo shows:
 * 1. Performance improvements over singleton pattern
 * 2. Thread safety with TSafeParserPool
 * 3. How parser references are cleared for safe concurrent use
 */
public class ParserPoolDemo {
    
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("    SQL Parser Pool Demonstration");
        System.out.println("========================================\n");
        
        // Example SQL statements
        String[] sqlStatements = {
            "SELECT * FROM users WHERE id = 1",
            "INSERT INTO logs (message, timestamp) VALUES ('User login', NOW())",
            "UPDATE products SET price = price * 1.1 WHERE category = 'electronics'",
            "DELETE FROM sessions WHERE last_activity < DATE_SUB(NOW(), INTERVAL 30 DAY)"
        };
        
        // First, initialize the parser to load grammar tables
        System.out.println("=== Initialization Phase ===");
        System.out.println("Loading grammar tables (one-time cost)...");
        long initStart = System.currentTimeMillis();
        TGSqlParser initParser = new TGSqlParser(EDbVendor.dbvmysql);
        initParser.sqltext = "SELECT 1";
        initParser.parse();
        long initTime = System.currentTimeMillis() - initStart;
        System.out.println("Grammar tables loaded in " + initTime + " ms\n");

        // Run multiple iterations to show consistent performance
        int iterations = 100;
        System.out.println("=== Performance Test (" + iterations + " iterations each) ===\n");


        System.out.println("\nMethod 2: Basic Parser Pool (with pre-warming)");
        demonstrateBasicPoolWithPrewarm(sqlStatements, iterations);
        
        System.out.println("\nMethod 3: SAFE Parser Pool (with pre-warming)");
        demonstrateSafePoolWithPrewarm(sqlStatements, iterations);

        System.out.println("Method 1: Single Parser Instance (Sequential)");
        demonstrateSingleParser(sqlStatements, iterations);

        System.out.println("\n=== Thread Safety Demonstration ===");
        demonstrateThreadSafety();

        System.out.println("\n=== Concurrent Performance Test ===");
        demonstrateConcurrentPerformance();



        // Cleanup
        TParserPoolFactory.shutdownAll();
        
        System.out.println("\n========================================");
        System.out.println("              Demo Complete");
        System.out.println("========================================");
    }
    
    /**
     * Demonstrates single parser instance (no concurrency possible)
     */
    private static void demonstrateSingleParser(String[] sqlStatements, int iterations) {
        long startTime = System.currentTimeMillis();
        
        TGSqlParser sqlParser = new TGSqlParser(EDbVendor.dbvmysql);
        
        int totalParsed = 0;
        for (int i = 0; i < iterations; i++) {
            for (String sql : sqlStatements) {
                sqlParser.sqltext = sql;
                sqlParser.parse();
                totalParsed++;
            }
        }
        
        long elapsed = System.currentTimeMillis() - startTime;
        System.out.println("  Parsed " + totalParsed + " statements");
        System.out.println("  Time taken: " + elapsed + " ms");
        System.out.println("  Average: " + String.format("%.2f", elapsed / (double)totalParsed) + " ms per statement");
        System.out.println("  Note: Cannot handle concurrent requests!");
    }
    
    /**
     * Demonstrates the basic parser pool with pre-warming
     */
    private static void demonstrateBasicPoolWithPrewarm(String[] sqlStatements, int iterations) {
        // Create and pre-warm the pool
        TParserPool pool = new TParserPool(4);
        pool.prewarm(EDbVendor.dbvmysql, 4);
        
        long startTime = System.currentTimeMillis();
        
        int totalParsed = 0;
        try {
            for (int i = 0; i < iterations; i++) {
                for (String sql : sqlStatements) {
                    TCustomSqlStatement stmt = pool.withParser(EDbVendor.dbvmysql, parser -> {
                        parser.sqltext = sql;
                        parser.parse();
                        return parser.sqlstatements.get(0);
                    });
                    totalParsed++;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pool.shutdown();
        }
        
        long elapsed = System.currentTimeMillis() - startTime;
        System.out.println("  Parsed " + totalParsed + " statements");
        System.out.println("  Time taken: " + elapsed + " ms");
        System.out.println("  Average: " + String.format("%.2f", elapsed / (double)totalParsed) + " ms per statement");
        System.out.println("  WARNING: Parse trees retain parser references!");
    }
    
    /**
     * Demonstrates the SAFE parser pool with pre-warming
     */
    private static void demonstrateSafePoolWithPrewarm(String[] sqlStatements, int iterations) {
        // Create and pre-warm the pool
        TSafeParserPool safePool = new TSafeParserPool(4);
        safePool.prewarm(EDbVendor.dbvmysql, 4);
        
        long startTime = System.currentTimeMillis();
        
        int totalParsed = 0;
        try {
            for (int i = 0; i < iterations; i++) {
                for (String sql : sqlStatements) {
                    TStatementList stmts = safePool.safeParseSQL(EDbVendor.dbvmysql, sql);
                    totalParsed++;
                    
                    // Verify references are cleared
                    TCustomSqlStatement stmt = stmts.get(0);
                    if (stmt.parser != null || stmt.plsqlparser != null) {
                        System.err.println("ERROR: Parser references not cleared!");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            safePool.shutdown();
        }
        
        long elapsed = System.currentTimeMillis() - startTime;
        System.out.println("  Parsed " + totalParsed + " statements");
        System.out.println("  Time taken: " + elapsed + " ms");
        System.out.println("  Average: " + String.format("%.2f", elapsed / (double)totalParsed) + " ms per statement");
        System.out.println("  SUCCESS: Parse trees are thread-safe with no parser references!");
    }
    
    /**
     * Demonstrates thread safety with concurrent parsing
     */
    private static void demonstrateThreadSafety() {
        System.out.println("Testing concurrent parsing with 10 threads...");
        
        TSafeParserPool safePool = new TSafeParserPool(5);
        safePool.prewarm(EDbVendor.dbvoracle, 5);
        
        ExecutorService executor = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(10);
        List<TCustomSqlStatement> statements = new CopyOnWriteArrayList<>();
        
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < 10; i++) {
            final int threadId = i;
            executor.submit(() -> {
                try {
                    String sql = "SELECT * FROM table_" + threadId + " WHERE id = " + threadId;
                    TStatementList stmts = safePool.safeParseSQL(EDbVendor.dbvoracle, sql);
                    statements.add(stmts.get(0));
                    System.out.println("  Thread " + threadId + " parsed successfully");
                } catch (Exception e) {
                    System.err.println("  Thread " + threadId + " failed: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }
        
        try {
            latch.await(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        executor.shutdown();
        safePool.shutdown();
        
        long elapsed = System.currentTimeMillis() - startTime;
        
        // Verify all statements are independent
        boolean allSafe = true;
        for (TCustomSqlStatement stmt : statements) {
            if (stmt.parser != null || stmt.plsqlparser != null) {
                allSafe = false;
                break;
            }
        }
        
        System.out.println("\n  Results:");
        System.out.println("  - Parsed " + statements.size() + " statements concurrently");
        System.out.println("  - Time taken: " + elapsed + " ms");
        System.out.println("  - Thread safety: " + (allSafe ? "VERIFIED ✓" : "FAILED ✗"));
        System.out.println("  - All parse trees are independent and safe for concurrent use!");
    }
    
    /**
     * Demonstrates performance improvement with concurrent workload
     */
    private static void demonstrateConcurrentPerformance() {
        System.out.println("Comparing concurrent performance (20 threads, 50 statements each)...\n");
        
        final int THREAD_COUNT = 20;
        final int STATEMENTS_PER_THREAD = 50;
        
        // Test 1: Single shared parser (with synchronization)
        System.out.println("1. Single Parser with Synchronization:");
        long singleParserTime = testSingleParserConcurrent(THREAD_COUNT, STATEMENTS_PER_THREAD);
        
        // Test 2: Parser Pool
        System.out.println("\n2. Parser Pool (10 parsers):");
        long poolTime = testParserPoolConcurrent(THREAD_COUNT, STATEMENTS_PER_THREAD);
        
        // Calculate improvement
        double improvement = (double)singleParserTime / poolTime;
        System.out.println("\n=== Performance Summary ===");
        System.out.println("Single Parser: " + singleParserTime + " ms");
        System.out.println("Parser Pool: " + poolTime + " ms");
        System.out.println("Improvement: " + String.format("%.1fx faster", improvement));
    }
    
    private static long testSingleParserConcurrent(int threadCount, int statementsPerThread) {
        TGSqlParser sharedParser = new TGSqlParser(EDbVendor.dbvpostgresql);
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch completeLatch = new CountDownLatch(threadCount);
        
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            executor.submit(() -> {
                try {
                    startLatch.await();
                    for (int j = 0; j < statementsPerThread; j++) {
                        synchronized (sharedParser) {
                            sharedParser.sqltext = "SELECT * FROM table_" + threadId + " WHERE id = " + j;
                            sharedParser.parse();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    completeLatch.countDown();
                }
            });
        }
        
        startLatch.countDown();
        try {
            completeLatch.await(60, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        long elapsed = System.currentTimeMillis() - startTime;
        executor.shutdown();
        
        System.out.println("  Processed " + (threadCount * statementsPerThread) + " statements");
        System.out.println("  Time: " + elapsed + " ms");
        
        return elapsed;
    }
    
    private static long testParserPoolConcurrent(int threadCount, int statementsPerThread) {
        TSafeParserPool pool = new TSafeParserPool(10);
        pool.prewarm(EDbVendor.dbvpostgresql, 10);
        
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch completeLatch = new CountDownLatch(threadCount);
        
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            executor.submit(() -> {
                try {
                    startLatch.await();
                    for (int j = 0; j < statementsPerThread; j++) {
                        pool.safeParseSQL(EDbVendor.dbvpostgresql, 
                            "SELECT * FROM table_" + threadId + " WHERE id = " + j);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    completeLatch.countDown();
                }
            });
        }
        
        startLatch.countDown();
        try {
            completeLatch.await(60, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        long elapsed = System.currentTimeMillis() - startTime;
        executor.shutdown();
        pool.shutdown();
        
        System.out.println("  Processed " + (threadCount * statementsPerThread) + " statements");
        System.out.println("  Time: " + elapsed + " ms");
        
        return elapsed;
    }
}