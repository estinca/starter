package com.est.app.config;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.web.util.matcher.RequestMatcher;


@Configuration
@EnableResourceServer
public class ResourceConfig extends ResourceServerConfigurerAdapter {

	@Value("${est.oauth2.resourceId}")
	private String resourceId;

	private final DefaultTokenServices tokenServices;
	private final TokenStore tokenStore;

	@Autowired
	public ResourceConfig(DefaultTokenServices tokenServices, TokenStore tokenStore) {
		this.tokenServices = tokenServices;
		this.tokenStore = tokenStore;
	}

	@Override
	public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
		resources.resourceId(resourceId)
				.tokenServices(tokenServices)
				.tokenStore(tokenStore);
	}

	@Override
	public void configure(HttpSecurity http) throws Exception {
		http.requestMatcher(new OAuthRequestMatcher())
				.formLogin().disable()
				.anonymous().disable()
				.csrf().disable()
				.cors()
				.and().authorizeRequests()
				.antMatchers(HttpMethod.OPTIONS).permitAll()
				.antMatchers(UriAccessConfig.PUBLIC_MEDIA_ROUTES).permitAll()

//				.antMatchers(HttpMethod.GET, UriAccessConfig.ADMIN_ROUTES_GET).hasRole(Role.ADMIN.toString())
//				.antMatchers(HttpMethod.POST, UriAccessConfig.ADMIN_ROUTES_POST).hasRole(Role.ADMIN.toString())
//				.antMatchers(HttpMethod.PUT, UriAccessConfig.ADMIN_ROUTES_PUT).hasRole(Role.ADMIN.toString())
//				.antMatchers(HttpMethod.DELETE, UriAccessConfig.ADMIN_ROUTES_DELETE).hasRole(Role.ADMIN.toString())
//
//				.antMatchers(HttpMethod.GET, UriAccessConfig.USER_ROUTES_GET).hasRole(Role.USER.toString())
//				.antMatchers(HttpMethod.POST, UriAccessConfig.USER_ROUTES_POST).hasRole(Role.USER.toString())
//				.antMatchers(HttpMethod.PUT, UriAccessConfig.USER_ROUTES_PUT).hasRole(Role.USER.toString())
//				.antMatchers(HttpMethod.DELETE, UriAccessConfig.USER_ROUTES_DELETE)
//				.hasRole(Role.USER.toString())
//
//				.antMatchers(HttpMethod.GET, UriAccessConfig.PUBLIC_ROUTES_GET).hasRole(Role.PUBLIC.toString())
//				.antMatchers(HttpMethod.POST, UriAccessConfig.PUBLIC_ROUTES_POST).hasRole(Role.PUBLIC.toString())
//				.antMatchers(HttpMethod.PUT, UriAccessConfig.PUBLIC_ROUTES_PUT).hasRole(Role.PUBLIC.toString())
//				.antMatchers(HttpMethod.DELETE, UriAccessConfig.PUBLIC_ROUTES_DELETE)
//				.hasRole(Role.PUBLIC.toString())

				.antMatchers("/**").authenticated();

	}

	public static class OAuthRequestMatcher implements RequestMatcher {

		@Override
		public boolean matches(HttpServletRequest request) {
			return true;
		}

	}

}
