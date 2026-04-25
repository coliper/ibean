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
 * Contains an {@link org.coliper.ibean.IBeanFactory} implementation that uses
 * Java proxy technology (see {@link java.lang.reflect.Proxy}). Beans are
 * created as interface proxies.<br>
 * Factory implementation is {@link org.coliper.ibean.proxy.ProxyIBeanFactory}.
 * See this class to check out its different configuration and customization
 * options.
 * <p>
 * {@link org.coliper.ibean.proxy.ExtensionSupport} is used to configure
 * handlers for custom extension interfaces.
 * <p>
 * Most other classes in this package are used internally by the framework.
 * 
 * @author alex@coliper.org
 */
package org.coliper.ibean.codegen;