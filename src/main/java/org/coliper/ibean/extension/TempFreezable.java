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
 * Extension of the {@link Freezable} interface that gives the possibility to
 * change a bean from an immutable (frozen) state back to a mutable state.<br>
 * Could for example used to freeze beans only for usage in certain portions of
 * the code where they are not supposed to be modified.
 * 
 * @author alex@coliper.org
 */
public interface TempFreezable<T> extends Freezable<T> {
    T unfreeze();
}
