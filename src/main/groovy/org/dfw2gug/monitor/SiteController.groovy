package org.dfw2gug.monitor

import groovy.util.logging.Slf4j

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam

@Slf4j
@Controller
@RequestMapping("/")
class SiteController {

	@Autowired
	SiteService siteService
	
	@Autowired
	MonitorService monitorService
	
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
	
	@RequestMapping(value="check", method=RequestMethod.POST)
	String check() {
		monitorService.check()
		"redirect:/"
	}

}
