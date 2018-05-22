package com.craigrueda.gateway.core.integration;

import lombok.extern.slf4j.Slf4j;
import org.junit.Ignore;
import org.junit.Test;

import static java.text.NumberFormat.getIntegerInstance;

/**
 * Created by Craig Rueda
 */
@Slf4j
public class MemTest {
    private static final int ITERATIONS_INT = 1_000_000_000;
    private static final double NANOSECONDS_TO_MILLISECONDS = 1.0D / 1_000_000.0D;

    private final FieldHolder fieldHolder = new FieldHolder();
    private final ThreadLocal<FieldHolder> holderThreadLocal = ThreadLocal.withInitial(() -> fieldHolder);
    private Runnable localIntInc = () -> {
        int localInt = 0;
        for (int i = 0; i < ITERATIONS_INT; i++) {
            localInt++;
        }
    };
    private Runnable localLongInc = () -> {
        long localLong = 0;
        for (int i = 0; i < ITERATIONS_INT; i++) {
            localLong++;
        }
    };
    private Runnable tlNonVolatile = () -> {
        for (int i = 0; i < ITERATIONS_INT; i++) {
            holderThreadLocal.get().nonVolatileInt++;
        }
    };
    private Runnable tlVolatile = () -> {
        for (int i = 0; i < ITERATIONS_INT; i++) {
            holderThreadLocal.get().volatileInt++;
        }
    };
    private Runnable directVol = () -> {
        for (int i = 0; i < ITERATIONS_INT; i++) {
            fieldHolder.volatileInt++;
        }
    };
    private Runnable directNonVol = () -> {
        for (int i = 0; i < ITERATIONS_INT; i++) {
            fieldHolder.nonVolatileInt++;
        }
    };

    @Test
    public void testAll() {
        doRun(localIntInc, "LocalIntAccess");
        doRun(localLongInc, "LocalLongAccess");
        doRun(directNonVol, "DirectNonVolatileAccess");
        doRun(tlNonVolatile, "ThreadLocalNonVolatile");
        doRun(directVol, "DirectVolatileAccess");
        doRun(tlVolatile, "ThreadLocalVolatile");
    }

    private void doRun(Runnable r, String type) {
        r.run();
        long startTime = System.nanoTime();
        r.run();
        startTime = System.nanoTime() - startTime;
        log.info("Completed {} iterations using {} in {}ms",
                getIntegerInstance().format(ITERATIONS_INT),
                type,
                startTime * NANOSECONDS_TO_MILLISECONDS);
    }

    class FieldHolder {
        private int nonVolatileInt = 0;
        private volatile int volatileInt = 0;
    }
}
