# Coliper IBean

*IBean* is a Java library that allows to declare [Java beans][_JavaBeansSpec] - or 
data transfer objects ([DTOs][_DTO]) and [value objects][_ValueObject] in general - as interfaces.
You can see it as an alternative to [Lombok][_LombokFramework].

For example a JavaBean like

```Java
public class Person {
    private String name;
    
    public String getName() {
        return this.name;
    }
    
    public void setName(String name) {
        this.name = name;
    } 
}
```

would be declared in *IBean* as follows:

```Java
public interface Person {
    String getName();
    void setName(String n);
}
```

And for creating a new instance of the bean, instead of saying ```new Person()``` you would
write ```IBean.newOf(Person.class)```.

IBean internally creates an implementation of the provided interface with expected behavior of
getter and setter methods and also with Object methods `toString`, `hashCode` and 
`equals` working as desired.


## Requirements

*IBean* requires Java 8 or greater.

*IBean* has dependencies to following frameworks:
* [Apache Commons Lang][_CommonsLang3Maven], version 3.2 or higher
* [Google Guava][_GuavaMaven], version 21 or higher

You do not have to declare these libraries in your Maven or Gradle dependencies. 
If not already present in your project these dependencies are automatically loaded.

If you want to use JSON convertion of *IBean* beans via *Gson* or
*Jackson* you need to have
[Gson][_GsonMaven], version 2.4 or higher, respectively
[Jackson Core][_JacksonCoreMaven] and
[Jackson Databind][_JacksonDatabindMaven], both version 2.6.1 or
higher, included in your project's dependencies. Here you have to explicitly define them in your
dependencies. The reason why these libararies are not automatically recursively loaded is that 
frameworks like Spring parse their classpath for these libraries and adopt their behavior if
they find them. To avoid this *IBean* does not load any JSON libarary on it's own.


## Quick Start

### Read This README

Gives you a good first overview.

### Include *IBean* In Your Project

#### Gradle

Add following dependency to your *build.gradle*:

```groovy
    implementation 'org.coliper:ibean:0.4.6'
```

#### Maven

Add following dependency to your *pom.xml*

```xml
    <dependency>
      <groupId>org.coliper</groupId>
      <artifactId>ibean</artifactId>
      <version>0.4.6</version>
    </dependency>
```

### Read the Developer Guide

We have integrated the developer guide into the [API docs][_PkgIbeanAPI].

### See an Extended Sample

tbd


## Why *IBean*?

* Main purpose of *IBean* is to free the developer of writing boilerplate code for Java beans 
  or other value object classes. Getter and setter methods only need to be declared but
  no implementation code needs to be written. Also implementation for Object methods  
  `toString`, `hashCode` and `equals` are automatically provided by *IBean*. Nevertheless,
   the way how these methods work can  be customized.
* You do not have to flag your beans with *IBean* specific annotations. You also do you have to list or
  configure your beans types anywhere. Just create a new bean interface and go!
* *IBean* provides a special way of [aspect oriented programming][_AspectOrientedProgramming].
  It allows to add behavior to beans just by interface extension. See [Extension Interfaces](#extension-interfaces)
  for more details.
* *IBean* allows to declare beans not only with the JavaBean way. It also allows more modern
  techniques that are nowadays used for value object declaration, for example builders,
  immutability, Optional-support or different signatures for getter and setter methods.
  See chapter [Bean Styles](#bean-styles) below for an example.  
* *IBean* provides JSON conversion support. *IBean* supports JSON frameworks
  [Jackson][_JacksonFramework] and [Gson][_GsonFramework]. How to use them with *IBean*
  refer to API documentation of [Jackson][_Jackson2ModuleForIBeans] and 
  [Gson][_GsonSerializerDeserializerForIBeans] support classes.    
* Declaring beans as interfaces allows multiple inheritance for the bean types. That sounds
  unimportant first but inheritance can be a very strong weapon when you have many and 
  complex value objects. With normal classes and single inheritance trying to create deep 
  bean class hierarchies almost always fails. You assemble beans almost only by 
  composition. With interfaces you have a powerful additional way to combine smaller bean
  types to bigger and more complex ones.     

Differences and benefits in contrast to other frameworks with similar purpose can be found below 
under [Alternative Frameworks](#alternative-frameworks).


## Extension Interfaces

*IBean* has a very powerful mechanism to inject generic functionality into
beans, so called *extension interfaces*. This mechanism is similar to
aspect oriented programming ([AOP][_AspectOrientedProgramming]). 
In Java you usually do AOP with
a framework like *AspectJ* or *Spring AOP* using annotations. That means, by 
labeling a method or a class with an annotation you
inject behavior into this method or class. In *IBean* you do similar, but instead
of using annotations you have your bean type extend one or more extension interfaces.

Let us see an example:

```Java
public interface Person extends ModificationAware, Freezable, NullSafe {
    String getName();
    void setName(String n);
}
```

Bean class `Person` extends three extension interfaces `ModificationAware`,
`Freezable` and `NullSafe`. By doing this you inject new functionality or
behavior to your bean. This functionality can either come from the methods
of the extension interfaces. Like for example `ModificationAware` contains
a method that tells you
whether a bean has been modified.

```Java
Person person = IBean.newOf(Person.class);
person.isModified();     // => false
person.setName("John");
person.isModified();     // => true
person.resetModified();  // make not dirty any more
person.isModified();     // => false
```

Methods `isModified` and `resetModified` are both supplied by interface
`ModificationAware`. See [ModificationAware API docs][_ModifcationAwareAPI] 
for more details.

On the other hand extension interfaces can also influence the behavior
of getter or setter methods. For example extension interface `NullSafe`
does not contain any method but changes the behavior of all getters
so that they never return null as value and throw an exception
instead. See [NullSafe API docs][_NullSafeAPI] for details.

Extension interface `Freezable` does both, it adds methods to the
bean and it changes the way how setters of work. By calling
`Freezable.freeze()` on the bean you make it immutable so that
any subsequent call to a setter causes an exception to be thrown
from the setter.

Of course not every interface can be used as an extension interface.
*IBean* provides several built-in extension interfaces which all can
be found in package [org.coliper.ibean.extension][_PkgExtensionAPI].
But you are able to define your own custom extension interfaces and
plug them in into *IBean* framework. How to do this is described in
the [ExtensionHandler API docs][_ExtensionHandlerAPI].


## Bean Styles

In the early Java years value objects or DTOs (data transfer objects)
have almost always been declared in JavaBean style. This changed
over time also under the influence of functional programming and
other programming languages. Developers now wanted to have their
beans to include concepts like immutability, chained declarations, builders
and null safety. *IBean* allows these concepts to be used in its beans.

Give it an example. The `Person` bean type from previous examples, extended
by date of birth, would be declared in the traditional JavaBean style like
this:

```Java
public interface Person {
    String getName();
    void setName(String n);
    LocalDate getDateOfBirth();
    void setDateOfBirth(LocalDate d);
}
```

Now assuming that `name` is mandatory for `Person` and DOB is optional, in
*IBean* you can declare the same bean type like this:

```Java
public interface Person extends {
    String name();
    Person name(String n);
    Optional<LocalDate> dateOfBirth();
    Person dateOfBirth(LocalDate d);
}
```

There are three differences between the two interface declarations:
1. The naming of the setters and getters is different.
2. Setters return the bean type (`Person`).
3. The getter or field *dateOfBirth* returns Java's `Optional` type indicating
   that its value might be null.

With that way of interface style you can create a `Person` instance like this:

```Java
Person person = IBean.newOf(Person.class)
    .name("John")
    .dateOfBirth(LocalDate.of(1977,1,1));
```

For a more complex example that also covers builders and immutability see
this [sample code][_BeanStyleSample].

If you might wonder how this `Optional` return type works, *IBean* has built-in
support for this. That means, if a field value is null the getter would
not return null but rather an empty `Optional` instance. But careful, you need
to use the right bean style that supports this.

Now to sum it up, a so called bean style in *IBean* defines two things
concerning bean interface declaration:
1. the naming of the getter and setter methods
2. the signature of the getter and setter methods

*IBean* currently has two built-in bean styles:
1. the "traditional" JavaBean style
   ([ClassicBeanStyle][_ClassicBeanStyleApi])
2. the more modern and sophisticated style as described in the sample above
   ([ModernBeanStyle][_ModernBeanStyleApi])

When using *IBean* you usually decide for a common bean style in your whole
code base. It is possible to have different styles in parallel but you
should have very good reasons for this.\
Besides the build-in styles it is possible to define your own bean style.
How to do this, how to choose a bean style and more information related you
find in [BeanStyle API docs][_BeanStyleApi].


## Alternative Frameworks

There are several excellent frameworks out there with the same purpose,
to free the user from writing boring boiler plate code for their
value objects. In spite of similar purpose they all differ in their
approaches and feature sets. These are probably the most important ones:
* [AutoValue][_AutoValueFramework] - provided and used by Google
* [Joda-Bean][_JodaBeanFramework] - from the inventors of *Joda-Time*
* [Immutables][_ImmutablesFramework] - probably the most powerful of all these
  frameworks
* [Lombok][_LombokFramework] - the veteran in this list
* [VALJOGen][_VALJOGenFramework]

You find comparisons and descriptions of some of this frameworks in the
net, for example on [DZone][_DzoneLessBetterCode] or in this
[blog][_BlogBeanCodeGeneration].

*IBean* differs to these frameworks mostly in following aspects:
* Most frameworks require a code generation process during build time.
  *IBean* does not need this and can be used out of the box.
* *IBean* uses interfaces to declare bean types whereas most of the other
  frameworks use abstract classes declaring only the fields. Abstract classes
  are a little shorter to write, interfaces give you more flexibility in
  composition of your bean types.
* Most frameworks do extensive use of annotations, *IBean* is completely
  free of any annotation.


## JSON Support

*IBean* supports JSON serialization and deserialization for
[Gson][_GsonFramework] and [Jackson2][_JacksonFramework]. If you want your bean type to be
convertible to and from JSON you need to use a certain
extension interface and you need to inject a handler into your
conversion framework. See API documentation for
[GsonSupport][_GsonSupportApi] and
[Jackson2Support][_Jackson2SupportApi] about details. This project also contains some
[sample code how to integrate Gson][_GsonSample].

*Hint:* If you use *Spring Framework* you normally have *Jackson2* under the
hood by default. In that case you only have to add the 
[JacksonModuleForIBeans][_Jackson2ModuleForIBeans] to your Jackson `ObjectMapper`.


[_JavaBeansSpec]: http://www.oracle.com/technetwork/java/javase/documentation/spec-136004.html
[_DTO]: http://en.wikipedia.org/wiki/Data_Transfer_Object
[_ValueObject]: http://martinfowler.com/bliki/ValueObject.html
[_AspectOrientedProgramming]: https://en.wikipedia.org/wiki/Aspect-oriented_programming
[_GsonFramework]: https://github.com/google/gson
[_JacksonFramework]: https://github.com/FasterXML/jackson
[_AutoValueFramework]: https://github.com/google/auto/tree/master/value 
[_JodaBeanFramework]: http://www.joda.org/joda-beans 
[_ImmutablesFramework]: http://immutables.org
[_LombokFramework]: https://projectlombok.org
[_VALJOGenFramework]: http://valjogen.41concepts.com
[_GsonMaven]: https://mvnrepository.com/artifact/com.google.code.gson/gson
[_JacksonCoreMaven]: https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-core
[_JacksonDatabindMaven]: https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-databind
[_CommonsLang3Maven]: https://mvnrepository.com/artifact/org.apache.commons/commons-lang3
[_GuavaMaven]: https://mvnrepository.com/artifact/com.google.guava/guava
[_BlogBeanCodeGeneration]: http://blog.joda.org/2016/09/code-generating-beans.html
[_DzoneLessBetterCode]: https://www.javacodegeeks.com/2018/03/lombok-autovalue-and-immutables-or-how-to-write-less-and-better-code-returns.html
[_GsonSerializerDeserializerForIBeans]: api/org/coliper/ibean/extension/GsonSerializerDeserializerForIBeans.html
[_Jackson2ModuleForIBeans]: api/org/coliper/ibean/extension/Jackson2ModuleForIBeans.html
[_GsonSupportApi]: api/org/coliper/ibean/extension/GsonSupport.html
[_Jackson2SupportApi]: api/org/coliper/ibean/extension/Jackson2Support.html
[_ModifcationAwareAPI]: api/org/coliper/ibean/extension/ModificationAware.html
[_NullSafeAPI]: api/org/coliper/ibean/extension/NullSafe.html
[_PkgExtensionAPI]: api/org/coliper/ibean/extension/package-summary.html
[_PkgIbeanAPI]: api/org/coliper/ibean/package-summary.html
[_ExtensionHandlerAPI]: api/org/coliper/ibean/proxy/ExtensionHandler.html
[_BeanStyleApi]: api/org/coliper/ibean/BeanStyle.html
[_ClassicBeanStyleApi]: api/org/coliper/ibean/beanstyle/ClassicBeanStyle.html
[_ModernBeanStyleApi]: api/org/coliper/ibean/beanstyle/ModernBeanStyle.html
[_BeanStyleSample]: https://github.com/coliper/ibean/blob/master/src/test/java/org/coliper/ibean/samples/ModernBeanStyleSample.java
[_GsonSample]: https://github.com/coliper/ibean/blob/master/src/test/java/org/coliper/ibean/samples/GsonSample.java
