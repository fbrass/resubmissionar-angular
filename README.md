# Resubmissionar application
The Resubmissionar application demonstrates a simple CRM tool to manage customers and partners. Its aim is to support test and development of required features of modern enterprise applications.

## Features
- JEE7 web profile application
- JPA 2.x
- Service interface using REST | JSon
- [AngularJS](https://angularjs.org) frontend
- [Bootstrap](http://getbootstrap.com) for HTML5 components
- [AngularUI](http://angular-ui.github.io/bootstrap/) for advanced HTML5 components
- Web package management with [bower](http://bower.io)
- Java 8 [stream optimizations, lambdas](http://www.techempower.com/blog/2013/03/26/everything-about-java-8/)
- Java 7 resource [management](http://www.oracle.com/technetwork/articles/java/trywithresources-401775.html)
- Servlets 3.0 for binary up- and downloads

## Requirements
- [Java 8]
- JEE7 Application Server (e.g. [WildFly] >= 8.1)
- Database, ideally supporting nested transactions (e.g. [PostgreSQL] >= 9.3.x)

## Running
Required
- Database connectible via JNDI `java:/jdbc/resubmissionar`
- Application Server

Steps
- `mvn clean package`
- Deploy `$PROJECT_ROOT/target/resubmissionar-angular.war` using application server console
- Browse to [http://localhost:8080/resubmissionar-angular/](http://localhost:8080/resubmissionar-angular/)

## Setting up PostgreSQL
The following steps assume a pristine installation of [PostgreSQL].

### Installing PostgreSQL on OS X
```
$ brew install postgresql
$ ln -sfv /usr/local/opt/postgresql/*.plist ~/Library/LaunchAgents
$ launchctl load ~/Library/LaunchAgents/homebrew.mxcl.postgresql.plist
```

### Setup PostgreSQL
1. Create user `resubmissionar` which is used by WildFly to connect to the database.
2. Create database `resubmissionar`.

```
$ createuser -P -s -e resubmissionar
Enter password for new role: 
Enter it again: 
CREATE ROLE resubmissionar PASSWORD 'md5ba899053e32755738fe5e17f744455ab' SUPERUSER CREATEDB CREATEROLE INHERIT LOGIN;
$ createdb resubmissionar
```

Test the setup by connecting with `psql` to the `resubmissionar` database:
```
$ psql resubmissionar
psql (9.3.4)
Type "help" for help.

resubmissionar=# \du
                                List of roles
   Role name    |                   Attributes                   | Member of 
----------------+------------------------------------------------+-----------
 johndoe        | Superuser, Create role, Create DB, Replication | {}
 resubmissionar | Superuser, Create role, Create DB              | {}
```

## Setting up WildFly
The following steps assume a pristine download of [WildFly]. These steps are just a guideline and the setup may differ.

- Simple setup of WildFly with GC settings for [Java 8].
- Datasource configuration for connecting to [PostgreSQL] using the current [JDBC 4.1 driver](http://jdbc.postgresql.org/download.html).

### Setting up WildFly
1. Configure [Java 8] GC by editing `$WILDFLY_HOME/bin/standalone.conf` (on Windows use `standalone.conf.bat`). Append `-XX:MaxMetaspaceSize=256M` to the variable `$JAVA_OPTS`.
2. Start WildFly using `$WILDFLY_HOME/bin/standalone.sh`. 
3. For the Admin console create an user using `$WILDFLY_HOME/bin/add-user.sh` and follow the on screen instructions.
4. Browse to the Admin console [http://localhost:9990](http://localhost:9990) and login using the specified credentials.

### Setting up the Datasource:
1. Navigate to  */ Runtime / Manage Deployments*.
2. Click *Add* and specify the downloaded JDBC driver (e.g. `postgresql-9.3-1101.jdbc41.jar`), followed by *Next* and *Save*.
3. Enable the the deployment using *En/Disable*.
4. Navigate to */ Configuration /  Subsystems / Connector / Datasources*.
5. Click *Add* to create a new Datasource; use a `Resubmissionar` as name and `java:/jdbc/resubmissionar` for the JNDI name, click *Next*.
6. Select the downloaded PostgreSQL JDBC driver, click *Next*.
7. Enter the Connection Settings (*Connection URL* `jdbc:postgresql://localhost:5432/resubmissionar`, *Username* `postgresql-user`, *Password* `postgresql-password`).
8. Use *Test Connection* to ensure the connection works, click *Done*.
9. Select the Datasource in the list of Datasources.
10. In the bottom select the *Pool* tab, click *Edit* and set the *min pool size* to be ```5``` and *max pool size* to be ```15```.
11. Finally enable the Datasource in the list of Datasources.

# Windows notes
An identifier like `$WILDFLY_HOME` indicates an environment variable, which is written on Windows systems `%WILDFLY_HOME%`. Such a variable may not exist because it is used in this document to indicate an installation root directory (e.g. the WildFly base directory).
When reading paths, replace `/` with `\`. For scripts replace `.sh` with `.bat`.

# License
See `LICENSE.txt` in the repository.

[Java 8]: http://www.oracle.com/technetwork/java/javase/downloads/index.html "Java8"
[WildFly]: http://wildfly.org/downloads "WildFly"
[PostgreSQL]: http://www.postgresql.org "PostgreSQL"

EOF
