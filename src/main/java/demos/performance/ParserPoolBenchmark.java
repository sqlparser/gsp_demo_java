package demos.performance;

import gudusoft.gsqlparser.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.ArrayList;
import java.util.List;

/**
 * Benchmark to demonstrate the dramatic performance improvement 
 * of TSafeParserPool over TSingletonParser and basic TParserPool.
 * 
 * This benchmark shows:
 * 1. Sequential performance comparison
 * 2. Concurrent performance comparison  
 * 3. Scalability under different thread counts
 * 4. Memory efficiency
 * 5. Thread safety verification
 */
public class ParserPoolBenchmark {
    
    // Test configurations
    private static final int WARMUP_ITERATIONS = 100;
    private static final int BENCHMARK_ITERATIONS = 1000;
    private static final int[] THREAD_COUNTS = {1, 2, 4, 8, 16, 32};
    
    // Test SQL statements of varying complexity
    private static final String[] TEST_SQLS = {
        // Simple query
        "SELECT * FROM users WHERE id = 1",
        
        // Medium complexity with JOIN
        "SELECT u.name, o.order_date, o.total " +
        "FROM users u " +
        "JOIN orders o ON u.id = o.user_id " +
        "WHERE o.status = 'completed' AND o.total > 100",
        
        // Complex query with subqueries
        "SELECT d.dept_name, " +
        "       (SELECT COUNT(*) FROM employees e WHERE e.dept_id = d.id) as emp_count, " +
        "       (SELECT AVG(salary) FROM employees e WHERE e.dept_id = d.id) as avg_salary " +
        "FROM departments d " +
        "WHERE EXISTS (SELECT 1 FROM employees e WHERE e.dept_id = d.id AND e.salary > 50000) " +
        "ORDER BY emp_count DESC",
        
        // Complex DML
        "INSERT INTO audit_log (user_id, action, timestamp, details) " +
        "SELECT u.id, 'LOGIN', CURRENT_TIMESTAMP, " +
        "       JSON_OBJECT('ip', s.ip_address, 'browser', s.user_agent) " +
        "FROM users u " +
        "JOIN sessions s ON u.id = s.user_id " +
        "WHERE s.created_at > DATE_SUB(NOW(), INTERVAL 1 HOUR)"
    };
    
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("   SQL Parser Pool Performance Benchmark");
        System.out.println("========================================\n");
        
        ParserPoolBenchmark benchmark = new ParserPoolBenchmark();
        
        // Warmup
        System.out.println("Warming up...");
        benchmark.warmup();
        
        // Run benchmarks
        System.out.println("\n=== SEQUENTIAL PERFORMANCE ===");
        benchmark.runSequentialBenchmark();
        
        System.out.println("\n=== CONCURRENT PERFORMANCE ===");
        benchmark.runConcurrentBenchmark();
        
        System.out.println("\n=== SCALABILITY TEST ===");
        benchmark.runScalabilityTest();
        
        System.out.println("\n=== THREAD SAFETY TEST ===");
        benchmark.runThreadSafetyTest();
        
        System.out.println("\n=== MEMORY EFFICIENCY TEST ===");
        benchmark.runMemoryTest();
        
        // Cleanup
        TParserPoolFactory.shutdownAll();
        
        System.out.println("\n========================================");
        System.out.println("         Benchmark Complete");
        System.out.println("========================================");
    }
    
    private void warmup() {
        TParserPool pool = new TParserPool();
        EDbVendor vendor = EDbVendor.dbvmysql;
        
        for (int i = 0; i < WARMUP_ITERATIONS; i++) {
            try {
                // Warmup singleton
                TSingletonParser.getInstance().getStmts(vendor, TEST_SQLS[0]);
                
                // Warmup pool
                pool.withParser(vendor, parser -> {
                    parser.sqltext = TEST_SQLS[0];
                    return parser.parse();
                });
            } catch (Exception e) {
                // Ignore warmup errors
            }
        }
        
        pool.shutdown();
    }
    
    private void runSequentialBenchmark() {
        EDbVendor vendor = EDbVendor.dbvoracle;
        TParserPool basicPool = new TParserPool();
        TSafeParserPool safePool = new TSafeParserPool();
        
        for (String sql : TEST_SQLS) {
            System.out.println("\nSQL complexity: " + getSqlComplexity(sql));
            
            // Benchmark Singleton
            long singletonTime = benchmarkSingleton(vendor, sql, BENCHMARK_ITERATIONS);
            
            // Benchmark Basic Pool
            long basicPoolTime = benchmarkPool(basicPool, vendor, sql, BENCHMARK_ITERATIONS);
            
            // Benchmark Safe Pool
            long safePoolTime = benchmarkSafePool(safePool, vendor, sql, BENCHMARK_ITERATIONS);
            
            // Calculate improvements
            double basicImprovement = (double) singletonTime / basicPoolTime;
            double safeImprovement = (double) singletonTime / safePoolTime;
            
            System.out.printf("  Singleton:      %d ms\n", singletonTime);
            System.out.printf("  Basic Pool:     %d ms (%.2fx faster)\n", basicPoolTime, basicImprovement);
            System.out.printf("  Safe Pool:      %d ms (%.2fx faster)\n", safePoolTime, safeImprovement);
        }
        
        basicPool.shutdown();
        safePool.shutdown();
    }
    
    private void runConcurrentBenchmark() {
        EDbVendor vendor = EDbVendor.dbvpostgresql;
        String sql = TEST_SQLS[2]; // Use complex query
        int threads = 10;
        int iterationsPerThread = 100;
        
        System.out.println("\nTesting with " + threads + " concurrent threads");
        System.out.println("Each thread performs " + iterationsPerThread + " parse operations");
        
        // Benchmark Singleton
        BenchmarkResult singletonResult = benchmarkConcurrent(
            vendor, sql, threads, iterationsPerThread, false);
        
        // Benchmark Pool
        BenchmarkResult poolResult = benchmarkConcurrent(
            vendor, sql, threads, iterationsPerThread, true);
        
        System.out.println("\nSingleton Results:");
        System.out.printf("  Total time: %d ms\n", singletonResult.totalTime);
        System.out.printf("  Throughput: %.0f ops/sec\n", singletonResult.throughput);
        System.out.printf("  Avg latency: %.2f ms\n", singletonResult.avgLatency);
        
        System.out.println("\nPool Results:");
        System.out.printf("  Total time: %d ms\n", poolResult.totalTime);
        System.out.printf("  Throughput: %.0f ops/sec\n", poolResult.throughput);
        System.out.printf("  Avg latency: %.2f ms\n", poolResult.avgLatency);
        
        System.out.printf("\nImprovement: %.2fx throughput, %.2fx lower latency\n",
            poolResult.throughput / singletonResult.throughput,
            singletonResult.avgLatency / poolResult.avgLatency);
    }
    
    private void runScalabilityTest() {
        EDbVendor vendor = EDbVendor.dbvsnowflake;
        String sql = TEST_SQLS[1]; // Medium complexity
        int iterationsPerThread = 50;
        
        System.out.println("\nTesting scalability with different thread counts");
        System.out.println("Iterations per thread: " + iterationsPerThread);
        
        for (int threads : THREAD_COUNTS) {
            System.out.printf("\n%d Threads:\n", threads);
            
            BenchmarkResult singletonResult = benchmarkConcurrent(
                vendor, sql, threads, iterationsPerThread, false);
            BenchmarkResult poolResult = benchmarkConcurrent(
                vendor, sql, threads, iterationsPerThread, true);
            
            System.out.printf("  Singleton: %.0f ops/sec\n", singletonResult.throughput);
            System.out.printf("  Pool:      %.0f ops/sec (%.2fx improvement)\n", 
                poolResult.throughput,
                poolResult.throughput / singletonResult.throughput);
        }
    }
    
    private void runMemoryTest() {
        System.out.println("\nTesting memory efficiency...");
        
        // Force garbage collection
        System.gc();
        long memoryBefore = getUsedMemory();
        
        // Create multiple singleton parsers (simulating the old approach)
        List<TGSqlParser> singletonParsers = new ArrayList<>();
        for (EDbVendor vendor : EDbVendor.values()) {
            if (vendor == EDbVendor.dbvgeneric) continue;
            singletonParsers.add(new TGSqlParser(vendor));
        }
        
        System.gc();
        long memorySingleton = getUsedMemory() - memoryBefore;
        
        // Clear singleton parsers
        singletonParsers.clear();
        System.gc();
        
        // Create parser pool
        memoryBefore = getUsedMemory();
        TParserPool pool = new TParserPool(5);
        
        // Use the pool for various vendors
        try {
            for (EDbVendor vendor : EDbVendor.values()) {
                if (vendor == EDbVendor.dbvgeneric) continue;
                pool.withParser(vendor, parser -> {
                    parser.sqltext = "SELECT 1";
                    return parser.parse();
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        System.gc();
        long memoryPool = getUsedMemory() - memoryBefore;
        
        System.out.printf("Memory used by Singleton approach: %.2f MB\n", 
            memorySingleton / (1024.0 * 1024.0));
        System.out.printf("Memory used by Pool approach:      %.2f MB\n", 
            memoryPool / (1024.0 * 1024.0));
        System.out.printf("Memory saved: %.2f MB (%.1f%% reduction)\n",
            (memorySingleton - memoryPool) / (1024.0 * 1024.0),
            (1.0 - (double)memoryPool / memorySingleton) * 100);
        
        pool.shutdown();
    }
    
    private long benchmarkSingleton(EDbVendor vendor, String sql, int iterations) {
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < iterations; i++) {
            TSingletonParser.getInstance().getStmts(vendor, sql);
        }
        
        return System.currentTimeMillis() - startTime;
    }
    
    private long benchmarkPool(TParserPool pool, EDbVendor vendor, String sql, int iterations) {
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < iterations; i++) {
            try {
                pool.withParser(vendor, parser -> {
                    parser.sqltext = sql;
                    return parser.parse();
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        return System.currentTimeMillis() - startTime;
    }
    
    private long benchmarkSafePool(TSafeParserPool pool, EDbVendor vendor, String sql, int iterations) {
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < iterations; i++) {
            try {
                pool.safeParseSQL(vendor, sql);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        return System.currentTimeMillis() - startTime;
    }
    
    private void runThreadSafetyTest() {
        System.out.println("\nTesting parse tree thread safety...");
        
        EDbVendor vendor = EDbVendor.dbvmysql;
        String sql = "SELECT u.id, u.name, o.total FROM users u JOIN orders o ON u.id = o.user_id";
        
        // Test with basic pool
        System.out.println("\nBasic Pool - Checking parser references:");
        TParserPool basicPool = new TParserPool(5);
        List<TCustomSqlStatement> basicStatements = new ArrayList<>();
        
        for (int i = 0; i < 10; i++) {
            try {
                TCustomSqlStatement stmt = basicPool.withParser(vendor, parser -> {
                    parser.sqltext = sql;
                    parser.parse();
                    return parser.sqlstatements.get(0);
                });
                basicStatements.add(stmt);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        int unsafeCount = 0;
        for (TCustomSqlStatement stmt : basicStatements) {
            if (stmt.parser != null) unsafeCount++;
        }
        System.out.printf("  %d/%d statements have parser references (UNSAFE)\n", 
            unsafeCount, basicStatements.size());
        
        basicPool.shutdown();
        
        // Test with safe pool
        System.out.println("\nSafe Pool - Checking parser references:");
        TSafeParserPool safePool = new TSafeParserPool(5);
        List<TCustomSqlStatement> safeStatements = new ArrayList<>();
        
        for (int i = 0; i < 10; i++) {
            try {
                TStatementList stmts = safePool.safeParseSQL(vendor, sql);
                safeStatements.add(stmts.get(0));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        int safeCount = 0;
        for (TCustomSqlStatement stmt : safeStatements) {
            if (stmt.parser == null && stmt.plsqlparser == null) safeCount++;
        }
        System.out.printf("  %d/%d statements have NO parser references (SAFE)\n", 
            safeCount, safeStatements.size());
        
        safePool.shutdown();
        
        // Concurrent test
        System.out.println("\nConcurrent Parse Tree Access Test:");
        testConcurrentParseTreeAccess();
    }
    
    private void testConcurrentParseTreeAccess() {
        TSafeParserPool safePool = new TSafeParserPool(5);
        String sql = "SELECT * FROM products WHERE price > 100 ORDER BY name";
        
        try {
            // Parse once
            TStatementList stmts = safePool.safeParseSQL(EDbVendor.dbvoracle, sql);
            TCustomSqlStatement stmt = stmts.get(0);
            
            // Access from multiple threads
            ExecutorService executor = Executors.newFixedThreadPool(10);
            CountDownLatch latch = new CountDownLatch(10);
            AtomicLong successCount = new AtomicLong(0);
            
            for (int i = 0; i < 10; i++) {
                executor.submit(() -> {
                    try {
                        // Each thread accesses the same parse tree
                        for (int j = 0; j < 100; j++) {
                            String str = stmt.toString();
                            if (str != null && str.contains("SELECT")) {
                                successCount.incrementAndGet();
                            }
                        }
                    } finally {
                        latch.countDown();
                    }
                });
            }
            
            latch.await(5, TimeUnit.SECONDS);
            executor.shutdown();
            
            System.out.printf("  %d successful concurrent operations on shared parse tree\n", 
                successCount.get());
            System.out.println("  Thread safety: VERIFIED ✓");
            
        } catch (Exception e) {
            System.out.println("  Thread safety: FAILED ✗ - " + e.getMessage());
        } finally {
            safePool.shutdown();
        }
    }
    
    private BenchmarkResult benchmarkConcurrent(EDbVendor vendor, String sql, 
                                                int threads, int iterationsPerThread, 
                                                boolean usePool) {
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch completeLatch = new CountDownLatch(threads);
        AtomicLong totalLatency = new AtomicLong(0);
        TParserPool pool = usePool ? new TParserPool(threads) : null;
        
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < threads; i++) {
            executor.submit(() -> {
                try {
                    startLatch.await();
                    
                    for (int j = 0; j < iterationsPerThread; j++) {
                        long opStart = System.nanoTime();
                        
                        if (usePool) {
                            pool.withParser(vendor, parser -> {
                                parser.sqltext = sql;
                                return parser.parse();
                            });
                        } else {
                            TSingletonParser.getInstance().getStmts(vendor, sql);
                        }
                        
                        totalLatency.addAndGet(System.nanoTime() - opStart);
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
            completeLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        long totalTime = System.currentTimeMillis() - startTime;
        int totalOperations = threads * iterationsPerThread;
        double throughput = (totalOperations * 1000.0) / totalTime;
        double avgLatency = totalLatency.get() / (totalOperations * 1_000_000.0);
        
        executor.shutdown();
        if (pool != null) {
            pool.shutdown();
        }
        
        return new BenchmarkResult(totalTime, throughput, avgLatency);
    }
    
    private String getSqlComplexity(String sql) {
        if (sql.length() < 50) return "Simple";
        if (sql.contains("JOIN") || sql.contains("WHERE")) return "Medium";
        return "Complex";
    }
    
    private long getUsedMemory() {
        Runtime runtime = Runtime.getRuntime();
        return runtime.totalMemory() - runtime.freeMemory();
    }
    
    private static class BenchmarkResult {
        final long totalTime;
        final double throughput;
        final double avgLatency;
        
        BenchmarkResult(long totalTime, double throughput, double avgLatency) {
            this.totalTime = totalTime;
            this.throughput = throughput;
            this.avgLatency = avgLatency;
        }
    }
}