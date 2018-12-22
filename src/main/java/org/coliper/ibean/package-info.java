//@formatter:off 
/**
 * Core package of the IBean framework containing the core framework classes 
 * {@link org.coliper.ibean.IBean} and {@link org.coliper.ibean.IBeanFactory}.
 * 
 * <h1><a id="development_guide">Development Guide</a></h1>
 *
 * <h2 id="intro">Introduction</h2>
 * 
 * The IBean framework enables to specify Java beans as interfaces. Main purpose
 * is to reduce the amount of boilerplate code that is necessary for writing
 * Java beans including setters, getters and Object methods
 * ({@link java.lang.Object#toString()},
 * {@link java.lang.Object#equals(Object)},
 * {@link java.lang.Object#hashCode()}).
 * 
 * See following example how to declare a bean with <em>IBean</em>. You just specify the
 * getter and setter methods in an interface class. 
 * <pre><code>
 * public interface Person {
 *     String getFirstName();
 *     void setFirstName(String n);
 *     
 *     String getLastName();
 *     void setLastName(String n);
 *     
 *     Date getDateOfBirth();
 *     void setDateOfBirth(Date d);
 * }
 * </code></pre> 
 * 
 * <p>To retrieve and instance of this bean you call
 * {@link org.coliper.ibean.IBean#newOf(Class)}: 
 * <br>
 * <pre><code>
 *     Person personInstance = IBean.newOf(Person.class);
 * </code></pre>
 * 
 * <p>The returned instance has expected behavior for the specified getters
 * and setters and also provides type specific implementation of
 * {@link java.lang.Object#toString()}, {@link java.lang.Object#equals(Object)}
 * and {@link java.lang.Object#hashCode()}.</p>
 * 
 * 
 * <h2 id="bean-interfaces">Bean Interfaces</h2>
 * 
 * Any interface that only consists of getter and setter methods can be used as an IBean. No
 * configuration or annotation is necessary. Only prerequisite is that you have a getter for each
 * setter and vice versa. Details about IBean compatible interfaces can be found in the 
 * documentation of type {@link org.coliper.ibean.IBeanFactory}.
 * 
 * <p>Getters and setters may be defined in different levels of an interface hierarchy. The type
 * that is provided to the factory needs to contain complete getter and setter tuples.<br>
 * For example you might want to declare all setters in a "builder" subclass to force immutability. 
 * Like this:</p>
 * 
 * <pre><code>
 * public interface Person {
 *     String getFirstName();
 *     String getLastName();
 *     Date getDateOfBirth();
 *     
 *     public static interface Builder extends Person {
 *         void setFirstName(String n);
 *         void setLastName(String n);
 *         void setDateOfBirth(Date d);
 *     }
 * }
 * </code></pre>
 * 
 * <p>You can then create the IBean via its builder:</p> 
 * <pre><code>
 *     Person personInstance = IBean.newOf(Person.Builder.class);
 * </code></pre> 
 * <p>Following code will not work:
 * <pre><code>
 *     // will throw an exception as provided class Person contains no setters
 *     Person personInstance = IBean.newOf(Person.class);
 * </code></pre>
 * 
 * <p>You might wonder how the setters will be called in the example above. Builders in subclasses
 * make most sense if you use a different {@link org.coliper.ibean.BeanStyle} that allows chained
 * setters. See the chapter for for <a href="#bean-styles">bean styles</a> below. </p>
 * 
 * 
 * <h2 id="bean-factories">Bean Factories</h2>
 * 
 * A bean factory creates IBean instances from provided interfaces. As seen in the examples before
 * you can use the default factory that is set as static field in class 
 * {@link org.coliper.ibean.IBean}.
 * <pre><code>
 *     Person personInstance = IBean.newOf(Person.class);
 * </code></pre>
 * 
 * <p>The <em>IBean</em> framework currently contains one implementation of 
 * {@link org.coliper.ibean.IBeanFactory} which is based
 * on Java proxies: {@link org.coliper.ibean.proxy.ProxyIBeanFactory}. Future versions of 
 * <em>IBean</em> will contain more factory types.<br>
 * If you need different settings than provided by the default factory inside 
 * {@link org.coliper.ibean.IBean} you can create your own
 * factory instance:</p>
 * 
 * <pre><code>
 *     IBeanFactory factory = ProxyIBeanFactory.builder().withBeanStyle(BeanStyle.MODERN).build();
 *     Person personInstance = factory.create(Person.class);
 * </code></pre>
 * 
 * <p>In the previous example the factory is created with a different bean style. More about bean
 * styles in the related chapter below <a href="#bean-style">below</a><br>
 * If you create your own factory you can put it into {@link org.coliper.ibean.IBean} for global
 * usage:</p>
 * 
 * <pre><code>
 *     IBeanFactory factory = ProxyIBeanFactory.builder().withBeanStyle(BeanStyle.MODERN).build();
 *     IBean.setFactory(factory);
 *     ...
 *     Person personInstance = IBean.newOf(Person.class);
 * </code></pre>
 * 
 * <p>For general information about bean factories see {@link org.coliper.ibean.IBeanFactory}.
 * Documentation how to customize a factory can be found in 
 * {@link org.coliper.ibean.proxy.ProxyIBeanFactory}.</p>
 * 
 * 
 * <h2 id="object-methods">Object Methods</h2>
 * 
 * IBeans provide implementations of {@link java.lang.Object#equals(Object)}, 
 * {@link java.lang.Object#hashCode()} and {@link java.lang.Object#toString()}. 
 * Details about the implementations and how 
 * to customize them can be found in this chapter.
 * 
 * <h3>{@code toString()}</h3>
 * 
 * IBeans internally use {@link org.apache.commons.lang3.builder.ToStringBuilder} to implement
 * {@code toString()} methods. The default {@link org.apache.commons.lang3.builder.ToStringStyle}
 * is {@link org.apache.commons.lang3.builder.ToStringStyle#SHORT_PREFIX_STYLE}. See this style
 * to get an impression how a {@code toString()} output of an IBean looks like.
 * <p>You can adopt the behavior of {@code toString()} by customizing the factory. This is described
 * at {@link org.coliper.ibean.proxy.ProxyIBeanFactory.Builder#withToStringStyle(org.apache.commons.lang3.builder.ToStringStyle)}.</p>
 * 
 * <h3>{@code equals()</code> and <code>hashCode()}</h3>
 * 
 * IBeans also provide implementations for {@code equals()} and {@code hashCode()}.<br>
 * {@code hashCode()} will return a hash that is calculated from the hash codes of all
 * field values.<br>
 * {@code equals()} will return {@code true} when comparing two IBeans when<ul>
 * <li>both beans are IBeans, that is, were created from the same {@code IBeanFactory},
 * <li>both beans were created for the same bean interface and
 * <li>all field values of both beans are equal (equal comparison and not identity check).
 * </ul>
 * <p>{@code equals()} and {@code hashCode()} can both be customized. This is done by
 * providing default interface methods named {@code _equals} respectively 
 * {@code _hashCode}
 * with exactly the same signatures as the default Object methods. You can provide both methods
 * or only just one but as {@code equals()} and {@code hashCode()} are related you 
 * have to take care that equal beans return the same hash code.<br>
 * You can either provide customizations for individual instances or you can also create a super
 * interface with {@code _equals} and 
 * {@code _hashCode}.</p>
 * 
 * <p>Following example shows a use case. You might want that beans equal if they have the same
 * identifier regardless of their field values. So you create following base interface:</p>
 * 
 * <pre><code>
 * public interface BeanWithIdentifier {
 *     Long getId();
 *     void setId(Long id);
 *     
 *     default boolean _equals(Object other) {
 *         if (other == null) return false;
 *         if (!this.getClass().equals(other.getClass())) return false;
 *         return Objects.equals(this.getId(), ((BeanWithIdentifier)other).getId());
 *     }
 *     
 *     default int _hashCode() {
 *         if (this.getId() ==  null) return -1;
 *        return this.getId().hashCode();
 *     }
 * }
 * </code></pre>
 * 
 * 
 * <h2 id="extension-interfaces">Extension Interfaces</h2>
 * 
 * Extension interfaces are a very powerful feature of the <em>IBean</em> framework. 
 * The behavior of an IBean can be enhanced by adding so called extension interfaces to bean type
 * definitions. These extension interfaces <ul>
 * <li>influence the way how getters or setters work and/or
 * <li>add new methods to the beans.
 * </ul>
 * The nature of these extension interfaces is that they are of general purpose so they can be 
 * used with a wide range of bean types regardless of the semantics of these types. So you can
 * see the extension interfaces as a sort of aspect oriented extension but using interface extension
 * rather than annotations.
 * 
 * <p>For example following bean interface uses two extension interfaces (
 * {@link org.coliper.ibean.extension.NullSafe} and {@link org.coliper.ibean.extension.Freezable}): </p>
 * 
 * <pre><code>
 * public interface Person extends NullSafe, Freezable {
 *    ...
 * }
 * </code></pre>
 * 
 * <p>{@link org.coliper.ibean.extension.NullSafe} does not add any own methods but influences the
 * way how getter methods work. {@link org.coliper.ibean.extension.Freezable} adopts the behavior
 * of setter methods and also adds new methods to the bean. See the javadoc for both interfaces
 * for more details.</p>
 * 
 * <p>The <em>IBean</em> framework already contains a set of extension interfaces but it is possible
 * to create your own extensions.
 * A list of predefined extension interfaces can be found in
 * package {@link org.coliper.ibean.extension}. How to create a new extension
 * interface is described in
 * {@link org.coliper.ibean.proxy.ExtensionHandler} </p>
 * 
 * <h3>Default Bean Interface</h3>
 *
 * Often it makes sense that all bean interfaces use the same set of extension interfaces. If 
 * this is the case it is recommended to create a base interface that extends all extension
 * interfaces that are commonly used. The concrete interfaces then have that base interface as 
 * their common super interface.
 *
 * 
 * <h2 id="bean-styles">Bean Styles</h2>
 * 
 * Bean styles are a feature of the <em>IBean</em> framework to allow getters and setters to have 
 * different names and signatures as known from the <a href=
 * "http://www.oracle.com/technetwork/java/javase/documentation/spec-136004.html">Java
 * Bean Specification</a>. In the JavaBean
 * specification setters are always prefixed with "set", have no parameter and return 
 * {@code void}. Similar rules exist for getters.

 * <p><em>IBean</em>s optionally now allow also a different style.    
 * For example the bean that was used in the examples above can also be defined like this:</p>
 * 
 * <pre><code>
 * public interface Person {
 *     String firstName();
 *     String lastName();
 *     Date dateOfBirth();
 *     
 *     public static interface Builder extends Person {
 *         Builder firstName(String n);
 *         Builder lastName(String n);
 *         Builder dateOfBirth(Date d);
 *     }
 * }
 * </code></pre>
 * 
 * <p>You can then create the IBean and immediately initialize it via chained setters:</p>
 * 
 * <pre><code>
 *     Person personInstance = IBean.newOf(Person.Builder.class).
 *          firstName("Al").
 *          lastName("Bundy").
 *          dateOfBirth(new Date());
 * </code></pre>
 * 
 * <p>The bean style used here ({@link org.coliper.ibean.beanstyle.ModernBeanStyle}) uses getters
 * and setters without prefixes and returns the bean type from its setters. 
 * More details about bean styles in general can be found in the 
 * {@link org.coliper.ibean.BeanStyle} type
 * documentation.</p>
 * 
 * 
 * <h2 id="jackson-gson">Jackson and Gson Support</h2>
 *
 * IBeans can be serialized or deserialized to respectively from JSON. For this purpose the 
 * <em>IBean</em> framework provides an integration into the two most popular frameworks in that 
 * area, Jackson 2 and Gson.
 *
 * <p>Both integrations do not work out-of-the box. You need to integrate the bean factory into your
 * Jackson respectively Gson settings. How this is done is described in
 * {@link org.coliper.ibean.extension.GsonSerializerDeserializerForIBeans} and
 * {@link org.coliper.ibean.extension.Jackson2ModuleForIBeans}.</p>
 */
package org.coliper.ibean;
//@formatter:on 
