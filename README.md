# DNSFilter

Simple DNS Server that blocks queries based on a blocklist.

## Creating the JAR
The project uses [Apache Maven](https://maven.apache.org/) CLI to build the distribution as a FatJAR.

### Installing dependencies

```bash
mvn clean install
```

### Packaging the JAR
```bash
mvn package
```

## Running the JAR
```bash
java -jar target/DNSFilter-x.x.jar
```
