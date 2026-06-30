# Book Manager

Simple Java Desktop Application to manage Books with Authur Name and Categories.

## Badge
1. SonarCloud

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=Irfancpv99_BookManager&metric=alert_status&token=bee20093b8f3ee2131238a2677d1752166107b78)](https://sonarcloud.io/summary/new_code?id=Irfancpv99_BookManager) [![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=Irfancpv99_BookManager&metric=security_rating&token=bee20093b8f3ee2131238a2677d1752166107b78)](https://sonarcloud.io/summary/new_code?id=Irfancpv99_BookManager) [![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=Irfancpv99_BookManager&metric=vulnerabilities&token=bee20093b8f3ee2131238a2677d1752166107b78)](https://sonarcloud.io/summary/new_code?id=Irfancpv99_BookManager) [![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=Irfancpv99_BookManager&metric=sqale_rating&token=bee20093b8f3ee2131238a2677d1752166107b78)](https://sonarcloud.io/summary/new_code?id=Irfancpv99_BookManager) [![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=Irfancpv99_BookManager&metric=code_smells&token=bee20093b8f3ee2131238a2677d1752166107b78)](https://sonarcloud.io/summary/new_code?id=Irfancpv99_BookManager)  [![Bugs](https://sonarcloud.io/api/project_badges/measure?project=Irfancpv99_BookManager&metric=bugs&token=bee20093b8f3ee2131238a2677d1752166107b78)](https://sonarcloud.io/summary/new_code?id=Irfancpv99_BookManager) 

2. CodeCov

<a href="https://codecov.io/github/Irfancpv99/BookManager" > 
 <img src="https://codecov.io/github/Irfancpv99/BookManager/graph/badge.svg?token=JFHOR3XID8"/> 
 </a>

3. GitHub Actions build badge
  
 [![Java CI](https://github.com/Irfancpv99/BookManager/actions/workflows/maven.yml/badge.svg)](https://github.com/Irfancpv99/BookManager/actions/workflows/maven.yml)

## Featues 
- view books with author name and categories 
- add books 
- Update books
- delete books

## Tech Stack

- Java 17 - Core language
- MongoDB - Database
- Swing - GUI
- Maven - Build tool

### Testing & Quality Tools

- JUnit 5 - Testing
- Mockito - Mocking
- AssertJ & AssertJ Swing - Assertions
- JaCoCo - Code coverage
- PIT - Mutation testing
- SonarCloud - Code quality
- CodeCov - Coverage tracking

## Run Unit tests
```
mvn clean test
```

## Run Mutation Tests
```
mvn pitest:mutationCoverage
```

## Run Integration and E2E tests
```
- Must Start the mongoDB database before running the Integration and E2E Test

- Use "mvn docker:start" toi stop "mvn docker:stop"
```
## Run everything (unit + IT + E2E, MongoDB starts automatically)
```
mvn clean verify
```
or in Eclipse: right-click → Run As → Maven test

