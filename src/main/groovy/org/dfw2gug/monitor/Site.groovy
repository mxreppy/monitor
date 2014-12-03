package org.dfw2gug.monitor

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
	
	String toString() {
		"Site[name=$name, status=$status, url=$url]"
	}
}
