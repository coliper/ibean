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

package org.coliper.ibean.extension;

/**
 * Extension interface that allows setting the status of a bean to immutable ("frozen") by 
 * preventing all further setter calls.<br>
 * The generic type <code>T</code> of this interface should be set to the bean type. The generic
 * type is used in {@link #freeze()} to allow chaining {@link #freeze()} to modern bean style
 * setters.
 * <p>
 * The bean instance is set to immutable (aka frozen) by calling {@link #freeze()}. All subsequent
 * calls to any setter will cause a {@link BeanFrozenException} to be thrown from the setter. 
 * @author alex@coliper.org
 *
 */
public interface Freezable<T> {
    T freeze();

    boolean isFrozen();
}
