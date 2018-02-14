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
 * Thrown from getter methods from beans that extend {@link NullSafe} in case
 * the value of the field is <code>null</code>.
 * 
 * @see NullSafe
 * @author alex@coliper.org
 */
public class NullSafetyException extends IllegalStateException {

    private static final long serialVersionUID = 1L;

    public NullSafetyException(String fieldName) {
        super("illegal read of field '" + fieldName + "': value is null");
    }

}
