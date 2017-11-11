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

import org.coliper.ibean.IBean;
import org.coliper.ibean.IBeanFactory;

/**
 * Enables cloning of beans via {@link Object#clone()}. To enable cloning the
 * bean type needs to extend {@link CloneableBean}. {@link #clone()} will always
 * return a bean of exactly the same type as the origin bean. Therefore the
 * generic type parameter must always be set to the declared bean type or at
 * least to one of its super types. The generic type here gives the advantage of
 * not having to cast the returned clone.
 * <p>
 * Example: <code>
 * public interface Customer extends CloneableBean&lt;Customer&gt; {
 *     ...
 * </code>
 * <p>
 * <em>Attention:</em>
 * <ul>
 * <li>A cloned bean has all field values copied from its origin but does
 * <em>not</em> have all extension interface states replicated as well. The
 * extension interface states are those of a newly created bean.<br>
 * For example if your bean type extends {@link Freezable} and you clone a
 * frozen bean the cloned bean will not be frozen as well.</li>
 * <li>Although <code>clone()</code> is a method from {@link Object} you always
 * need a dedicated handler for cloning in your {@link IBeanFactory}. This needs
 * to be considered when creating custom factories. If you use {@link IBean}
 * with its default configuration this can be neglected.</li>
 * </ul>
 * 
 * @author alex@coliper.org
 */
public interface CloneableBean<T> extends Cloneable {
    /**
     * Creates a copy of this bean, having the same field values but having an
     * initial state on any extension interface.
     * 
     * @return an IBean of the same type
     */
    T clone();
}
