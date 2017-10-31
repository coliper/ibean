/*
 * Copyright (C) 2017 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package org.coliper.ibean;

import java.util.Date;

/**
 * @author alex@coliper.org
 *
 */
public class IBeanPerformanceTest {

    public static final long IBEAN_ITERATIONS = 1000_000;
    public static final long CLASSIC_ITERATIONS = 10_000_000_000L;

    /**
     * @param args
     */
    public static void main(String[] args) {
        testClassic();
        System.out.println("---");
        testIBean();
    }

    private static void testClassic() {
        long start, duration;
        double durationPerCall;
        PrimitivesBeanClassic o = null;

        // warmup the garbage collector
        for (long i = 0; i < 1000_000_000; i++) {
            new Date(i);
        }

        start = System.currentTimeMillis();
        for (long i = 0; i < CLASSIC_ITERATIONS; i++) {
            o = new PrimitivesBeanClassicImpl();
        }
        duration = System.currentTimeMillis() - start;
        durationPerCall = 1000_000.0 * duration / (double) CLASSIC_ITERATIONS;
        System.out.println("Creation of classic bean takes " + durationPerCall + " ns");

        start = System.currentTimeMillis();
        for (long i = 0; i < CLASSIC_ITERATIONS; i++) {
            o.setInt(17);
        }
        duration = System.currentTimeMillis() - start;
        durationPerCall = 1000_000.0 * duration / (double) CLASSIC_ITERATIONS;
        System.out.println(
                "Setter call of primitive to classic bean takes " + durationPerCall + " ns");

        start = System.currentTimeMillis();
        for (long i = 0; i < CLASSIC_ITERATIONS; i++) {
            o.setBooleanObject(Boolean.TRUE);
        }
        duration = System.currentTimeMillis() - start;
        durationPerCall = 1000_000.0 * duration / (double) CLASSIC_ITERATIONS;
        System.out
                .println("Setter call of object to classic bean takes " + durationPerCall + " ns");

    }

    private static void testIBean() {
        long start, duration;
        double durationPerCall;
        PrimitivesBeanClassic o = null;

        // warmup the garbage collector
        for (long i = 0; i < 1000_000_000; i++) {
            new Date(i);
        }

        start = System.currentTimeMillis();
        for (long i = 0; i < IBEAN_ITERATIONS; i++) {
            o = IBean.newOf(PrimitivesBeanClassic.class);
        }
        duration = System.currentTimeMillis() - start;
        durationPerCall = 1000_000.0 * duration / (double) IBEAN_ITERATIONS;
        System.out.println("Creation of IBean takes " + durationPerCall + " ns");

        start = System.currentTimeMillis();
        for (long i = 0; i < IBEAN_ITERATIONS; i++) {
            o.setInt(17);
        }
        duration = System.currentTimeMillis() - start;
        durationPerCall = 1000_000.0 * duration / (double) IBEAN_ITERATIONS;
        System.out.println(
                "Setter call of primitive (cached) to IBean takes " + durationPerCall + " ns");

        start = System.currentTimeMillis();
        for (long i = 0; i < IBEAN_ITERATIONS; i++) {
            o.setLong(i);
        }
        duration = System.currentTimeMillis() - start;
        durationPerCall = 1000_000.0 * duration / (double) IBEAN_ITERATIONS;
        System.out.println(
                "Setter call of primitive (uncached) to IBean takes " + durationPerCall + " ns");

        start = System.currentTimeMillis();
        for (long i = 0; i < IBEAN_ITERATIONS; i++) {
            o.setBooleanObject(Boolean.TRUE);
        }
        duration = System.currentTimeMillis() - start;
        durationPerCall = 1000_000.0 * duration / (double) IBEAN_ITERATIONS;
        System.out.println("Setter call of object to IBean takes " + durationPerCall + " ns");

    }

}
