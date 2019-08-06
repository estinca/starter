package com.est.app.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.web.filter.CorsFilter;

import com.est.app.entity.Role;


@Configuration
@EnableAuthorizationServer
//@PropertySource("classpath:resources/application.properties")
public class AuthorizationConfig extends AuthorizationServerConfigurerAdapter  {
	
	@Value("${est.oauth2.resourceId}")
	private String resourceId;
	
	@Value("${est.oauth2.secret}")
	private String secret;
	
	@Value("${est.oauth2.signingKey}")
	private String signingKey;
	
	private final AuthenticationManager authenticationManager;
	private final CorsFilter corsFilter;
	private final PasswordEncoder passwordEncoder;
	
	
	@Autowired
	public AuthorizationConfig(AuthenticationManager authenticationManager, CorsFilter corsFilter,
			PasswordEncoder passwordEncoder) {
		this.authenticationManager = authenticationManager;
		this.corsFilter = corsFilter;
		this.passwordEncoder = passwordEncoder;
	}
	
	
	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
		clients.inMemory()
				.withClient("app")
				.authorizedGrantTypes("password", "refresh_token")
				.authorities(Role.USER.toString())
				.scopes("read","write")
				.resourceIds(resourceId)
				.accessTokenValiditySeconds(3600)
				.secret(passwordEncoder.encode(secret))
				
				.and()
				
				.withClient("public")
				.authorizedGrantTypes("clients_credentials")
				.authorities(Role.PUBLIC.toString())
				.scopes("read")
				.resourceIds(resourceId)
				.accessTokenValiditySeconds(3600)
				.secret(passwordEncoder.encode(secret));
	}


	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		endpoints.authenticationManager(authenticationManager)
		.tokenServices(tokenServices())
		.tokenStore(tokenStore())
		.accessTokenConverter(accessTokenConverter());
	}
	

	@Override
	public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
		security.passwordEncoder(passwordEncoder)
		.tokenKeyAccess("hasAuthority('ROLE_USER')")
		.checkTokenAccess("hasAuthority('ROLE_USER')")
		.addTokenEndpointAuthenticationFilter(corsFilter);
	}
	  

	@Bean
	public TokenStore tokenStore() {
		return new JwtTokenStore(accessTokenConverter());
	}
	
	@Bean
	public JwtAccessTokenConverter accessTokenConverter() {
		JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
		converter.setSigningKey(signingKey);
		return converter;
	}
	
	@Bean
	public DefaultTokenServices tokenServices() {
		DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
		defaultTokenServices.setTokenStore(tokenStore());
		defaultTokenServices.setSupportRefreshToken(true);
		defaultTokenServices.setTokenEnhancer(accessTokenConverter());
		defaultTokenServices.setAccessTokenValiditySeconds(3600);
		
		return defaultTokenServices;
	}
	

	
}
