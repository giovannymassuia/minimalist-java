# GitHub Packages

To use `minimalist-java` in your Maven project, follow the steps below:

### 1. Authenticate with GitHub Packages

Before you can use or install packages from the GitHub Package Registry, you need to authenticate.
GitHub Packages only
supports authentication via the personal access token at this time.

Add the following to your `~/.m2/settings.xml`:

```xml title="~/.m2/settings.xml"

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

```xml title="pom.xml"

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

```xml title="pom.xml"

<dependency>
    <groupId>io.giovannymassuia.minimalist.java</groupId>
    <artifactId>MODULE</artifactId>
    <version>LATEST_VERSION</version>
</dependency>

```

REPLACE `MODULE` with the desired [module](../modules) name.
Replace `LATEST_VERSION` with the desired version of the library.
