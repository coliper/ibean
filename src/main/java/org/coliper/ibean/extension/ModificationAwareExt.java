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
 * Extends {@link ModificationAware} with functionality to determine which
 * fields have been modified.
 * 
 * @see ModificationAware
 * @author alex@coliper.org
 */
public interface ModificationAwareExt extends ModificationAware {

    /**
     * Gives the names of all bean fields that have been modified since creation
     * or respectively since the last call of {@link #resetModified()}.
     * 
     * @return a string array containing the names of the modified fields. If no
     *         fields have been modified an empty array will be returned. The
     *         names of the fields are deducible from the getter and setter
     *         names depending on the bean style.<br>
     *         The order of field names returned is random and not be reliable!
     */
    String[] getModifiedFieldNames();

    /**
     * Checks if every field has been set since creation or
     * {@link #resetModified()}. Can for example be used as a runtime check if
     * an initialization has been complete.
     * 
     * @return <code>true</code> if each setter had been called
     */
    boolean allFieldsModified();
}
