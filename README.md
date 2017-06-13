# cookbook
#Prerequisites
 - Java Development Kit 8
 - Maven
 - Git
 - mysql
 
# Activating
- git clone the repository
- run mvn install
- start mysql server
    - create database cookbook
	    - create user flyway with table modification, creation and deleting rights
	    - create user cookbook with only table reading and writing rights
	    - use the passwords written in application.properties
    - into cookbook.user insert into columns username, passwordHash and active the details of the user account you want to create 
 - run the jar file in .../cookbook/backend/target/ with java -jar filename.jar
