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

import java.util.Optional;

import org.coliper.ibean.beanstyle.ModernBeanStyleWithOptionalSupport;

/**
 * Extension interface that allows runtime checking whether all fields of a bean
 * have been initialized, that is, are not null.<br>
 * The generic type <code>T</code> should always be set to the bean type. The type is used in 
 * {@link #assertComplete()} and is done to be able to put {@link #assertComplete()} at the end 
 * of an initialization chain. See example below.
 * <p> 
 * If used together with {@link OptionalSupport} some bean fields can be set to
 * not mandatory and will be left out of the not-null check.<br>
 * Here you find an example that also uses {@link OptionalSupport} and modern
 * bean style (see {@link ModernBeanStyleWithOptionalSupport}): 
 * <code>
 * public interface Person extends Completable&lt;Person&gt;, OptionalSupport {
 *     String firstName();
 *     Person firstName(String s);
 *     
 *     String lastName();
 *     Person lastName(String s);
 *     
 *     Optional&lt;LocalDate&gt; dateOfBirth();
 *     Person dateOfBirth(LocalDate d);
 * }
 * </code>
 * 
 * {@link #assertComplete()} should now always be called after the
 * initialization of a <code>Person</code> bean is completed.
 * <code>dateOfBirth</code> is set as optional so it will not be considered when
 * checking for completeness: 
 * <code>
 * // will throw no exception as firstName and lastName are set
 * Person person1 = IBean.of(Person.class).firstName("John").lastName("Doe").assertComplete();
 *
 * // will throw exception as firstName was not set
 * Person person2 = IBean.of(Person.class).lastName("Doe").assertComplete();
 *
 * // will throw exception as lastName is null
 * Person person3 = IBean.of(Person.class).firstName("John").lastName(null).assertComplete();
 *
 * // will throw no exception; setting dateOfBirth here is optional
 * Person person4 = IBean.of(Person.class)
 *      .firstName("John")
 *      .dateOfBirth(LocalDate.of(11, 11, 1977))
 *      .lastName("Doe").assertComplete();
 * </code>
 * <p>
 * <em>Attention:</em><code>Completable</code> checks all (mandatory) fields
 * from being not null. Contrary to {@link ModificationAware} it does not check
 * if any setter was called. And for declaring a field as non-mandatory it is
 * not enough to just return {@link Optional} from the getter, you also have to
 * declare the bean type as {@link OptionalSupport}.
 * 
 * @author alex@coliper.org
 */
public interface Completable<T> {
    /**
     * @return <code>true</code> if all (non-optional) bean fields are not <code>null</code>
     * @see Completable
     */
    boolean isComplete();

    /**
     * Checks if all (non-optional) bean fields are not <code>null</code>.
     * @return this bean if all fields are set and check was successful
     * @throws BeanIncompleteException if some of the (mandatory) fields are <code>null</code>
     * @see Completable
     */
    T assertComplete() throws BeanIncompleteException;
}
