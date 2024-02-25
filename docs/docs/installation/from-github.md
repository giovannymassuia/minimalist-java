# GitHub Packages

To use `minimalist-java` in your Maven project, follow the steps below:

### 1. Authenticate with GitHub Packages

Before you can use or install packages from the GitHub Package Registry, you need to authenticate.
GitHub Packages only
supports authentication via the personal access token at this time.

Add the following to your `~/.m2/settings.xml`:

```xml

<servers>
    <server>
        <id>github</id>
        <username>YOUR_GITHUB_USERNAME</username>
        <password>YOUR_PERSONAL_ACCESS_TOKEN</password>
    </server>
</servers>
```

Replace `YOUR_GITHUB_USERNAME` with your GitHub username and `YOUR_PERSONAL_ACCESS_TOKEN` with a
personal access token
that has the `read:packages` scope (also ensure you have the `repo` and `write:packages` scopes if
you plan to publish
packages).

### 2. Add the Repository

In your project's `pom.xml`, add the following repository:

```xml

<repositories>
    <repository>
        <id>github</id>
        <url>https://maven.pkg.github.com/giovannymassuia/minimalist-java</url>
    </repository>
</repositories>
```

Replace `OWNER` with the repository owner's username and `REPO` with the name of the repository.

### 3. Add the Dependency

Next, you can add the dependency to your project:

```xml

<dependency>
    <groupId>io.giovannymassuia.minimalist.java</groupId>
    <artifactId>http-api</artifactId>
    <version>LATEST_VERSION</version>
</dependency>
```

Replace `LATEST_VERSION` with the desired version of the library.

### 4. (Optional) Using the BOM

If you're planning to use multiple modules from `minimalist-java`, consider adding the BOM (Bill of
Materials) to your
project. It will help manage versions for you.

Add the BOM to your `dependencyManagement` section:

```xml

<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>io.giovannymassuia.minimalist.java</groupId>
            <artifactId>bom</artifactId>
            <version>LATEST_VERSION</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

Now, when adding individual `minimalist-java` modules to your dependencies, you don't need to
specify their versions:

```xml

<dependencies>
    <dependency>
        <groupId>io.giovannymassuia.minimalist.java</groupId>
        <artifactId>http-api</artifactId>
    </dependency>
    <dependency>
        <groupId>io.giovannymassuia.minimalist.java</groupId>
        <artifactId>di</artifactId>
    </dependency>
</dependencies>
```
