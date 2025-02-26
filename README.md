# spring-poc

## Local Development

### First run

To ensure the correct version of Maven is used, run the following command:

```bash
./mvnw clean install
```

This will install the correct version of Maven if it is missing.

After the install command, you can check your Maven version with:

```bash
./mvnw -version
```

To update the Maven version, modify the `distributionUrl` in the `.mvn/wrapper/maven-wrapper.properties` file.

---

## Code Style

Before pushing your branch, make sure to apply the correct code formatting. Run the IntelliJ run configuration:
`Format Java Code Google Style`.

Additionally, every Maven build applies the code style defined in the `pom.xml` via the Spotless plugin. This ensures a
consistent code style across the project.

---

## IntelliJ Plugins

### Cucumber Plugins

#### Gherkin

This plugin is required for syntax highlighting of Cucumber tests in `.feature` files located under `/test/resources`.  
It also enables navigation from `.feature` files to Java code by holding `Ctrl` and left-clicking on the `Given`,
`When`, and `Then` keywords.

#### Cucumber for Java

This plugin is needed to run Cucumber tests directly from `.feature` files within IntelliJ.

---

## Project Overview

The `spring-poc` project is designed for experimenting with and demonstrating features of Spring technologies. It
supports local development through the Maven wrapper (`mvnw`) and enforces a standardized code style using the Google
style guide.

### Features:

- **Spring MVC**: Used for creating REST endpoints.
- **Spring Data JPA**: Included for database management with repositories.
- **Lombok**: Reduces boilerplate code (like getters, setters, etc.).
- **Cucumber Testing**: Allows behavior-driven development using `.feature` files.

The project maintains clean and readable code via linting and formatting tools applied during the build process.

### How to Contribute

1. Clone the repository:
   ```bash
   git clone <repository-url>
   cd spring-poc
   ```
2. Install Maven dependencies:
   ```bash
   ./mvnw clean install
   ```
3. Ensure you use the proper code formatting:
    - Run the IntelliJ `Format Java Code Google Style` or let the Maven build process apply the Spotless code style.

4. After making changes, ensure all tests pass:
   ```bash
   ./mvnw verify
   ```

5. Push your changes to a feature branch and open a pull request.

---

## Build & Test Workflow

To build the application:

```bash
./mvnw clean install
```

To run tests:

```bash
./mvnw verify
```

Spotless (code formatting check) will automatically run as part of the build process.

---

### Useful Links

- [Spring Framework Documentation](https://spring.io/projects/spring-framework)
- [Jakarta EE Documentation](https://jakarta.ee/specifications/)
- [Lombok Documentation](https://projectlombok.org/)
- [Cucumber Documentation](https://cucumber.io/docs/)

---
