package org.sumdonkus.postJob.configurations;

import java.io.IOException;
import java.util.Properties;

import javax.sql.DataSource;

import liquibase.integration.spring.SpringLiquibase;

import org.quartz.spi.JobFactory;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.sumdonkus.postJob.configurations.quartz.AutowiringSpringBeanJobFactory;

@Configuration
public class QuartzConfiguration {
	private final static String QUARTZ_PROPERTY_FILE_PATH = "/quartz.properties";
	
	@Bean
	// injecting SpringLiquibase to ensure liquibase is already initialized and created the quartz tables:
	public JobFactory jobFactory(ApplicationContext applicationContext, SpringLiquibase springLiquibase){
		AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
		jobFactory.setApplicationContext(applicationContext);
		return jobFactory;
	}
	
	@Bean
	public SchedulerFactoryBean schedulerFactoryBean(DataSource dataSource, JobFactory jobFactory) throws IOException {
		SchedulerFactoryBean factory = new SchedulerFactoryBean();
		// this allows to update triggers in DB when updating settings in config file:
		factory.setOverwriteExistingJobs(true);
		factory.setDataSource(dataSource);
		factory.setJobFactory(jobFactory);
		
		factory.setQuartzProperties(quartzProperties());
		
		return factory;
	}
	
	@Bean
	public Properties quartzProperties() throws IOException {
		PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
		propertiesFactoryBean.setLocation(new ClassPathResource(QUARTZ_PROPERTY_FILE_PATH));
		propertiesFactoryBean.afterPropertiesSet();
		return propertiesFactoryBean.getObject();
	}
}
