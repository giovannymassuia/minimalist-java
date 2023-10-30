# Minimalist Java

Minimalist Java is a collection of lightweight Java libraries aiming to simplify and streamline your Java development.
This project is structured as a monorepo, containing multiple individual libraries that can be used independently or
together for a cohesive development experience.

## Modules

1. **Minimalist API (`minimalist-api`)**: Provides essential API functionalities for Java applications.
2. **Minimalist Dependency Injection (`minimal-dependency-injection`)**: A lightweight solution for dependency injection
   in Java.

### Using Minimalist BOM (Bill of Materials)

To ensure consistent dependency versions across your projects, we provide a Bill of Materials (BOM). By importing the
Minimalist BOM, you won't have to specify versions for any of the Minimalist libraries you use.

#### Maven

To use the BOM:

```xml

<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>io.giovannymassuia.minimalist.java</groupId>
            <artifactId>minimalist-java-bom</artifactId>
            <version>USE_LATEST_VERSION</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

With the BOM imported, add the desired Minimalist modules:

```xml

<dependencies>
    <dependency>
        <groupId>io.giovannymassuia.minimalist.java</groupId>
        <artifactId>api</artifactId>
    </dependency>
    <dependency>
        <groupId>io.giovannymassuia.minimalist.java</groupId>
        <artifactId>di</artifactId>
    </dependency>
</dependencies>
```

### Without Using Minimalist BOM

If you choose not to use the BOM, you'll need to specify versions for each Minimalist module you use.

#### Maven

```xml

<dependencies>
    <dependency>
        <groupId>io.giovannymassuia.minimalist.java</groupId>
        <artifactId>api</artifactId>
        <version>USE_LATEST_VERSION</version>
    </dependency>
    <dependency>
        <groupId>io.giovannymassuia.minimalist.java</groupId>
        <artifactId>di</artifactId>
        <version>USE_LATEST_VERSION</version>
    </dependency>
</dependencies>
```

---

## Updating POMs versions

To update the versions of all POMs in the project, run the following command:

```shell
mvn versions:set -DnewVersion=NEW_VERSION
```

To revert the changes, run:

```shell
mvn versions:revert
```

## Contributing

If you'd like to contribute to Minimalist Java, please read our [contributing guidelines](CONTRIBUTING.md).
