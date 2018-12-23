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
 * When IBean types reference each other you might want to chain getter calls to
 * have short code and to avoid unnecessary temporary variables. The problem
 * with chained getters is that you can easily run into null pointer scenarios.
 * And here come the lazy initialization interfaces into play. They simply
 * create the referenced IBean if it is {@code null}.<br>
 * 
 * <p>
 * See following example:
 * </p>
 * 
 * <pre>
 * <code>
 * interface Address extends LazyInitChild {
 *     String getCity();
 *     void setCity(String c);
 * }
 * 
 * interface Customer extends LazyInitParent {
 *     Address getAddress();
 *     void setAddress(Address a);
 * }
 * 
 * void changeCity(String city) {
 *     Customer customer = ...;
 *     // Now here comes the code with the potential null pointer that is prevented by the 
 *     // LazyInitXXX interfaces. If the customer does not yet own an Address object
 *     // a new Address instance is created on the fly.
 *     customer.getAddress().setCity(city);
 * } 
 * </code>
 * </pre>
 * 
 * To have precise control over instance creation the logic is split into two
 * interfaces:
 * <ul>
 * <li>{@link LazyInitChild} for the bean type that might be created on demand
 * and</li>
 * <li>{@link LazyInitParent} for the bean type that is referencing the child
 * and whose getter calls are intercepted and checked for potential null
 * returns.</li>
 * </ul>
 * Often you might not want to distinguish between parents and children. In that
 * case you can simply extend all your bean types with {@link LazyInit} which is
 * a combination of {@link LazyInitChild} and {@link LazyInitParent}.
 * 
 * <p>
 * {@link LazyInitChild} are always created with the same bean factory as their
 * {@link LazyInitParent}s. That of course means that their bean style needs to
 * match.
 * </p>
 * 
 * @author alex@coliper.org
 */
public interface LazyInitChild {

}
