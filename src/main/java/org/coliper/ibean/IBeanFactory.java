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

//@formatter:off 
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
 * An {@code IBeanFactory} of course cannot create a bean instance from every kind of interface.
 * An interface that can be handled by a factory needs to follow these criteria:<ul>
 * <li>Every method declared in the interface must either be<ul>
 *         <li>a getter or setter method,</li>
 *         <li>a default method (interface method with a given default implementation) or</li>
 *         <li>a method belonging to an extension interface.</li>
 *     </ul>
 * </li>
 * <li>Always a tuple of a setter and a getter method virtually defines an interface field.
 *     (<em>firstname</em> and <em>dateOfBirth</em> would be the fields of the example above.)
 *     That means for each setter a getter must exist as well and vice versa.</li>
 * <li>An {@code IBeanFactory} always looks on the complete list of methods including those
 *     from super interfaces. The factory does not care in which hierarchy level a method is 
 *     declared. That means for example that you can declare getter and setter methods in 
 *     different parent or sub interfaces as long as they are complete in the final interface
 *     that is provided to the {@link #create(Class)} method.</li>
 * <li>Default methods are ignored by the {@code IBeanFactory}. You must therefore not have 
 *     default implementations for getter or setter methods.</li>
 * <li>Whether an interface method is a getter or setter method and whether a method pair builds 
 *     up a valid setter-getter combination is defined by the so called
 *     bean style. All built in bean styles for example require a consistent type to be used 
 *     in both methods to unambiguously define the field type. If a setter for example has
 *     a primitive {@code boolean} parameter, the getter should return the same type, not even
 *     {@code Boolean} would be accepted instead. See {@link BeanStyle} for more information.</li>
 * <li>A method can also belong to a so called extension interface. Extension interfaces
 *     are a group of interfaces that are well known by the {@code IBeanFactory}. 
 *     These interfaces are declared as super interfaces of the bean interface and 
 *     add certain functionality to the beans. The {@code IBeanFactory} simply ignores all
 *     methods from these extension super interfaces. 
 *     See <a href="{@docRoot}/org/coliper/ibean/extension/package-summary.html"><code>org.coliper.ibean.extension</code> package</a> for more
 *     details about extension interfaces.</li>
 * </ul> 
 * If you try to create a bean instance from an interface that does not follow these set
 * of conditions a {@link InvalidIBeanTypeException} will be thrown.
 * <p>
 * See <a href="{@docRoot}/org/coliper/ibean/package-summary.html"> IBean
 * tutorial</a> for a more holistic view on <em>IBean</em>.
 * <p>
 * At the moment there is only one {@code IBeanFactory} implementation which is
 * {@link ProxyIBeanFactory}.
 * 
 * @author alex@coliper.org
 */
//@formatter:on 
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
