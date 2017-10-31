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

package org.coliper.ibean.proxy;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

/**
 * @author alex@coliper.org
 *
 */
class PrimitivesUtil {

    private static final Map<Class<?>,Object> DEFAULTS_MAP = new ImmutableMap.Builder<Class<?>,Object>()
            .put(byte.class, Byte.valueOf((byte)0))
            .put(short.class, Short.valueOf((short)0))
            .put(int.class, Integer.valueOf(0))
            .put(long.class, Long.valueOf(0L))
            .put(float.class, Float.valueOf((float)0.0))
            .put(double.class, Double.valueOf(0.0))
            .put(boolean.class, Boolean.FALSE)
            .put(char.class, Character.valueOf('\u0000'))
            .build();
    
    static Object defaultValue(Class<?> primitiveType) {
        requireNonNull(primitiveType, "primitiveType");
        Object ret = DEFAULTS_MAP.get(primitiveType);
        checkArgument(ret != null, "%s is not a primitive type", primitiveType);
        return ret;
    }
    
    /**
     * 
     */
    private PrimitivesUtil() {
    }

}
