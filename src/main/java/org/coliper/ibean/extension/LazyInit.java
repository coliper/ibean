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
 * Extension interface that supports with on-the-fly creation of nested IBeans.
 * Beans of this type act as a {@link LazyInitChild} as well as a
 * {@link LazyInitParent}. See {@link LazyInitChild} for a description and
 * comparison of the lazy loading extension interfaces.
 * 
 * @author alex@coliper.org
 */
public interface LazyInit extends LazyInitChild, LazyInitParent {

}
