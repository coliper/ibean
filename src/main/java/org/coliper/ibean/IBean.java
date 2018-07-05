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

import static java.util.Objects.requireNonNull;

import org.coliper.ibean.beanstyle.ClassicBeanStyle;
import org.coliper.ibean.proxy.ProxyIBeanFactory;

/**
 * Used for creating new IBean instances.
 * <p>
 * Sample:<br>
 * 
 * <pre>
 * <code>
 *     SomeBeanType bean = IBean.newOf(SomeBeanType.class);
 * </code>
 * </pre>
 * 
 * <br>
 * {@code SomeBeanType} needs to be a valid IBean interface type. See also
 * {@link IBeanFactory} for a explanation what interfaces build up a valid IBean
 * type. <br>
 * See <a href="{@docRoot}/org/coliper/ibean/package-summary.html">here</a> for
 * a more general overview over <em>IBean</em> framework and some sample usages
 * of class {@code IBean}.
 * <p>
 * In many cases {@code IBean} is the main - and sometimes even only - class
 * that needs to be used from the <em>IBean</em> framework. Basically
 * {@code IBean} is just a convenience class that is supposed to be used as a
 * singleton and that holds a static instance of a {@link IBeanFactory} which
 * does the actual job of bean creation.
 * <p>
 * In most scenarios only one {@code IBeanFactory} is needed and this factory
 * can be made available to rest of the code using {@code IBean}. {@code IBean}
 * contains a preset default factory. If a customized factory should be used
 * instead the static instance can be changed via
 * {@link #setFactory(IBeanFactory)}. See also {@link ProxyIBeanFactory} for how
 * to create customized factories. <br>
 * The predefined {@link IBeanFactory} inside {@code IBean} has following
 * characteristics:
 * <ul>
 * <li>It is of type {@link ProxyIBeanFactory}.</li>
 * <li>It supports bean interfaces using the default Java bean style
 * ({@link ClassicBeanStyle})</li>
 * <li></li>
 * <li></li>
 * 
 * @author alex@coliper.org
 */
public class IBean {
    static {
        resetToDefaultFactory();
    }

    private static IBeanFactory factory;

    public static <T> T newOf(Class<T> beanType) {
        requireNonNull(beanType, "beanType");
        return factory.create(beanType);
    }

    public static void setFactory(IBeanFactory newFactory) {
        requireNonNull(newFactory, "newFactory");
        factory = newFactory;
    }

    public static void resetToDefaultFactory() {
        factory = ProxyIBeanFactory.builder().withDefaultInterfaceSupport().build();
    }
}
