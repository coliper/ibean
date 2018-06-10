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

package org.coliper.ibean.proxy;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.coliper.ibean.BeanStyle;
import org.coliper.ibean.IBeanFactory;
import org.coliper.ibean.extension.Completable;
import org.coliper.ibean.extension.GsonSupport;
import org.coliper.ibean.extension.Jackson2Support;
import org.coliper.ibean.extension.ModificationAwareExt;
import org.coliper.ibean.extension.TempFreezable;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author alex@coliper.org
 *
 */
public class ConcurrencyTest {

    public static interface BeanType extends Completable<BeanType>, TempFreezable<BeanType>,
            ModificationAwareExt, Jackson2Support, GsonSupport {
      //@formatter:off 
          void setBooleanPrimitive(boolean b);
          boolean isBooleanPrimitive();

          void setString(String s);
          String getString();
          
          void setDate(Date d);
          Optional<Date> getDate();

      //@formatter:on    
    }

    private static final int NO_OF_THREADS = 100;
    private static final int NO_OF_LOOPS = 100000;

    private final IBeanFactory factory;
    private final BeanType bean;
    private final ExecutorService executor = Executors.newFixedThreadPool(NO_OF_THREADS);

    public ConcurrencyTest() {
        this.factory = ProxyIBeanFactory.builder().withDefaultInterfaceSupport()
                .withBeanStyle(BeanStyle.CLASSIC_WITH_OPTIONAL).build();
        this.bean = this.factory.create(BeanType.class);
    }

    @After
    public void shutdown() throws InterruptedException {
        this.executor.shutdown();
        this.executor.awaitTermination(10, TimeUnit.SECONDS);
    }

    private void testMultithreaded(Callable<?> testMethod) {
        List<Future<?>> futures = new ArrayList<>();
        for (int i = 0; i < NO_OF_THREADS; i++) {
            futures.add(this.executor.submit(testMethod));
        }
        futures.forEach((f) -> {
            try {
                f.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                Assert.fail("unexpected: " + e);
            }
        });
    }

    @Test
    public void testFactory() throws Exception {
        testMultithreaded(this::hammerOnFactory);
    }

    public Object hammerOnFactory() {
        for (int i = 0; i < NO_OF_LOOPS; i++) {
            this.factory.create(BeanType.class);
        }
        return null;
    }

    @Test
    public void testBean() throws Exception {
        testMultithreaded(this::hammerOnBean);
    }

    public Object hammerOnBean() {
        for (int i = 0; i < NO_OF_LOOPS; i++) {
            this.bean.setBooleanPrimitive(false);
            this.bean.setDate(null);
            this.bean.setString("slfdj");
            this.bean.isBooleanPrimitive();
            this.bean.getString();
            this.bean.getDate();

            this.bean.isComplete();
            this.bean.assertComplete();
            this.bean.isFrozen();
            this.bean.unfreeze();
            this.bean.isModified();
            this.bean.resetModified();
            this.bean.getModifiedFieldNames();
            this.bean.allFieldsModified();
        }
        return null;
    }

}
