package org.dfw2gug.monitor

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

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
