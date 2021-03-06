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

package org.coliper.ibean.proxy.extension;

import static org.assertj.core.api.Assertions.assertThat;

import org.coliper.ibean.BeanStyle;
import org.coliper.ibean.IBeanFactory;
import org.coliper.ibean.extension.LazyInit;
import org.coliper.ibean.extension.LazyInitChild;
import org.coliper.ibean.extension.LazyInitParent;
import org.coliper.ibean.proxy.ProxyIBeanFactory;
import org.junit.Test;

/**
 * @author alex@coliper.org
 *
 */
public class LazyInitTest {

    public static interface Parent extends LazyInitParent {
        Child child();

        Parent child(Child c);
    }

    public static interface Child extends LazyInitChild {
        Child parent(Parent p);

        Parent parent();
    }

    public static interface Endless extends LazyInit {
        Endless other();

        Endless other(Endless o);
    }

    private final IBeanFactory factory;

    protected LazyInitTest(IBeanFactory factory) {
        this.factory = factory;
    }

    public LazyInitTest() {
        this(ProxyIBeanFactory.builder().withBeanStyle(BeanStyle.MODERN)
                .withDefaultInterfaceSupport().build());
    }

    @Test
    public void testCombined() {
        assertThat(this.factory.create(Endless.class).other().other().other()).isNotNull();
    }

    @Test
    public void testParentAndChild() {
        Parent parent = this.factory.create(Parent.class);
        Child child = parent.child();
        assertThat(child).isNotNull();
        assertThat(child.parent()).isNull();
    }
}
