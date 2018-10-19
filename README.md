# Space Worms

## How to install Space Worms

1. Run `git clone https://github.com/runi95/Sp4ceW0rms` to clone the repository.
2. Run `cd Sp4ceW0rms` to get into the newly cloned folder.
3. Create a new file called `application.properties` inside the `Sp4ceW0rms` folder and follow the instructions for how to set up your properties file.
4. Run `mvn install` to compile and install the project.

## How to set up application.properties

Your application.properties file should look like this:
```
server.port = 8080
buildNumber = @buildNumber@

spring.datasource.driverClassName = org.h2.Driver
spring.datasource.url = jdbc:h2:file:./SpaceWormsDB;FILE_LOCK=FS
spring.jpa.hibernate.ddl-auto = update
spring.jpa.hibernate.show-sql = true
spring.jpa.database-platform = org.hibernate.dialect.H2Dialect
spring.jpa.show-sql = false

spring.session.store-type = none

spring.freemarker.suffix = .ftlh
spring.freemarker.cache = true

api.url = <your-api-url>
```
Simply replace <your-api-url> with the actual API URL.


## How to start Space Worms

1. Run `java -jar target/SpaceWorms-1.0-SNAPSHOT.jar` to start the application.
2. Open a web browser and check out `localhost:8080`.
3. Enjoy!

## How to have fun

1. Play Space Worms.
2. Play Space Worms.
3. Play Space Worms.