package org.dfw2gug.monitor

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix="timeout")
class Timeout {
	int connect
	int read
}
