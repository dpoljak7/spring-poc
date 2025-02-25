# spring-poc

# Local Development

## First run 

Run command to ensure same version of Maven is used:
`./mvnw clean install`

It will install correct version of maven if missing.

After install command run this command to check your version of Maven:
`./mvnw -version`


To update version of Maven update `distributionUrl` in file `.mvn/wrapper/maven-wrapper.properties`


# Code Style

Before pushing your branch run IntelliJ run configuration: `Format Java Code Google Style`
Note: every Maven build applies the code style defined in pom.xml (spotless plugin).

# Intellij plugins

## Cucumber plugins
### Gherkin 

Needed for syntax highlighting of Cucumber tests in .feature files under /test/resources.
Additionally, this will enable you to navigate to Java code from .feature file with Ctrl + left click on
each Given When Then line.

### Cucumber for Java
Needed for running Cucumber tests from .feature files directly in IntelliJ 