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
 * Marker extension interface that gives null safety to all getter methods that
 * return type {@link Optional}. If such a getter is called and the
 * corresponding field value is <code>null</code> then the getter will return an
 * empty <code>Optional</code> instead of <code>null</code>.
 * <p>
 * This interface is often used in conjunction with bean styles
 * {@link ModernBeanStyleWithOptionalSupport} and
 * {@link ClassicBeanStyleWithOptionalSupport}.
 * 
 * @author alex@coliper.org
 */
public interface OptionalSupport {

}
