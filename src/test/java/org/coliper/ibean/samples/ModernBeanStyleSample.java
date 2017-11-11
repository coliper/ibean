/*
 * Copyright (C) 2017 the original author or authors
 */

package org.coliper.ibean.samples;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import java.time.LocalDate;
import java.util.Optional;
import java.util.function.Function;

import org.coliper.ibean.BeanStyle;
import org.coliper.ibean.IBean;
import org.coliper.ibean.extension.BeanIncompleteException;
import org.coliper.ibean.extension.CloneableBean;
import org.coliper.ibean.extension.Completable;
import org.coliper.ibean.extension.OptionalSupport;
import org.coliper.ibean.proxy.ProxyIBeanFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

//@formatter:off

/**
 * This is a test case that shows an example usage of the IBean framework. In particular it shows
 *   - alternate configuration of the bean factory 
 *   - usage of ModernBeanStyle
 *   - usage of extension interfaces Completable and OptionalSupport to prevent null-pointer errors
 *   - usage of default methods to add additional functionality to beans
 *   
 * To achieve immutability of beans we use a technique to move all setter methods to a nested 
 * interface. This technique is not IBean specific and could also be used with traditional 
 * Java beans. 
 * Immutable beans cannot be modified after creation. When now a bean with one or modified fields
 * is needed you create a clone of the existing bean and modify the respective fields during 
 * creation. This test case shows how to do this with IBean.
 * 
 * Purpose of this test case is not primarily testing but more being a show case for the usage of
 * the IBean framework. Nevertheless, writing the show case as a test it guarantees the sample  
 * code to work properly.
 *  
 * The test case contains only one real test method, showUsage(). It contains following parts:  
 *   - Method initIBean() shows how to reconfigure the bean factory (here to use modern bean style).
 *   - We define interface CommonBeanSuperInterface as a bundle for used extension interfaces.
 *   - Interface Person is a sample of a bean type how it could exist in a real world scenario.
 *   - showUsage() demonstrates creation of Person beans and accessing its methods and fields  
 */
public class ModernBeanStyleSample {

    @Before
    public void initIBean() {
        // We use a modern bean style with Optional support. Therefore we need
        // to set a custom factory into IBean. In real life you will do this
        // once during initialization of the application.
        IBean.setFactory(ProxyIBeanFactory.builder().withDefaultInterfaceSupport()
                .withBeanStyle(BeanStyle.MODERN_WITH_OPTIONAL).build());
    }

    // Usually you have a lot of bean classes that should all behave similar.
    // Therefore it is good practice if you create a common super interface 
    // for all your bean interfaces.
    public static interface CommonBeanSuperInterface<T>
            extends CloneableBean<T>, Completable<T>, OptionalSupport {
    }

    // Interface Person is the bean type used in our example. It has mandatory and optional fields.
    // It is supposed to be immutable, that is, all fields, optional and mandatory ones, can
    // only be set during creation.
    public static interface Person extends CommonBeanSuperInterface<Person.Builder> {
        // getters of all fields; 
        // firstName and lastName are mandatory, dateOfBirth may be null
        String firstName();
        String lastName();
        Optional<LocalDate> dateOfBirth();

        // All setters are moved to to a sub interface Builder. Builder is only
        // used once during initialization of the bean to set all mandatory
        public static interface Builder extends Person {
            Person.Builder firstName(String s);
            Person.Builder lastName(String s);
            Person.Builder dateOfBirth(LocalDate d);
        }

        // Instances of Person are supposed to be created only with this method. We do this for
        // several reasons:
        //   - Outside of the creation process we only want to deal with interface Person, rather
        //     than with Person.Builder.
        //   - Reused creation code, that is, calls of IBean.of() and assertComplete() go into this
        //     method and do not need be called on every creation. And you ensure that call of
        //     assertComplete() is not forgotten.
        static Person create(Function<Person.Builder, Person> initFunc) {
            return initFunc.apply(IBean.newOf(Person.Builder.class)).assertComplete();
        }

        // Example how to easily add additional functionality to a bean using
        // Java8 default methods.
        default Optional<Integer> getAge() {
            if (!dateOfBirth().isPresent()) {
                return Optional.empty();
            }
            return Optional.of(dateOfBirth().get().until(LocalDate.now()).getYears());
        }
    }

    // Now we use our Person class in following test method.
    // This method shows how to create Person beans and how to create modified clones of existing
    // Person beans.
    @Test
    public void showUsage() {
        Person person;
        
        // Persons are created using the create method together with a lambda expression that 
        // initializes at least all mandatory fields and eventually some optional ones. 
        person = Person.create(p -> p
                .firstName("John")
                .lastName("Doe")
                .dateOfBirth(LocalDate.of(1940, 10, 9)));
        assertThat(person.lastName()).isEqualTo("Doe");

        // Create a new person WITHOUT optional birthday.
        person = Person.create(p -> p.firstName("John").lastName("Lennon"));
        assertThat(person.firstName()).isEqualTo("John");
       
        // Here we can see the effect of interface OptionalSupport. Even though the dateOfBirth
        // has not been initialized we receive an (empty) Optional value from the getter.
        assertThat(person.dateOfBirth().isPresent()).isFalse();
        
        // If we create a Person and do not set all mandatory fields we run into a 
        // BeanIncompleteException. Let's try it and set only lastName.
        assertThatExceptionOfType(BeanIncompleteException.class).isThrownBy(() ->
                Person.create(p -> p.lastName("Lastnameonly")));

        // All object methods (toString(), hashCode(), equals()) work as expected. For example
        assertThat(person.toString()).contains("Person.Builder").contains("firstName=John");
        
        // As setters are not accessible Person objects are immutable.
        //
        // Looking at functional programming concepts immutable objects often have mutator methods
        // but the trick is that calling such a method does not modify the instance itself but
        // instead creates another object with the changed value.
        // Perfect examples for this are the Java8 date and time classes. For example
        // LocalTime.minus() creates a new LocalTime object with the subtracted time.
        //
        // We can now simulate something similar with our Person bean. We create an identical 
        // Person object with firstName and lastName modified.
        person = person.clone().firstName("John Winston").dateOfBirth(LocalDate.of(1940, 10, 9));
        assertThat(person.lastName()).isEqualTo("Lennon");
    }

    @After
    public void resetIBeanToDefault() {
        // We modified the IBean bean factory so we reset it to default to not affect other tests.
        // In real life in most cases you use only one configuration, so this would not be 
        // necessary.
        IBean.resetToDefaultFactory();
    }

    //@formatter:on
}
