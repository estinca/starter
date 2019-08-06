package com.est.app.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import com.est.app.exception.JsonConfigException;
import com.est.app.utils.JsonConfigUtils;

@Configuration
@ComponentScan(basePackages = { "com.est.app" })
@EntityScan(basePackages = { "com.est.app.entity" })
@EnableJpaRepositories(basePackages = { "com.est.app.dao" })
public class DatabaseConfig {

	private DataSourceConfig config;

	@PostConstruct
	public void init() throws JsonConfigException {
		config = JsonConfigUtils.loadJsonConfig("config/dataSource.json", DataSourceConfig.class);

	}

	@Bean
	@Primary
	public DataSource dataSource() {
		return DataSourceBuilder.create()
				.driverClassName(getDatabaseDriver(config.getDatabaseType()))
				.url(buildConnectionUrl(config))
				.username(config.getUsername())
				.password(config.getPassword())
				.build();
	}

	@Bean
	@Primary
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
		emf.setDataSource(dataSource());
		emf.setPackagesToScan(getEmfPackagesToScan(config));
		emf.setPersistenceUnitName("est_app");
		HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		vendorAdapter.setShowSql(true);
		vendorAdapter.setGenerateDdl(false);

		Properties jpaProperties = new Properties();
		jpaProperties.put("hibernate.dialect", getHibernateDialect(config.getDatabaseType()));
		jpaProperties.put("hibernate.hbm2ddl.auto", "update");

		emf.setJpaVendorAdapter(vendorAdapter);
		emf.setJpaProperties(jpaProperties);
		emf.afterPropertiesSet();

		return emf;
	}

	private String[] getEmfPackagesToScan(DataSourceConfig config) {
		List<String> packagesToScan = new ArrayList<>();
		packagesToScan.add("com.est.app.entity");
		packagesToScan.addAll(Arrays.asList(config.getPackagesToScan()));

		return packagesToScan.toArray(new String[0]);
	}

	@Bean(name = "rawSettingsMap")
	public Map<String, String> rawSettingMap() {
		Map<String, String> settings = new HashMap<>();

		settings.put("connection.driver_class", getDatabaseDriver(config.getDatabaseType()));
		settings.put("dialect", getHibernateDialect(config.getDatabaseType()));
		settings.put("hibernate.connection.url", buildConnectionUrl(config));
		settings.put("hibernate.connection.username", config.getUsername());
		settings.put("hibernate.connection.password", config.getPassword());
		settings.put("show_sql", "true");

		return settings;
	}

	private String buildConnectionUrl(DataSourceConfig config) {
		StringBuilder stringBuilder = new StringBuilder("jdbc:");
		stringBuilder.append(getConnectionUrlType(config.getDatabaseType()))
				.append("://").append(config.getHost())
				.append(":").append(config.getPort())
				.append("/").append(config.getDatabaseName()).append("?useSSL=")
				.append(config.isUseSSL()).append("&serverTimezone=")
				.append(config.getServerTimezone());

		return stringBuilder.toString();
	}

	private String getHibernateDialect(DatabaseType databaseType) {
		switch (databaseType) {
		case POSTGRES:
			return "org.hibernate.dialect.PostgreSQL95Dialect";
		case MYSQL:
		default:
			return "org.hibernate.dialect.MySQL8Dialect";
		}
	}

	private String getDatabaseDriver(DatabaseType databaseType) {
		switch (databaseType) {
		case POSTGRES:
			return "org.postgres.Driver";
		case MYSQL:
		default:
			return "com.mysql.cj.jdbc.Driver";
		}
	}

	private String getConnectionUrlType(DatabaseType databaseType) {
		switch (databaseType) {
		case POSTGRES:
			return "postgres";
		case MYSQL:
		default:
			return "mysql";
		}
	}

}
