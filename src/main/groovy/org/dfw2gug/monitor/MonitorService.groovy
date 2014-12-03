package org.dfw2gug.monitor

import groovy.util.logging.Slf4j

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service

@Slf4j
@Service
@EnableScheduling
class MonitorService {

	@Autowired
	SiteService siteService
	
	@Autowired
	Timeout timeout
	
	@Scheduled(cron="0 0/1 * * * ?") // every 1 minutes
	void scheduledCheck() {
		log.info "scheduled check ${new Date()}"
		check()
	}

	void check() {
		log.info "checking sites"
		siteService.list().each { Site site ->
			check site
		}
	}
	
	void check(Site site) {
		log.info "checking site $site"
		
		try {
			site.url.toURL().getText(connectTimeout: timeout.connect, readTimeout: timeout.read)
			site.status = "OK"
			siteService.update site
		} catch(e) {
			site.status = "FAIL " + e.message
			siteService.update site
			log.info e.message, e
		}
	}

}

