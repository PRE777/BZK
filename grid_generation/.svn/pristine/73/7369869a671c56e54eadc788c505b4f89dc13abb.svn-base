package com.iwhere.gridgeneration;

import javax.servlet.MultipartConfigElement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import com.iwhere.gridgeneration.discretization.utils.DiscretizationProperties;


@SpringBootApplication()
@EnableAutoConfiguration  //开启自动配置
@ComponentScan(basePackages={"com.iwhere.geosot","com.iwhere.gridgeneration"}) 
public class Application {
	
	@Autowired 
	private DiscretizationProperties discretizationProperties;
	
    /**
     * Main Start
     */
    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }
    
    @Bean
    MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        // 设置一个临时目录
        factory.setLocation(discretizationProperties.getTmp());
        return factory.createMultipartConfig();
    }
}
