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

import java.lang.reflect.Method;

import org.coliper.ibean.beanstyle.ClassicBeanStyle;
import org.coliper.ibean.beanstyle.ClassicBeanStyleWithOptionalSupport;
import org.coliper.ibean.beanstyle.ModernBeanStyle;

/**
 * {@code BeanStyle} defines general rules about the signatures of getter and
 * setter methods of a bean.
 * <p>
 * Everyone knows one occurrence of a bean style, it is the style defined in the
 * <a href=
 * "http://www.oracle.com/technetwork/java/javase/documentation/spec-136004.html">
 * JavaBeans Spec</a>. That one roughly says:<br>
 * Assuming you have a bean property with name <em>xyz</em> and type <em>T</em>
 * the related getter and setter will be
 * <ul>
 * <li>(getter) <code>T getXyz()</code></li>
 * <li>(setter) <code>void setXyz(T t)</code></li>
 * </ul>
 * This is also the default bean style used in IBean and can be found in
 * predefined {@code BeanStyle} {@link #CLASSIC}.<br>
 * <em>ibean</em> also allows other styles where getters and setters have
 * different naming convention or other signatures. These styles are defined in
 * subclasses of {@code BeanStyle}.
 * <p>
 * You can either choose one of the predefined bean styles or implement your own
 * style. The built in styles can be found as constants in this class, for
 * example {@link #MODERN} or {@link #CLASSIC_WITH_OPTIONAL}.
 * <p>
 * To create a custom style you need to create a new subclass of
 * {@code BeanStyle} which has five abstract methods to overwrite. Optionally
 * you might also overwrite some of the other methods that have a default
 * implementation. To better understand how to implement a {@code BeanStyle}
 * this paragraph describes how a {@code BeanStyle} is used to examine an IBean
 * interface and how it is even used during lifecycle of the bean.<br>
 * {@link IBeanMetaInfoParser} is the class where a {@code BeanStyle} is used
 * most. It uses the {@code BeanStyle} to examine a new given interface
 * <ul>
 * <li>to determine if the provided interface is a valid IBean interface
 * and</li>
 * <li>to parse all fields with corresponding getters and setters from this
 * interface.</li>
 * </ul>
 * The {@link IBeanMetaInfoParser} does that in following steps:
 * <ul>
 * <li>It iterates over all method of the interface and checks if they are
 * potential setters or getters by calling {@link #isGetterMethod(Method)} and
 * {@link #isSetterMethod(Method)}.</li>
 * <li>From all found getters and setters it retrieves their matching field name
 * via calling {@link #convertGetterNameToFieldName(String)} and
 * {@link #convertSetterNameToFieldName(String)}.</li>
 * <li>Finally it combines all getters and setters that have the same field name
 * and determines the type of the field by calling
 * {@link #determineFieldTypeFromGetterAndSetter(Class, Method, Method)}.</li>
 * </ul>
 * The methods mentioned in the previous steps are the abstract methods that
 * define a {@code BeanStyle} and that are called once for each bean interface
 * to collect the meta data.<br>
 * Bean style also contains two methods that are not used for meta parsing but
 * that are called during life time of a bean, more specific, during execution
 * of getters and setters. See
 * {@link #convertReturnValueOfGetterCall(Class, Object)} and
 * {@link #createReturnValueForSetterCall(Object, Method, Object)} for more
 * details.
 * 
 * @author alex@coliper.org
 */
public abstract class BeanStyle {

    /**
     * Predefined bean style following the commonly known Java beans
     * specification.
     * 
     * @see ClassicBeanStyle
     */
    public static final BeanStyle CLASSIC = ClassicBeanStyle.INSTANCE;

    /**
     * Predefined bean style mostly following the commonly known Java beans
     * specification with the exception of an {@code Optional} support in
     * getters.
     * 
     * @see ClassicBeanStyleWithOptionalSupport
     */
    public static final BeanStyle MODERN = ModernBeanStyle.INSTANCE;

    /**
     * Predefined bean style with a different naming of the setters and getters
     * and with {@code Optional} support in getters.
     * 
     * @see ModernBeanStyle
     */
    public static final BeanStyle CLASSIC_WITH_OPTIONAL =
            ClassicBeanStyleWithOptionalSupport.INSTANCE;

    /**
     * Helper method checking if a given method has exactly one argument.
     * 
     * @param method
     *            the method to check
     * @return <code>true</code> if method has one param
     */
    protected static boolean isOneParameterInMethod(Method method) {
        requireNonNull(method, "method");
        return method.getParameterTypes().length == 1;
    }

    /**
     * Helper method checking if a given method has no arguments.
     * 
     * @param method
     *            the method to check
     * @return <code>true</code> if method does not have any params
     */
    protected static boolean isNoParameterInMethod(Method method) {
        requireNonNull(method, "method");
        return method.getParameterTypes().length == 0;
    }

    /**
     * Throws an {@link InvalidIBeanTypeException} if a given condition is not
     * met.
     * 
     * @param beanType
     *            the related IBean class that will be passed to the thrown
     *            Exception in case exception is thrown
     * @param condition
     *            if this parameter evaluates to <code>false</code> the
     *            exception will be thrown
     * @param message
     *            the message to be passed to the exception in case exception is
     *            thrown
     * @throws InvalidIBeanTypeException
     *             if the given condition is not met
     */
    protected static void assertForBeanType(Class<?> beanType, boolean condition, String message)
            throws InvalidIBeanTypeException {
        if (!condition) {
            throw new InvalidIBeanTypeException(beanType, message);
        }
    }

    /**
     * Determines if a given method is a potential getter method for this bean
     * type. Typically it checks if the method confirms to the required
     * signature and naming convention of the style. This method should not do
     * any checks about the type the method belongs to.
     * <p>
     * See {@link ClassicBeanStyle#isGetterMethod(Method)} for a concrete
     * example.
     * 
     * @param method
     *            the {@code Method} to test
     * @return <code>true</code> if the method matches the requirements for a
     *         getter
     */
    public abstract boolean isGetterMethod(Method method);

    /**
     * Determines if a given method is a potential setter method for this bean
     * type. Typically it checks if the method confirms to the required
     * signature and naming convention of the style. This method should not do
     * any checks about the type the method belongs to.
     * <p>
     * See {@link ClassicBeanStyle#isSetterMethod(Method)} for a concrete
     * example.
     * 
     * @param method
     *            the {@code Method} to test
     * @return <code>true</code> if the method matches the requirements for a
     *         setter
     */
    public abstract boolean isSetterMethod(Method method);

    /**
     * Derives the name of a bean field from the name of its corresponding
     * getter method. This method can assume that the given getter name has been
     * checked for compliance via {@link #isGetterMethod(Method)}.
     * 
     * @param getterName
     *            the name of a method that has been identified as a potential
     *            getter
     * @return the name of the bean field; names are case sensitive
     * @throws IllegalArgumentException
     *             if a conversion is not possible
     */
    public abstract String convertGetterNameToFieldName(String getterName)
            throws IllegalArgumentException;

    /**
     * Derives the name of a bean field from the name of its corresponding
     * setter method. This method can assume that the given setter name has been
     * checked for compliance via {@link #isSetterMethod(Method)}.
     * 
     * @param setterName
     *            the name of a method that has been identified as a potential
     *            setter
     * @return the name of the bean field; names are case sensitive
     * @throws IllegalArgumentException
     *             if a conversion is not possible
     */
    public abstract String convertSetterNameToFieldName(String setterName)
            throws IllegalArgumentException;

    /**
     * Determines the type of a bean field from given corresponding getter and
     * setter method. Implementations of this method can assume that both given
     * methods have been checked for getter and setter compliance and that both
     * methods match in terms of method names. Implementations need to determine
     * the type of the field and need to check if both getter and setter match
     * to the type. That means that for example not only the given setter should
     * be looked at to find out the field type.
     * 
     * @param beanType
     *            the examined bean class
     * @param getterMethod
     *            a method of the {@code beanType} that is proven to be a
     *            potential getter
     * @param setterMethod
     *            a method of the {@code beanType} that is proven to be a
     *            potential setter
     * @return the type of the bean field
     * @throws InvalidIBeanTypeException
     *             if either the type cannot be determined for any reason or if
     *             the concluded types of setter and getter do not match
     */
    public abstract Class<?> determineFieldTypeFromGetterAndSetter(Class<?> beanType,
            Method getterMethod, Method setterMethod) throws InvalidIBeanTypeException;

    /**
     * This method is called during runtime of a bean, more precisely, always
     * when setters of a bean are called to assemble the return value of the
     * setter method.<br>
     * This method is only called for bean styles that allow setter methods that
     * do not return {@code void}. Therefore this method only needs to be
     * implemented for those type of bean styles. This default implementation
     * throws {@code IllegalStateException}.
     * <p>
     * See {@link ModernBeanStyle} for a concrete implementation of this method.
     * 
     * @param instance
     *            the IBean instance
     * @param setterMethod
     *            the setter method in whose return value should be created
     * @param newValue
     *            the new value of the field, given as a parameter to the setter
     * @return the value that will then be returned from the setter
     */
    public Object createReturnValueForSetterCall(Object instance, Method setterMethod,
            Object newValue) {
        throw new IllegalStateException("createReturnValueForSetterCall() must not be called"
                + "for bean style " + this.getClass().getName());
    }

    /**
     * This method is called during runtime of a bean, more precisely, always
     * when getters of a bean are called to adjust the type of the return value
     * of the getter method.<br>
     * This method is only called for bean styles that allow getter methods that
     * do not match to the type of the field. Therefore this method only needs
     * to be implemented for those type of bean styles. This default
     * implementation throws {@code IllegalStateException}.
     * <p>
     * See {@link ModernBeanStyle} for a concrete implementation of this method.
     * 
     * @param expectedReturnType
     *            the type the field value needs to be converted to
     * @param returnValueWithWrongType
     *            the field value that needs to be converted
     * @return the value that will then be returned from the getter
     */
    public Object convertReturnValueOfGetterCall(Class<?> expectedReturnType,
            Object returnValueWithWrongType) {
        throw new IllegalStateException("unexpected call of convertReturnValueOfGetterCall()"
                + "for bean style " + this.getClass().getName());
    }

    /**
     * As stateless we treat all instances of one {@link BeanStyle} sub class as
     * equal.
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        return this.getClass() == obj.getClass();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return this.getClass().hashCode();
    }

}
