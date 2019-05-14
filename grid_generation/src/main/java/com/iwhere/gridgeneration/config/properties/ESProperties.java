package com.iwhere.gridgeneration.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;



@Configuration
@ConfigurationProperties(prefix = "escluster.transport")
public class ESProperties {
	private static String name;
	private static String ip;
	private static String port;
	public ESProperties() {
		super();
		// TODO Auto-generated constructor stub
	}
	public static String getName() {
		return name;
	}
	public static void setName(String name) {
		ESProperties.name = name;
	}
	public static String getIp() {
		return ip;
	}
	public static void setIp(String ip) {
		ESProperties.ip = ip;
	}
	public static String getPort() {
		return port;
	}
	public static void setPort(String port) {
		ESProperties.port = port;
	}
	
}
