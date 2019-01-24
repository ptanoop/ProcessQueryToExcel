package com.report.configuration;

import org.apache.catalina.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.embedded.tomcat.TomcatWebServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;


@Profile(value="test")
@Configuration
public class EmbeddedTomcatConfiguration {
	@SuppressWarnings("unused")
	private final static Logger logger = LoggerFactory.getLogger(EmbeddedTomcatConfiguration.class);
	
	@Bean
	public TomcatServletWebServerFactory tomcatFactory() {
	    return new TomcatServletWebServerFactory() {
	        @Override
	        protected TomcatWebServer getTomcatWebServer(org.apache.catalina.startup.Tomcat tomcat) {
	        	logger.info("For running from embedded tomcat");
	            tomcat.enableNaming(); 
	            return super.getTomcatWebServer(tomcat);
	        }

	        @Override 
	        protected void postProcessContext(Context context) {
	        	
	       
	        }
	    };
	}
	

}
