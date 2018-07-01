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

package org.coliper.ibean;

import org.coliper.ibean.proxy.ProxyIBeanFactory;

/**
 * Creates <em>IBean</em> instances for given bean interfaces. <br>
 * For example:<br>
 * 
 * <pre>
 * <code>
 *     IBeanFactory factory = ...;
 *     SomeBeanType bean = factory.create(SomeBeanType.class);
 * </code>
 * </pre>
 * 
 * {@code IBeanFactory}s are the heart and the core of the <em>IBean</em>
 * framework. They are responsible for creating instances for provided IBean
 * compliant interfaces with their desired behavior. IBean interfaces consist of
 * setter and getter methods and super-interfaces.<br>
 * Following code shows an example of a bean interface:<br>
 * 
 * <pre>
 * <code>
 *     public interface Person {
 *         
 *         void setFirstname(String s);
 *         String getFirstname();
 *         
 *         void setDateOfBirth(Date d);
 *         Date getDateOfBirth();
 *         
 *     }
 * </code>
 * </pre>
 * 
 * <br>
 * See <a href="{@docRoot}/org/coliper/ibean/package-summary.html"> IBean
 * tutorial</a> for details about <em>IBean</em> interfaces.
 * <p>
 * At the moment there is only one {@code IBeanFactory} implementation which is
 * {@link ProxyIBeanFactory}.
 * 
 * @author alex@coliper.org
 */
public interface IBeanFactory {

    /**
     * Creates a new instance of a given bean type.
     * 
     * @param beanType
     *            a <em>IBean</em> that matches general <em>IBean</em> rules and
     *            that complies to the specific requirements for this factory
     *            like bean styles or supported extension interfaces
     * @param <T>
     *            generic type {@code T} is the bean class provided with
     *            parameter {@code beanType}
     * @return an instance of the bean type, never <code>null</code>. The
     *         returned beans will usually have default values set for all field
     *         values, like <code>null</code> for objects or zero for number
     *         primitives.
     * @throws InvalidIBeanTypeException
     *             if the given {@code beanType} is not a valid <em>IBean</em>
     *             interface for this factory
     */
    <T> T create(Class<T> beanType) throws InvalidIBeanTypeException;

}
