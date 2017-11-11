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
 * Extension interface that gives the possibility to check if a bean has been
 * modified since creation or last reset of the modification flag (via
 * {@link #resetModified()}).<br>
 * A bean is considered as modified as soon as a setter has been called,
 * regardless if the setter call actually changed the value of the field.<br>
 * If you need to know in detail which fields had been modified use sub
 * interface {@link ModificationAwareExt}.
 * <p>
 * This can for example used with persistence frameworks to decide if a bean has
 * been touched in prior code and needs to be stored.
 * 
 * @author alex@coliper.org
 */
public interface ModificationAware {
    /**
     * @return <code>true</code> if the bean is dirty, that is, if a setter has
     *         been called since creation or last {@link #resetModified()}
     */
    boolean isModified();

    /**
     * Sets a bean to not modified. If the bean was not dirty it does nothing.
     */
    void resetModified();
}
