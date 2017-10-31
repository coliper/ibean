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

package org.coliper.ibean.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * @author alex@coliper.org
 *
 */
public class RecursionCycleDetectorTest {

    public static class BadAlgorithm {
        public Boolean isMultipleOfSeven(final int no) {
            if (no == 7) {
                return Boolean.TRUE;
            }
            return this.isMultipleOfSeven(Math.abs(no - 7));
        }
    }

    public static class BadAlgorithmWithDetector extends BadAlgorithm {
        private final RecursionCycleDetector<Boolean> detector = new RecursionCycleDetector<Boolean>(
                Boolean.FALSE);

        @Override
        public Boolean isMultipleOfSeven(final int no) {
            return detector.executeWithCycleDetection(this, new Object[] { Integer.valueOf(no) },
                    () -> super.isMultipleOfSeven(Math.abs(no - 7)));
        }
    }

    @Test
    public void test() {
        BadAlgorithm bad = new BadAlgorithmWithDetector();
        assertTrue(bad.isMultipleOfSeven(14).booleanValue());
        assertFalse(bad.isMultipleOfSeven(3).booleanValue());
        assertTrue(bad.isMultipleOfSeven(70).booleanValue());
        assertFalse(bad.isMultipleOfSeven(17).booleanValue());
    }

    @Test(expected = StackOverflowError.class)
    public void testBoom() {
        new BadAlgorithm().isMultipleOfSeven(17);
    }
}
