# Book Manager

Simple Java app to manage books and categories.

## What it does
- view books and categories
- add books (with basic validation)
- delete books

## Stack
- Java + Maven
- JUnit 5 + Mockito

## Run tests
```
mvn clean test
```
or in Eclipse: right-click → Run As → Maven test

## Structure
```
src/main/java/com/bookmanager/
  model/       - Book, Category
  repository/  - data access
  service/     - business logic
  view/        - view interface
src/test/java/ - unit tests
```
