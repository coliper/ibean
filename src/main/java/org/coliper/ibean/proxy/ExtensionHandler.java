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

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.coliper.ibean.IBeanFactory;
import org.coliper.ibean.IBeanFieldMetaInfo;
import org.coliper.ibean.IBeanTypeMetaInfo;
import org.coliper.ibean.extension.ModificationAware;
import org.coliper.ibean.extension.NullSafe;
import org.coliper.ibean.proxy.ProxyIBeanFactory.Builder;
import org.coliper.ibean.proxy.handler.NullSafeHandler;
import org.coliper.ibean.proxy.handler.StatefulExtensionHandler;
import org.coliper.ibean.proxy.handler.StatelessExtensionHandler;

/**
 * {@link ExtensionHandler}s contain the logic for handling calls to extension
 * interfaces of IBeans. They are used by {@link ProxyIBeanFactory} for internal
 * extension interfaces but also for any custom extension interface. See
 * {@link IBeanFactory} and {@org.coliper.ibean.extension} for general
 * descriptions about extension interfaces.
 * <p>
 * {@link ExtensionHandler} itself is just an interface and each extension
 * interface requires its individual implementation of {@link ExtensionHandler}.
 * For example built in extension interface {@link NullSafe} uses its matching
 * handler implementation {@link NullSafeHandler}.
 * <p>
 * If you are just using existing extension interfaces provided by the IBean
 * framework you will normally not get in touch with {@link ExtensionHandler}s.
 * They become relevant if you want to implement your own extension interface.
 * In this case you need to implement {@link ExtensionHandler}, bundle it with
 * the extension interface using {@link ExtensionSupport} and configure it in
 * the {@link ProxyIBeanFactory}
 * ({@link Builder#withInterfaceSupport(ExtensionSupport)}).
 * <p>
 * {@link ExtensionHandler} implementations need to be instanciated by the
 * {@link IBeanFactory}. Therefore they need to have a public constructor
 * without arguments. The implementation class itself also needs to be public.
 * <p>
 * To increase performance by avoiding unnecessary instance creation two types
 * of {@link ExtensionHandler}s were introduced, stateful and stateless
 * handlers. Stateful handlers are used for extension interfaces that enrich the
 * state of an IBean. Therefore each IBean instance holds its individual handler
 * instance. For stateless {@link ExtensionHandler}s only one instances is
 * created initially and that instance serves all interface calls to any
 * bean.<br>
 * For example extension interface {@link ModificationAware} requires a stateful
 * handler as the handler needs to store a dirty flag internally, whereas
 * {@link NullSafe} can run with a stateless handler.<br>
 * Whether stateful or stateless is determined in {@link ExtensionSupport} when
 * configuring a handler for a factory.
 * <p>
 * An IBean receives three types of method calls,
 * <ul>
 * <li>calls to setter methods,</li>
 * <li>calls to getter methods and</li>
 * <li>calls to extension interface methods</li>
 * </ul>
 * Now handlers need to provide a logic for calls to their corresponding
 * extension interface and can optionally also intercept getter and setter
 * calls. When intercepting getters and setters the handler can modify the
 * return respectively field values.<br>
 * As only the extension interface logic must be provided an the only mandatory
 * overwritten method is
 * {@link #handleExtendedInterfaceCall(IBeanContext, IBeanFieldAccess, Object, Method, Object[])}.
 * An implementation may override
 * {@link #interceptGetterCall(IBeanContext, IBeanFieldMetaInfo, Object, Object)}
 * or
 * {@link #interceptSetterCall(IBeanContext, IBeanFieldMetaInfo, Object, Object)}
 * for hooking into getter and setter calls.
 * <p>
 * Handlers for built-in extension interfaces (found in
 * {@org.coliper.ibean.proxy.handler}) do not implement {@link ExtensionHandler}
 * directly but extend either {@link StatefulExtensionHandler} or
 * {@link StatelessExtensionHandler}. This is recommended for custom handlers as
 * well.
 * 
 * @author alex@coliper.org
 *
 */
public interface ExtensionHandler {
    /**
     * Handles all calls to methods of its related extension interface. This is
     * the only method that needs to be overwritten when creating a new
     * {@link ExtensionHandler} implementations, all other methods are optional
     * and have default implementations.
     * <p>
     * When a method of an extension interface is called on an IBean the bean
     * looks up the matching {@link ExtensionHandler} for the extension and
     * calls this method on the handler. As the IBean implementation in this
     * package is based on Java proxies <code>handleExtendedInterfaceCall</code>
     * is very similar to
     * {@link InvocationHandler#invoke(Object, Method, Object[])} just extended
     * by some IBean scope information. The last three parameters
     * (<code>proxyInstance</code>, <code>method</code> and <code>params</code>)
     * are simply passed through from the {@link InvocationHandler} of the
     * IBean. Therefore when implementing this method you need to follow the
     * same rules that you need to consider when implementing an
     * {@link InvocationHandler}, for example in terms of thrown exceptions or
     * return types.
     * <p>
     * Bean values can be read or manipulated by this method. Used the passed
     * <code>bean</code> parameter for doing so.
     * <p>
     * The return value or exception coming out of this method will be the
     * return value or exception returned from the extension method call.
     * 
     * @param context
     *            provides meta information about the bean
     * @param bean
     *            allows this method to read or change field values of the IBean
     * @param proxyInstance
     *            the IBean instance object (see
     *            {@link InvocationHandler#invoke(Object, Method, Object[])} for
     *            more details)
     * @param method
     *            the {@link Method} of the extension interface that was called
     *            on the IBean (see
     *            {@link InvocationHandler#invoke(Object, Method, Object[])} for
     *            more details)
     * @param params
     *            the parameters passed to the extension method call (see
     *            {@link InvocationHandler#invoke(Object, Method, Object[])} for
     *            more details)
     * @return an object that matches to the return type of the extension
     *         method; return <code>null</code> if the return type is
     *         <code>void</code>. (see
     *         {@link InvocationHandler#invoke(Object, Method, Object[])} for
     *         more details)
     * @throws Throwable
     *             can be any {@link Error}, {@link RuntimeException} or checked
     *             exception that is definied in the signature of the extension
     *             method. (see
     *             {@link InvocationHandler#invoke(Object, Method, Object[])}
     *             for more details)
     */
    Object handleExtendedInterfaceCall(IBeanContext<?> context, IBeanFieldAccess bean,
            Object proxyInstance, Method method, Object[] params) throws Throwable;

    /**
     * Called as an optional hook with every getter call on an IBean. Getter
     * calls on IBeans are handled by the IBean implementation itself but an
     * {@link ExtensionHandler} can implement this method to hook into the
     * getter call. It can just peak on the return value or it can even change
     * it or throw an exception.
     * <p>
     * When you have several extension handlers registered for an IBean type the
     * intercept calls will be called in the order in which the handlers are
     * registered in the {@link ProxyIBeanFactory}. See also
     * {@link Builder#withInterfaceSupport(ExtensionSupport)}.
     * <p>
     * The default implementation just returns <code>returnValue</code>.
     * 
     * @param context
     *            provides meta information about the IBean
     * @param fieldMeta
     *            provides meta information about the field related to the
     *            getter
     * @param returnValue
     *            the current return value of the getter. This must not match to
     *            the field value of the IBean as the value might have already
     *            been changed by intercept calls from other handlers
     * @param proxyInstance
     *            the IBean instance itself
     * @return the value to be returned from the getter call. If you do not want
     *         to change the getter return value just pass the value provided in
     *         <code>returnValue</code>
     */
    default Object interceptGetterCall(IBeanContext<?> context, IBeanFieldMetaInfo fieldMeta,
            Object returnValue, Object proxyInstance) {
        return returnValue;
    }

    /**
     * Called as an optional hook with every setter call on an IBean. Setter
     * calls on IBeans are handled by the IBean implementation itself but an
     * {@link ExtensionHandler} can implement this method to hook into the
     * setter call. It can just peak on the new value or it can even change it
     * or throw an exception.
     * <p>
     * When you have several extension handlers registered for an IBean type the
     * intercept calls will be called in the order in which the handlers are
     * registered in the {@link ProxyIBeanFactory}. See also
     * {@link Builder#withInterfaceSupport(ExtensionSupport)}.
     * <p>
     * The default implementation just returns <code>newValue</code>.
     * 
     * @param context
     *            provides meta information about the IBean
     * @param fieldMeta
     *            provides meta information about the field related to the
     *            setter
     * @param newValue
     *            the current new value of the field. This must not match to the
     *            initial value given in the setter call of the IBean as the
     *            value might have already been changed by intercept calls from
     *            other handlers
     * @param proxyInstance
     *            the IBean instance itself
     * @return the value the field is supposed to be set to. If you do not want
     *         to change the value just pass the value provided in
     *         <code>newValue</code>
     */
    default Object interceptSetterCall(IBeanContext<?> context, IBeanFieldMetaInfo fieldMeta,
            Object newValue, Object proxyInstance) {
        return newValue;
    }

    /**
     * Initialization method for stateful handlers that is called after
     * initialization of the IBean. If a handler is registered as stateful a new
     * instance of the {@link ExtensionHandler} implementation is created for
     * each new IBean. After the IBean itself and all handlers created this
     * method is called on all stateful handlers. This method should therefore
     * be overwritten if a handler needs some initialization after creation.
     * <p>
     * The default implementation does nothing.
     * 
     * @param proxyInstance
     *            the IBean instance
     * @param metaInfo
     *            meta information about the IBean
     */
    default void onInitStateful(Object proxyInstance, IBeanTypeMetaInfo<?> metaInfo) {
    }
}
