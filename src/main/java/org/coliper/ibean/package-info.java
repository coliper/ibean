/**
 * Core package of the IBean framework containing class
 * {@link org.coliper.ibean.IBean}.
 * 
 * The IBean framework enables to specify Java beans as interfaces. Main purpose
 * is to reduce the amount of boilerplate code that is necessary for writing
 * Java beans including setters, getters and Object methods
 * ({@link java.lang.Object#toString()},
 * {@link java.lang.Object#equals(Object)},
 * {@link java.lang.Object#hashCode()}).
 * 
 * See following example how to write a bean with IBean. You just specify the
 * getter and setter methods in an interface class. <code>
 * public class Person {
 *     String getFirstName();
 *     void setFirstName(String n);
 *     
 *     String getLastName();
 *     void setLastName(String n);
 *     
 *     Date getDateOfBirth();
 *     void setDateOfBirth(Date d);
 * }
 * </code> To retrieve and instance of this bean you call
 * {@link org.coliper.ibean.IBean#newOf(Class)}: <code>
 * IBean.newOf(Person.class);
 * </code> The returned instance has expected behavior for the specified getters
 * and setters and also provides type specific implementation of
 * {@link java.lang.Object#toString()}, {@link java.lang.Object#equals(Object)}
 * and {@link java.lang.Object#hashCode()}. See
 * {@link org.coliper.ibean.IBean#newOf(Class)} for more details.
 * 
 * The IBean framework provides several features to customize and enhance the
 * behavior of beans. These features are:
 * <ul>
 * <li><b>Customizable output of toString()</b>: The output generated when
 * calling toString() on a bean is based on conventions introduced by Apache
 * Common's {@link org.apache.commons.lang3.builder.ToStringStyle}. See
 * {@link org.coliper.ibean.proxy.ProxyIBeanFactory.Builder#withToStringStyle(org.apache.commons.lang3.builder.ToStringStyle)}
 * how to change the output to a different style.</li>
 * <li><b>Customizable bean styles</b>: The default naming and signature
 * conventions for setters and getters in bean types follow the standard
 * <a href=
 * "http://www.oracle.com/technetwork/java/javase/documentation/spec-136004.html">Java
 * Bean Specification</a>. Now there are different other approaches how value
 * objects can be specified. See {@link org.coliper.ibean.BeanStyle} how to use
 * a more modern style or how to create a new custom style.</li>
 * <li><b>Extension interfaces</b>: The behavior of a bean created with IBean
 * can be enhanced by adding so called extension interfaces to bean type
 * definitions. A list of predefined extension interfaces can be found in
 * package {@link org.coliper.ibean.extension}. How to create a new extension
 * interface is described in
 * {@link org.coliper.ibean.proxy.ExtensionHandler}</li>
 * </ul>
 * 
 */
package org.coliper.ibean;