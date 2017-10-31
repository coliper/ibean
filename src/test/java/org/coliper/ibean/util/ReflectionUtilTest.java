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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.io.Reader;
import java.io.StringReader;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.junit.Test;

/**
 * @author alex@coliper.org
 *
 */
public class ReflectionUtilTest {

    @Test
    public void testAreClassesRelated() {
        assertThat(ReflectionUtil.areClassesRelated(List.class, Map.class)).isFalse();
        assertThat(ReflectionUtil.areClassesRelated(Map.class, List.class)).isFalse();
        assertThat(ReflectionUtil.areClassesRelated(ArrayList.class, Collection.class)).isTrue();
        assertThat(ReflectionUtil.areClassesRelated(Collection.class, ArrayList.class)).isTrue();
        assertThat(ReflectionUtil.areClassesRelated(List.class, List.class)).isTrue();
        assertThat(ReflectionUtil.areClassesRelated(List.class, Object.class)).isTrue();
    }

    @Test
    public void testDoesMethodBelongToType() throws Exception {
        assertThat(ReflectionUtil.doesMethodBelongToType(Object.class.getMethod("hashCode"),
                List.class)).isTrue();
        assertThat(ReflectionUtil.doesMethodBelongToType(
                Map.class.getMethod("get", new Class<?>[] { Object.class }), List.class)).isFalse();
        assertThat(ReflectionUtil.doesMethodBelongToType(
                Map.class.getMethod("get", new Class<?>[] { Object.class }), HashSet.class))
                        .isFalse();
    }

    @Test
    public void testGetSuperTypesInclRoot() {
        assertThat(ReflectionUtil.getSuperTypesInclRoot(Object.class)).isEmpty();
        assertThat(ReflectionUtil.getSuperTypesInclRoot(Collection.class))
                .containsExactlyInAnyOrder(Collection.class, Iterable.class);
        assertThat(ReflectionUtil.getSuperTypesInclRoot(AbstractCollection.class))
                .containsExactlyInAnyOrder(Collection.class, Iterable.class,
                        AbstractCollection.class);
    }

    @Test
    public void testInvokeMethodUnchecked() {
        StringReader reader = new StringReader("");
        reader.close();
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(
                () -> ReflectionUtil.invokeMethodUnchecked(reader, Reader.class.getMethod("read")));
    }
}
