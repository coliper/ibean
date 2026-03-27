# GitHub Copilot Instructions for Coliper IBean

## Project Context

You are working on **Coliper IBean**, a Java library that enables interface-based bean declaration as an alternative to traditional JavaBean classes and annotation-based frameworks like Lombok.

### Technology Stack
- **Language**: Java 17
- **Build Tool**: Gradle
- **Testing**: JUnit 4.12, AssertJ 3.8.0
- **Core Dependencies**: Apache Commons Lang3, Google Guava, JavaPoet
- **Optional Dependencies**: Gson, Jackson (for JSON support)

## Code Style and Conventions

### General Java Conventions

1. **Use Java 17 features** but maintain compatibility with the project's baseline
2. **Prefer interfaces over abstract classes** - this is the core philosophy of IBean
3. **Null safety**: Use `requireNonNull` from `java.util.Objects` for parameter validation
4. **Immutability**: Prefer immutable designs where appropriate
5. **Method chaining**: Support fluent APIs when designing bean-style interfaces

### Naming Conventions

1. **Interfaces**: Use descriptive nouns (e.g., `ModificationAware`, `Freezable`, `NullSafe`)
2. **Extension interfaces**: Should describe capability, not implementation
3. **Handlers**: Use `Handler` suffix (e.g., `ExtensionHandler`, `FreezableHandler`)
4. **Factory classes**: Use `Factory` suffix (e.g., `IBeanFactory`, `ProxyIBeanFactory`)
5. **Bean methods**: 
   - Classic style: `getPropertyName()`, `setPropertyName()`
   - Modern style: `propertyName()`, `propertyName(value)` with chaining

### Package Organization

```
org.coliper.ibean/
├── [root]              # Core classes (IBean, IBeanFactory, BeanStyle)
├── beanstyle/          # Bean style implementations
├── extension/          # Extension interfaces and support classes
├── proxy/              # Proxy-based implementation internals
├── util/               # Utility classes
└── codegen/            # Code generation utilities
```

## Coding Patterns

### Extension Interface Pattern

When creating extension interfaces:

```java
/**
 * Extension interface that [describe functionality].
 * 
 * <p>Usage example:
 * <pre><code>
 * public interface MyBean extends YourExtension {
 *     // bean methods
 * }
 * </code></pre>
 * 
 * @see YourExtensionHandler
 */
public interface YourExtension {
    // methods that inject behavior
}
```

### Extension Handler Pattern

```java
public class YourExtensionHandler implements ExtensionHandler {
    @Override
    public Object handleMethodCall(Object proxy, Method method, 
                                   Object[] args, ...) {
        // implementation
    }
}
```

### Bean Factory Pattern

When working with factory code:
```java
IBeanFactory factory = ProxyIBeanFactory.builder()
    .withBeanStyle(new ModernBeanStyle())
    .withExtensionHandler(YourExtension.class, new YourExtensionHandler())
    .build();
```

## Documentation Standards

### JavaDoc Requirements

1. **All public APIs** must have comprehensive JavaDoc
2. **Include usage examples** in JavaDoc using `<pre><code>` blocks
3. **Link to related classes** using `{@link ClassName}`
4. **Document exceptions** with `@throws` tags
5. **Include `@see` tags** for related functionality

Example:
```java
/**
 * Creates a new instance of the specified bean type.
 * <p>
 * Sample usage:
 * <pre><code>
 * Person person = IBean.newOf(Person.class);
 * person.setName("John Doe");
 * </code></pre>
 * 
 * @param <T> the bean type
 * @param beanType the class object of the bean interface
 * @return a new instance implementing the bean interface
 * @throws NullPointerException if beanType is null
 * @see IBeanFactory#create(Class)
 */
```

### Code Comments

- Use `//` for single-line explanatory comments
- Avoid obvious comments - code should be self-documenting
- Explain "why" not "what" in comments
- Use TODO comments sparingly and always with context

## Testing Guidelines

### Test Structure

1. **Mirror source structure** in test directories
2. **One test class per source class** typically
3. **Use descriptive test method names**: `testMethodName_Condition_ExpectedBehavior()`
4. **Use AssertJ** for assertions (modern, fluent style)

### Test Example

```java
@Test
public void newOf_ValidInterface_CreatesInstance() {
    // Arrange
    Class<SampleBean> beanType = SampleBean.class;
    
    // Act
    SampleBean bean = IBean.newOf(beanType);
    
    // Assert
    assertThat(bean).isNotNull();
    assertThat(bean).isInstanceOf(SampleBean.class);
}
```

### What to Test

- Bean creation and initialization
- Extension interface behavior
- Getter/setter functionality across different bean styles
- Edge cases (null handling, exceptions, immutability)
- Integration between extensions
- JSON serialization/deserialization (if applicable)

## Common Development Tasks

### Adding a New Extension Interface

1. Create interface in `org.coliper.ibean.extension`
2. Add JavaDoc with usage examples
3. Create corresponding handler in `org.coliper.ibean.proxy`
4. Register handler in factory builder
5. Write comprehensive tests
6. Add samples in `src/test/java/.../samples/`
7. Update package documentation if needed

### Modifying Core Bean Creation Logic

1. Review `IBean.java` for static factory methods
2. Check `ProxyIBeanFactory.java` for factory implementation
3. Understand `CachedIBeanMetaInfoParser.java` for metadata
4. Update tests in parallel
5. Consider backward compatibility

### Working with Bean Styles

1. Implement `BeanStyle` interface
2. Define method naming patterns clearly
3. Document signature requirements
4. Provide sample bean interfaces
5. Test with various property types

## Error Handling

1. **Validate inputs**: Use `requireNonNull` for required parameters
2. **Use appropriate exceptions**:
   - `NullPointerException` for null arguments
   - `IllegalArgumentException` for invalid values
   - `IllegalStateException` for invalid object state
   - Custom exceptions when more specific error handling needed
3. **Provide clear error messages**: Include context about what went wrong

Example:
```java
public static <T> T newOf(Class<T> beanType) {
    requireNonNull(beanType, "beanType must not be null");
    if (!beanType.isInterface()) {
        throw new IllegalArgumentException(
            "beanType must be an interface, got: " + beanType.getName());
    }
    return factory.create(beanType);
}
```

## Performance Considerations

1. **Use caching**: Bean metadata is cached via `CachedIBeanMetaInfoParser`
2. **Avoid reflection in hot paths** when possible
3. **Lazy initialization**: Use lazy patterns for expensive operations
4. **Reuse objects**: Don't create unnecessary objects in loops
5. **Profile before optimizing**: Measure actual performance impact

## Version Compatibility

- **Maintain backward compatibility** for public APIs
- Use `@Deprecated` annotation for obsolete methods
- Document breaking changes clearly
- Follow semantic versioning: MAJOR.MINOR.PATCH

## Gradle Build Tasks

Commonly used tasks:
```bash
./gradlew build           # Build entire project
./gradlew test            # Run tests
./gradlew javadoc         # Generate API docs
./gradlew jar             # Create JAR
./gradlew clean           # Clean build artifacts
```

## Special Considerations

### Reflection Usage
- IBean heavily uses reflection for bean introspection
- Be mindful of Java module system restrictions (if applicable)
- Cache reflection results to minimize overhead

### Proxy Usage
- Uses Java dynamic proxies (`java.lang.reflect.Proxy`)
- All bean interfaces are implemented as proxy instances
- Understand proxy limitations (interface-only, performance implications)

### Thread Safety
- Consider thread safety in extension handlers
- Document thread-safety guarantees in JavaDoc
- Use appropriate synchronization when needed

## Code Review Checklist

Before suggesting code:
- [ ] Follows existing code style and patterns
- [ ] Includes comprehensive JavaDoc
- [ ] Has corresponding unit tests
- [ ] Handles null and edge cases appropriately
- [ ] Maintains backward compatibility
- [ ] Uses appropriate Java 17 features
- [ ] Follows naming conventions
- [ ] No hardcoded values without constants
- [ ] Error messages are clear and helpful
- [ ] Updates documentation if needed

## Helpful Context for Suggestions

When suggesting code, prefer:
- **Interface-based designs** over class-based
- **Fluent APIs** for better usability
- **Immutable objects** when appropriate
- **Builder patterns** for complex object construction
- **Extension mechanisms** for adding functionality
- **Clear separation** between API and implementation

Avoid:
- Breaking existing public APIs
- Introducing unnecessary dependencies
- Complex inheritance hierarchies
- Mutable static state
- Catching generic exceptions without re-throwing

## Resources

- Main API documentation: `docs/api/`
- User guide: `doc-src/userguide.md`
- Sample code: `src/test/java/org/coliper/ibean/samples/`
- Build configuration: `build.gradle`
- Package documentation: `package-info.java` files

---

**Remember**: The core philosophy of IBean is to eliminate boilerplate while maintaining type safety and flexibility through interface-based design. Keep this principle in mind when suggesting or generating code.
