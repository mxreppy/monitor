package org.dfw2gug.monitor

import javax.annotation.PostConstruct

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@Configuration
@ComponentScan
@EnableAutoConfiguration
class Application {

    static void main(String[] args) {
        SpringApplication.run Application, args
    }
	
	@Autowired
	SiteRepository siteRepository
	
	@PostConstruct
	void init() {
		siteRepository.save new Site(name:"Google", url:"http://www.google.com", status:"n/a")
		siteRepository.save new Site(name:"Yahoo", url:"http://www.yahoo.com", status:"n/a")
		siteRepository.save new Site(name:"Bad", url:"www.hopethisdoesntexist.com", status:"n/a")
	}
	
}
