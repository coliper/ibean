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

/**
 * Contains all extension interfaces that are supported out of the box by IBean.
 * An extension interface adds cross-cutting functionality to bean interfaces.
 * Extension interfaces are enabled by adding them to the super-interface list
 * of the bean type. For example, to add extension interfaces
 * {@link org.coliper.ibean.extension.NullSafe} and
 * {@link org.coliper.ibean.extension.Freezable} to bean type Customer they just
 * need to be listed in the extended clause: <code>
 * public interface Customer extends NullSafe, Freezable {
 *     ...
 * </code>
 *
 * Developers can define their own extension interfaces. See
 * {@link org.coliper.ibean.proxy.ExtensionHandler} for a description how to do
 * this.<br>
 * This package contains all built-in extension interfaces of the IBean
 * framework.
 */
package org.coliper.ibean.extension;