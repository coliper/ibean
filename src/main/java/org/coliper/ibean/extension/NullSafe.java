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

import java.util.Optional;

import org.coliper.ibean.beanstyle.ClassicBeanStyleWithOptionalSupport;
import org.coliper.ibean.beanstyle.ModernBeanStyleWithOptionalSupport;

/**
 * Marker extension interface that injects getter calls in a way that if a
 * getter is called on a field with value <code>null</code> it throws an
 * {@link NullSafetyException}.
 * <p>
 * This of course gives no real protection of null-pointer cases. It just gives
 * a sort of an early detection during runtime. A more recommended way to
 * prevent null errors is to work with mandatory and optional fields, for
 * example by using Java's {@link Optional} in conjunction with IBean's
 * {@link Completable}, {@link ClassicBeanStyleWithOptionalSupport},
 * {@link ModernBeanStyleWithOptionalSupport}.
 * 
 * @author alex@coliper.org
 */
public interface NullSafe {

}
