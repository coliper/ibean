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

package org.coliper.ibean.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Utility class to help detecting endless loops in recusive methods. It works
 * by recording all instances and parameters a method is called on resp with.
 * This utility internally works with {@link ThreadLocal}s so it only works if
 * recursion takes place within the same thread. To use this utility create a
 * static instance of {@link RecursionCycleDetector} for the method you want to
 * protect against endless loops. The generic type of this class should match
 * the return type of the recursive method. The cycle detection is done via
 * embedding the recursive call (the call of the method on itself) in a
 * {@link #executeWithCycleDetection(Object, Object[], Supplier)} call via a
 * lambda expression. The following example shows a recursive method. <code>
 * Integer recursiveMethod(Integer i) {
 *     return recursiveMethod(i.intValue() / 2);
 * }    
 * </code> Now the same with cycle protection: <code>
 * private static final RecursionCycleDetector DETECTOR = 
 *     new RecursionCycleDetector&lt;Integer&gt;(Integer.valueOf(0));
 *     
 * Integer recursiveMethod(int i) {
 *     return DETECTOR.executeWithCycleDetection(this, new Object[] { i }, 
 *         () -&gt; recursiveMethod(i / 2));
 * }    
 * </code> See {@link #executeWithCycleDetection(Object, Object[], Supplier)}
 * for a more detailled description of its parameters.
 * <p>
 * Note that on cycle detection no exception is thrown but a predefined (in
 * constructor) value is returned.
 * 
 * @author alex@coliper.org
 */
public class RecursionCycleDetector<T> {

    private final ThreadLocal<List<Object>> instanceStack = ThreadLocal.withInitial(ArrayList::new);
    private final ThreadLocal<List<Object[]>> paramStack = ThreadLocal.withInitial(ArrayList::new);

    private final T returnValueIfCycleDetected;

    /**
     * Create a new {@link RecursionCycleDetector} used with in a method with
     * return type T. In most cases it makes sense to have one static instance
     * of {@link RecursionCycleDetector} for one method to protect.
     * 
     * @param returnValueIfCycleDetected
     *            the default value that is returned by
     *            {@link #executeWithCycleDetection(Object, Object[], Supplier)}
     *            in case a call cycle is detected
     */
    public RecursionCycleDetector(T returnValueIfCycleDetected) {
        this.returnValueIfCycleDetected = returnValueIfCycleDetected;
    }

    /**
     * Short for
     * <code>executeWithCycleDetection(instance, null, functionToExecute)</code>.
     * 
     * @param instance
     *            the object instance the referred method is executed on. As
     *            this method is normally called inside the protected method, in
     *            that cases 'this' would be passed here.
     * @param functionToExecute
     *            the lambda expression to call if no cycle is detected yet. In
     *            most cases this will be the recursive call of the protected
     *            method to itself.
     * @return if a cycle is detected the cycle detection value is returned that
     *         was set in the constructor.
     * @see #executeWithCycleDetection(Object, Object[], Supplier)
     */
    public T executeWithCycleDetection(Object instance, Supplier<T> functionToExecute) {
        return this.executeWithCycleDetection(instance, null/* params */, functionToExecute);
    }

    /**
     * Checks a method call for a recursion cycle and if no cycle is detected
     * executes a given lambda expression. The two first arguments are first
     * compared against the internal call stack and if no identical matches are
     * found in the stack the lambda expression in the third argument is
     * executed. Any RuntimeExceptions thrown in the lambda block will simply be
     * passed through.
     * 
     * @param instance
     *            the object instance the referred method is executed on. As
     *            this method is normally called inside the protected method, in
     *            that cases 'this' would be passed here.
     * @param params
     *            all parameters of the protected method as an object array. If
     *            some arguments have primitive types the values have to be
     *            converted to their object representations.
     * @param functionToExecute
     *            the lambda expression to call if no cycle is detected yet. In
     *            most cases this will be the recursive call of the protected
     *            method to itself.
     * @return if a cycle is detected the cycle detection value is returned that
     *         was set in the constructor.
     */
    public T executeWithCycleDetection(Object instance, Object[] params,
            Supplier<T> functionToExecute) {
        if (this.cycleDetected(instance, params)) {
            return this.returnValueIfCycleDetected;
        }
        instanceStack.get().add(instance); // push
        if (params != null && params.length > 0) {
            paramStack.get().add(params); // push
        }
        try {
            return functionToExecute.get();
        } finally {
            instanceStack.get().remove(instanceStack.get().size() - 1); // pop
            if (params != null && params.length > 0) {
                paramStack.get().remove(paramStack.get().size() - 1); // pop
            }
        }

    }

    /**
     * Searches in the internal stack for the exact match of given instance an
     * parameters.
     */
    private boolean cycleDetected(Object instance, Object[] params) {
        final List<Object> instList = this.instanceStack.get();
        for (int index = 0; index < instList.size(); index++) {
            if (instance == instList.get(index)) {
                if (params == null || params.length == 0) {
                    return true;
                }
                if (Objects.deepEquals(params, this.paramStack.get().get(index))) {
                    return true;
                }
            }
        }
        return false;
    }

}
