# Coliper IBean - AI Agent Documentation

## Project Overview

**IBean** is a Java library for declaring Java beans, DTOs (Data Transfer Objects), and value objects as interfaces instead of classes. It serves as an alternative to frameworks like Lombok, eliminating boilerplate code while providing powerful extension mechanisms.

### Key Information
- **Project Name**: Coliper IBean
- **Version**: 0.4.6
- **Group ID**: org.coliper
- **Artifact ID**: ibean
- **Language**: Java (Version 17)
- **Build Tool**: Gradle
- **Main Package**: org.coliper.ibean

## Core Concepts

### 1. Interface-Based Bean Declaration
Instead of writing traditional JavaBean classes with fields, getters, and setters, IBean allows developers to declare beans as interfaces:

```java
// Traditional approach (NOT used in IBean)
public class Person {
    private String name;
    public String getName() { return this.name; }
    public void setName(String name) { this.name = name; }
}

// IBean approach
public interface Person {
    String getName();
    void setName(String n);
}

// Creating instances
Person person = IBean.newOf(Person.class);
```

### 2. Extension Interfaces (AOP-like Mechanism)
IBean provides an aspect-oriented programming approach through extension interfaces that inject functionality into beans:

- **ModificationAware**: Tracks if a bean has been modified
- **Freezable**: Makes beans immutable after calling freeze()
- **NullSafe**: Prevents getters from returning null (throws exception instead)
- **LazyInit**: Lazy initialization support
- **GsonSupport**: JSON serialization with Gson
- **Jackson2Support**: JSON serialization with Jackson

Example:
```java
public interface Person extends ModificationAware, Freezable, NullSafe {
    String getName();
    void setName(String n);
}
```

### 3. Bean Styles
IBean supports different bean declaration patterns:

**ClassicBeanStyle** (Traditional JavaBeans):
```java
public interface Person {
    String getName();
    void setName(String n);
    LocalDate getDateOfBirth();
    void setDateOfBirth(LocalDate d);
}
```

**ModernBeanStyle** (with fluent API, Optional support):
```java
public interface Person {
    String name();
    Person name(String n);  // Returns Person for chaining
    Optional<LocalDate> dateOfBirth();
    Person dateOfBirth(LocalDate d);
}

// Usage
Person person = IBean.newOf(Person.class)
    .name("John")
    .dateOfBirth(LocalDate.of(1977, 1, 1));
```

## Project Structure

### Source Organization
```
src/
├── main/java/org/coliper/ibean/
│   ├── IBean.java                    # Main entry point class
│   ├── IBeanFactory.java             # Factory interface for bean creation
│   ├── BeanStyle.java                # Bean style abstraction
│   ├── IBeanFieldMetaInfo.java       # Metadata about bean fields
│   ├── beanstyle/                    # Bean style implementations
│   │   ├── ClassicBeanStyle.java
│   │   └── ModernBeanStyle.java
│   ├── extension/                    # Extension interfaces
│   │   ├── ModificationAware.java
│   │   ├── Freezable.java
│   │   ├── NullSafe.java
│   │   ├── LazyInit.java
│   │   ├── GsonSupport.java
│   │   └── Jackson2Support.java
│   ├── proxy/                        # Proxy-based implementation
│   │   ├── ProxyIBeanFactory.java
│   │   ├── ExtensionHandler.java
│   │   └── ExtensionSupport.java
│   ├── util/                         # Utility classes
│   └── codegen/                      # Code generation support
└── test/java/                        # Test files
```

### Key Dependencies
- **Apache Commons Lang3**: 3.4+ (required)
- **Google Guava**: 21.0+ (required)
- **JavaPoet**: 1.11.1 (for code generation)
- **Gson**: 2.4+ (optional, for JSON support)
- **Jackson Core/Databind**: 2.6.1+ (optional, for JSON support)

### Build Configuration
- Uses Gradle for build management
- Java source/target compatibility: Java 17
- Publishes to Maven Central
- Includes javadoc generation with custom stylesheet

## Development Guidelines for AI Agents

### When Adding New Features

1. **Extension Interfaces**: When creating new extension interfaces:
   - Place them in `org.coliper.ibean.extension` package
   - Create corresponding `ExtensionHandler` implementations in `org.coliper.ibean.proxy`
   - Update documentation in package-info.java files
   - Add comprehensive unit tests

2. **Bean Styles**: When adding new bean styles:
   - Extend or implement the `BeanStyle` interface
   - Place implementation in `org.coliper.ibean.beanstyle` package
   - Document naming and signature patterns clearly
   - Provide sample usage in test code

3. **Core Framework Changes**:
   - Maintain backward compatibility
   - Update version numbers following semantic versioning
   - Modify javadoc build configuration if needed

### Code Quality Standards

1. **Naming Conventions**:
   - Use clear, descriptive names for interfaces and classes
   - Extension interfaces should describe their capability (e.g., `ModificationAware`, `Freezable`)
   - Avoid abbreviations unless widely recognized

2. **Documentation**:
   - All public APIs must have comprehensive JavaDoc
   - Include usage examples in JavaDoc
   - Update README.md for user-facing changes
   - Maintain userguide.md in doc-src/

3. **Testing**:
   - Write unit tests for all new functionality
   - Use JUnit 4.12
   - Use AssertJ for assertions (preferred style)
   - Include integration tests for extension interfaces

### Common Development Tasks

#### Adding a New Extension Interface

1. Create interface in `org.coliper.ibean.extension/`
2. Create handler in `org.coliper.ibean.proxy/`
3. Register handler in `ProxyIBeanFactory`
4. Add tests in `src/test/java/`
5. Update API documentation

#### Modifying Bean Creation Logic

- Main class: `IBean.java` (static factory)
- Factory implementation: `ProxyIBeanFactory.java`
- Metadata parsing: `CachedIBeanMetaInfoParser.java`

#### Working with JSON Support

- Gson integration: `GsonSerializerDeserializerForIBeans`
- Jackson integration: `Jackson2ModuleForIBeans`
- Both require corresponding extension interfaces

### Testing Guidelines

- Test files mirror source structure under `src/test/java/`
- Sample code examples in `org.coliper.ibean.samples` package
- Focus areas for testing:
  - Bean creation and initialization
  - Extension interface behavior
  - Bean style variations
  - Edge cases (null handling, immutability, etc.)
  - JSON serialization/deserialization

### Documentation Structure

- **README.md**: User-facing documentation (generated from templates)
- **doc-src/intro.templ.md**: Template for README with version placeholders
- **doc-src/userguide.md**: Comprehensive user guide
- **docs/**: GitHub Pages documentation
- **docs/api/**: Generated JavaDoc API documentation

### Build and Release Process

```bash
# Build the project
./gradlew build

# Generate JavaDoc
./gradlew javadoc

# Run tests
./gradlew test

# Create distribution
./gradlew jar
```

### Important Implementation Details

1. **Proxy-based Implementation**: IBean uses Java dynamic proxies internally to implement interface-based beans
2. **Caching**: Bean metadata is cached for performance
3. **Thread Safety**: Consider thread safety when working with extension handlers
4. **Reflection**: Heavy use of reflection for metadata discovery and bean creation

### Common Patterns

#### Bean Factory Pattern
```java
IBeanFactory factory = ProxyIBeanFactory.builder()
    .withDefaultInterfaceSupport()
    .build();
IBean.setFactory(factory);
```

#### Extension Handler Registration
Extension handlers are registered during factory creation and dispatched through `ExtensionHandlerDispatcher`.

## AI Agent Workflow Recommendations

1. **Understanding Changes**: Always read relevant source files before making changes
2. **Impact Analysis**: Check for interface usage across the codebase
3. **Test First**: Review existing tests to understand expected behavior
4. **Documentation**: Update javadoc and user documentation simultaneously with code changes
5. **Consistency**: Follow existing patterns for similar features
6. **Version Awareness**: Be mindful of Java version requirements and dependency versions

## Quick Reference

### Main Entry Points
- `IBean.newOf(Class<T>)` - Create new bean instance
- `ProxyIBeanFactory` - Factory implementation
- `BeanStyle` - Interface for custom bean styles
- `ExtensionHandler` - Interface for custom extensions

### Key Packages
- `org.coliper.ibean` - Core framework
- `org.coliper.ibean.extension` - Built-in extensions
- `org.coliper.ibean.beanstyle` - Bean style implementations
- `org.coliper.ibean.proxy` - Proxy implementation details
- `org.coliper.ibean.util` - Utility classes

## Additional Resources

- API Documentation: Generated JavaDoc in `docs/api/`
- User Guide: `doc-src/userguide.md`
- Sample Code: `src/test/java/org/coliper/ibean/samples/`
- Maven Central: https://mvnrepository.com/artifact/org.coliper/ibean
