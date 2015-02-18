#Spring Boot Tutorial Project - Monitor
We will build a website monitoring project, using Spring Boot and Spring Framework components.

## Setup
1. Eclipse (or your favorite IDE)
2. Gradle 2.3
	
## Create the starter project

0. Browse to the Spring initializer: http://start.spring.io

0. Fill the form with these options:
	* Group: org.javamug
	* Artifact: monitor
	* Name: monitor
	* Description> Monitor application
	* Package Name: org.javamug.monitor
	* Type: Gradle Project
	* Packaging: War
	* Java Version: 1.7
	* Language: Groovy
	* Spring Boot Version: 1.2.1

0. Check the following project dependencies:
	* Thymeleaf
	* JPA
	* Web
	* Actuator

0. Click Generate Project to download the monitor.zip starter file.
0. Create a project folder. I'm using c:\projects3\monitor.
0. Open the monitor.zip file, and save it in your folder.

## Use the latest gradle version

The gradle version is set to 1.12. Lets update it to use gradle 2.3.

0. Edit the build.groovy file
1. Near the bottom of the file, change the line
	* from gradleVersion = '1.12'
	* to gradleVersion = '2.3'

## Do an initial build

0. open a command window, change the folder to your project, and execute these commands
1. gradle wrapper
2. gradle clean
3. gradle build

Notice, that we get a test failure, but the details are not clear.
Lets view the debug information to idenfity the issue. Run the following
command

* gradle test --debug

There is much more output now. If you look through the exceptions, you will
find a line that indicates that Spring is unable to configure the datasource.
We don't have a database set for our project, so lets add the H2 embedded database now.

1. edit build.groovy
2. in the dependencies code block, and the following
	* compile("com.h2database:h2")


Notice that we didn't include the version of H2 we wanted. Spring has a list of components
that it recommends the version numbers. H2 is one of those, and Spring will supply the version. See appendix D in the Spring-boot documentation for the version numbers.

## Integrate with Eclipse

Next, we will use gradle to create the Eclipse files needed to import this project into Eclipse. From the command line, enter the following command

1. gradle eclipse

Now, start Eclipse (or SSTS), and import your project. Let's examine the files that the Spring Initializr created.

### `src/main/groovy/org.javamug/monitor/MonitorApplication.groovy`

This is the "Main" class for our application. There is one annotation attached to the class.

#### @SpringBootApplication

This is a shortcut for the following annotations

##### @Configuration

This tells Spring that this is a Java Configuration class. 

##### @ComponentScan

This tells Spring that when looking for beans, start at this package, and scan it and subpackages for beans.

##### @EnableAutoConfiguration

This is where the magic happens. Spring-Boot determines its configuration from many sources, including the jars on the classpath. Spring will automatically configure the application to use the available jars.

### `src/main/groovy/org.javamug/monitor/ServletInitializer.groovy`

Since we selected WAR files as our packaging, this file is created.
 
### `src/main/resources/application.properties`

This is the file that will hold our application properties.

## Run the Application

We can run the application by running the following command

* gradle bootRun

Next, we browse to the applications home page http://localhost:8080, and we see a Whitelabel error. This is because we haven't told the app what to display on the home page. We need to 
build a controller and an index.html page.


## Create `src/main/groovy/org.javamug/monitor/SiteController.groovy`

create the SiteController.groovy file, and set its contents to this:

	package org.javamug.monitor

	import org.Springframework.stereotype.Controller
	import org.Springframework.web.bind.annotation.RequestMapping

	@Controller
	@RequestMapping("/")
	class SiteController {

		@RequestMapping("")
		String index() {
			"index"
		}
		
	}

now, browse to http://localhost:8080

Notice, we still get an error, but the error message has changed to 'template "index" does not exist.' Spring is looking for the index.html file, but not finding it. So, lets create one now.

## Create `src/main/resources/templates/index.html`

Create the index.html file, and set its contents to this:

	<!doctype html>
	<html>
	<head>
		<title>Monitor</title>
	</head>
	<body>
		Hello, World
	</body>
	</html>

now, browse to http://localhost:8080, and you will see the "Hello World" home page.

## Caching Templates

Make a change to the index.html file. Change the line "Hello World" to "Goodbye World", and refresh your browser. Notice that the text didn't change. By default, SpringBoot caches your templates the first time they are viewed, and doesn't recognize that they have change. We can turn this off by modifying our application.properties file.

1. edit the file: src/main/resources/application.properties
2. add a line
	* spring.thymeleaf.cache=false

Now, you can restart the app and browse to the home page. Any changes you make to the index.html file will show in the browser.


## Building the Domain with Spring Data

We are now ready to build our domain. We are using JPA entity objects (and Spring Data) to access our database. In order to monitor web sites, we need:

1. the name of the site
2. the address of the site
3. the status of the site (OK or ERROR)

### Create: `src/main/groovy/org/javamug/monitor/Site.groovy`

create the Site.groovy file, with the following contents:

	package org.javamug.monitor

	import javax.persistence.*

	@Entity
	class Site {

		@Id
		@GeneratedValue
		Long id
		
		@Column(length=100)
		String name
		
		@Column(length=500)
		String url

		@Column(length=500)
		String status
	}

This file defines our domain object and database schema. Next, we will create a repository to interface between our program and the database.

### Create: `src/main/groovy/org/javamug/monitor/SiteRepository.groovy`

	package org.javamug.monitor

	import org.springframework.data.repository.CrudRepository

	interface SiteRepository extends CrudRepository<Site, Long>{
	}

This interface tells spring data to use Site objects to access the database. CrudRepository defines the method that access the database, such as findOne, save and delete.


## User Interface

Lets build the initial user interface. Our interface will show the list of sites monitored, and thier status. Also, there is a form to add additional sites.

## Edit `src/main/resources/templates/index.html`

change the index.html contents to this:

	<!doctype html>
	<html>
    	<head>
    	    <title>Monitor</title>
    	</head>
    	<body>
    
        	<h2>Monitor</h2>
        	<hr/>
        
        	<h3>Current Status</h3>
        
        	<table>
            	<tr>
            	    <th>Site</th>
            	    <th>Status</th>
            	</tr>
            
            	<tr>
                	<td>Alpha</td>
                	<td>Good</td>
            	</tr>
            
            	<tr>
                	<td>Beta</td>
                	<td>Error</td>
            	</tr>
        
        	</table>
        
        	<hr/>
        
        	<h3>Add New Site</h3>
        
        	<form action="add" method="post">
        		<div style="margin-bottom: 1em;">
            		<label for="name">Site Name</label><br/>
            		<input id="name" name="name"/>
        		</div>
        		
        		<div style="margin-bottom: 1em;">
            		<label for="url">URL</label><br/>
            		<input id="url" name="url"/>
        		</div>
        		
        		<input type="submit" value="add" />
        	</form>
    	</body>
	</html>

The data in this form is hard wired. Lets add some default data to the database, then change this form to access the data from the database.

## Create Test Data

We need to create test data, gather the Site objects in the controller, and pass them to the view.

### Edit `src/main/groovy/org/javamug/monitor/MonitorApplication.groovy`

We will use @PostConstruct to create a method that runs just after Spring configures the beans. We will create three Sites to monitor.

Add the following to Application.groovy

    @Autowired
    SiteRepository siteRepository

	@PostConstruct
	void init() {
		siteRepository.save new Site(name:"Google", url:"http://www.google.com", status:"n/a")
		siteRepository.save new Site(name:"Yahoo", url:"http://www.yahoo.com", status:"n/a")
		siteRepository.save new Site(name:"Bad", url:"www.hopethisdoesntexist.com", status:"n/a")
	}

### Edit `src/main/groovy/org/javamug/monitor/SiteController.groovy`

We need to update the controller to grab the sites and pass them to the view. We get the list of objects from the siteRepository, then pass them to the view using the model.

Add the following to SiteController.groovy

	@Autowired
	SiteRepository siteRepository

	@RequestMapping("")
	String index(final Model model) {
		def sites = siteRepository.findAll()
		model.addAttribute "sites", sites
		"index"
	}

### Edit `src/main/resources/templates/index.html`

Next, we will update the index.html file to show the sites. Edit the index.html file, and Change the table to this:

	<table>
		<tr>
			<th>Site</th>
			<th>Status</th>
		</tr>
		<tr th:each="site : ${sites}">
			<td th:text="${site.name}">Alpha</td>
			<td th:text="${site.status}">Good</td>
		</tr>
	</table>

Restart the app, and refresh the browser. You will now see the sites that were setup in Application.groovy. Next, we will add the ability to add new sites.

## Implement Add New Site

### Edit `src/main/groovy/org/javamug/monitor/SiteController.groovy`

Add the annotation @Slf4j to the class.

    @Slf4j
    @Controller
    @RequestMapping("/")
    class SiteController {
    	
Add this method:

	@RequestMapping(value="/add", method=RequestMethod.POST)
	String add(@RequestParam String name, @RequestParam String url) {
		log.info "add: name=$name, url=$url"
		"redirect:/"
	}

We've added the @Slf4j annotation to the class. This sets-up logging for the class. The annotation adds a log object has the standard methods (.info(), .debug(), ...) 

A few notes about SpringBoot's logging.

* Logback is used by default.
* Logback is configured to support java util logging, commons, log4j and slf4j.
* it creates 10MB rotating output files.

To change the log filename & folder edit application.properties:  
	
	logging.file=c://data//monitor.log

Note: Windows users must use // folder separators.

Log level is INFO by default. To change it, edit application.properties

	logging.level.&lt;package&gt; = DEBUG

## Add a Service to Access the Site

Normally, we don't access the repositories directly from the controller. We use a service in the middle. This allows us to easly manage transactions. Next, we will create the service.

### Create `src/main/groovy/org/javamug/monitor/SiteService.groovy`

Create the SiteService.groovy file, and set its contents to this:

	package org.javamug.monitor

	import org.Springframework.beans.factory.annotation.Autowired
	import org.Springframework.stereotype.Service
	import org.Springframework.transaction.annotation.Transactional

	@Service
	class SiteService {

		@Autowired
		SiteRepository siteRepository
		
		List<Site> list() {
			siteRepository.findAll()    
		}
		
		Site create(String name, String url) {
			siteRepository.save new Site(name:name, url:url)
		}
		
    	void update(Site site) {
    		siteRepository.save site
    	}
	
	}

### Edit `src/main/groovy/org/javamug/monitor/SiteController.groovy`

Update site controller to use the SiteService instead of the SiteRepository

Delete the 2 lines

	@Autowired 
	SiteRepository siteRepository

Add the following autowired bean, and change the index and add methods:

	@Autowired
	SiteService siteService

	@RequestMapping("")
	String index(Model model) {
		def sites = siteService.list()
		model.addAttribute "sites", sites
		"index"
	}

	@RequestMapping(value="add", method=RequestMethod.POST)
	String add(@RequestParam String name, @RequestParam String url) {
		log.info "add: name=$name, url=$url"
		siteService.create name, url
		"redirect:/"
	}

Restart the server, and browse to the home page. You can now add sites to the list.

## Add a Button to Check the Sites Availability

Now, we are ready to start checking the site's availability.

### Edit `src/main/resources/templates/index.html`

Below the table, add the following form:

	<form action="check" method="post">
		<input type="submit" value="check" />
	</form>

### Edit `src/main/groovy/org/javamug/monitor/SiteController.groovy`

Add a method to the controller to handle the check function, and delegate to the monitor service's check method()

Add the monitorService bean.

		@Autowired
		MonitorService monitorService

Add the check method.

		@RequestMapping(value="check", method=RequestMethod.POST)
		String check() {
			monitorService.check()
			"redirect:/"
		}

### Create `src/main/groovy/org/javamug/monitor/MonitorService.groovy`

The monitor service will check the availability of the sites. Create MonitorService.groovy, and set its contents to this:

	package org.javamug.monitor

	import groovy.util.logging.Slf4j
	import org.Springframework.beans.factory.annotation.Autowired
	import org.Springframework.stereotype.Service

	@Slf4j
	@Service
	class MonitorService {

		@Autowired
		SiteService siteService
		
		void check() {
			log.info "checking sites"
			siteService.list().each { Site site ->
				check site
			}
		}
		
		void check(Site site) {
			log.info "checking site $site"
			
			try {
				site.url.toURL().getText(connectTimeout: 5000, readTimeout: 5000)
				site.status = "OK"
				siteService.update site
			} catch(e) {
				site.status = "FAIL " + e.message
				siteService.update site
				log.info e.message, e
			}
		}
		
	}
	
Now, refresh the browser. Clicking the check button will check each of the sites for availability. The table will update with the status of the sites.

## Move Timeout Values to the application.properties file.

Notice, in the MonitorService, the connectionTimeout and readTimeout values are hard wired. Lets move these values to the properties file, and use a @ConfigurationProperties annotation to define a bean to read those properties.

### Edit `src/main/resources/application.properties`

Edit the application properties, and add the following two lines:

    timeout.connect=5000
    timeout.read=5000

### Create `src/main/groovy/org/javamug/monitor/Timeout.groovy`

We create a Timeout.groovy bean to hold the timeout values. Use @ConfigurationProperties to read the values from the spring environment and store them in the bean.

	package org.javamug.monitor

	import org.Springframework.boot.context.properties.ConfigurationProperties
	import org.Springframework.stereotype.Component

	@Component
	@ConfigurationProperties(prefix="timeout")
	class Timeout {
		int connect
		int read
	}

### Modify `src/main/groovy/org/javamug/monitor/MonitorService.groovy`

Now, change the MonitorService.groovy file to use the Timeout bean.

Autowire the timeout bean

	@Autowired
	Timeout timeout

Change the getText line to use the bean

	site.url.toURL().getText(connectTimeout: timeout.connect, readTimeout: timeout.read)

## Run the Check Routine on a Schedule

The monitor should automatically check the sites for availability. We will use Spring's scheduling component to run the check method every minute.

### Modify `src/main/groovy/org/javamug/monitor/MonitorService.groovy`

Update the MonitorService.groovy to enable scheduling and call a method every minute.

Add the @EnableScheduling annotation to the class

Add the following method:

	@Scheduled(cron="0 0/1 * * * ?") // every 1 minute
	void scheduledCheck() {
		log.info "scheduled check ${new Date()}"
		check()
	}

## Change the Database to MySql

We've been using the H2 embeded database. Let's change that to use a MySql database

In MySql
* create a monitor schema.
* create user with name = monitor, and password = monitor.

Modify application.properties, and add the following lines:

	spring.datasource.driverClassName=com.mysql.jdbc.Driver
	spring.datasource.url=jdbc:mysql://localhost:3306/monitor
	spring.datasource.username=monitor
	spring.datasource.password=monitor
	spring.datasource.validation-query=select 1
	spring.datasource.test-on-borrow=true
	spring.jpa.hibernate.ddl-auto=create-drop

Modify build.gradle
* change: compile("com.h2database:h2")
* to: compile("mysql:mysql-connector-java")

## Improve the UI

Let's add bootstrap ui to our application. We will use webjars, because of ease-of-use and SpringBoot support of webjars.

### Edit build.gradle

Add the jquery and bootstrap jars to our project.

1. In the dependencies block, add the following entries
	* compile("org.webjars:jquery:2.1.3")
	* compile("org.webjars:bootstrap:3.3.2-1")

### Edit `src/main/resources/templates/index.html`

Update the index.html file to support bootstrap.

1. In the &lt;head&gt; section, add the following:

		<meta charset="utf-8"/>
		<meta http-equiv="X-UA-Compatible" content="IE=edge"/>
		<meta name="viewport" content="width=device-width, initial-scale=1"/>
		<link rel="stylesheet" href="webjars/bootstrap/3.3.2-1/css/bootstrap.css"/>
		<link rel="stylesheet" href="webjars/bootstrap/3.3.2-1/css/bootstrap-theme.css"/>
		<script src="webjars/jquery/2.1.3/jquery.js"></script>
		<script src="webjars/bootstrap/3.3.2-1/js/bootstrap.js"></script>

2. In the body section, wrap the contents with 

		<div class="container">
		...
		</div>

3. Modify the table, and add the class

		<table class="table table-striped table-bordered table-hover table-condensed">

4. Modify both the buttons, and add the class	

		class="btn btn-success"

5. Modify the forms. add the class to both DIVs:

		<div class="form-group">


Now refresh the application, and you will have a nice-looking bootstrap application.

## Deployment

We will use gradle to create the .war file. From the command line, enter the following command:
1. gradle clean build war

Now, you will have a .war file in the build/libs folder.

## Miscellaneous

### Actuator

The actuator project adds several endpoints to your application. You can see the results by accessing the application. For example, you can see the spring environment by accessing: http://localhost:8080/env

Note, the results are in json. In your browser, you may need to either use a plugin, or view source to see the data. If using chrome, I recommend using the JSONView extension.

Here are the endpoints:

* http://localhost:8080/autoconfig
* http://localhost:8080/beans
* http://localhost:8080/configprops
* http://localhost:8080/dump
* http://localhost:8080/env
* http://localhost:8080/health
* http://localhost:8080/info
* http://localhost:8080/metrics
* http://localhost:8080/mappings
* http://localhost:8080/shutdown
* http://localhost:8080/trace

### Favicon

By default, a green leaf is used as your spring boot app's favorite icon. To change it, place
your icon into the src/main/resource folder, and name it favicon.ico.

### Common properties
Many components needs to have their property values set. This page shows the default values for the components

http://docs.Spring.io/Spring-boot/docs/current/reference/html/common-application-properties.html

### Dependency versions
	
Spring-boot recommends you use certain component versions. To use the correct version, do not include the version number in the build.groovy dependency strings. This page lists the components, and the version that will be used. If the component is not listed on this page, then you must supply the version.

http://docs.Spring.io/Spring-boot/docs/current/reference/html/appendix-dependency-versions.html
