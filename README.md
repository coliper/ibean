# Coliper IBean

*IBean* is a Java library that allows to declare [Java beans][_JavaBeansSpec] - or 
[DTOs][_DTO] and [ValueObjects][_ValueObject] in general - as interfaces.

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

## Why *IBean*?

Main purpose of *IBean* is to free the developer of writing boilerplate code for Java beans or other
value object classes. 

## Description

In contrast to other libraries with the same purpose - like 
[AutoValue](https://github.com/google/auto/tree/master/value), 
[Joda-Bean](http://www.joda.org/joda-beans/), 
[Immutables](http://immutables.org/),
[Lombok](https://projectlombok.org/) or
[VALJOGen](http://valjogen.41concepts.com/) - IBean takes a interface style of bean definition 
and a runtime "code generation" approach.

Like some of the other mentioned frameworks IBean also provides solutions for the two major issues
with value objects
- immutability and
- null safety.

### Hello World Sample

Let's dig into an example. Classic way to represent a person by a good old Java bean class would be



### More Sophisticated Sample

## Quick Start

[_JavaBeansSpec](http://www.oracle.com/technetwork/java/javase/documentation/spec-136004.html)
[_DTO](http://en.wikipedia.org/wiki/Data_Transfer_Object)
[_ValueObject](http://martinfowler.com/bliki/ValueObject.html).

