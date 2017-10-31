# Coliper IBean

IBean is a Java library for an interface based way to deal with Java beans or other forms of 
logicless data-only classes like 
[data transfer objects](http://en.wikipedia.org/wiki/Data_Transfer_Object)
(DTO) or [value objects](http://martinfowler.com/bliki/ValueObject.html).

## Description

Main purpose of IBean is to free the developer of writing boilerplate code for Java beans or other
value object classes. In contrast to other libraries with the same purpose - like 
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