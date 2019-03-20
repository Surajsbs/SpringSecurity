package com.bitwise.authorization.configuration;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.bitwise.authorization.exception.handler.RestAccessDeniedHandler;
import com.bitwise.authorization.exception.handler.RestAuthenticationEntryPoint;
import com.bitwise.authorization.filter.AuthFilter;
import com.bitwise.authorization.provider.AuthJdbcProvider;

/**
 * @author surajs
 *
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private AuthJdbcProvider jdbcAuthProvider;

	@Autowired
	private RestAuthenticationEntryPoint unauthorizedHandler;

	@Autowired
	private RestAccessDeniedHandler accessDeniedHandler;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.anonymous().disable().csrf().disable().sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		http.cors().configurationSource(corsConfigSource());

		http.authorizeRequests().antMatchers(HttpMethod.GET, "/data/user/getAll")
				.access("hasRole('ROLE_USER') OR hasRole('ROLE_BA') OR hasRole('ROLE_QA')")
				.antMatchers(HttpMethod.GET, "/data/user/filter").access("hasRole('ROLE_QA')")
				.antMatchers(HttpMethod.PUT, "/data/user/update/**", "/data/user/delete/**")
				.access("hasRole('ROLE_ADMIN')").antMatchers(HttpMethod.POST, "/data/user/create")
				.access("hasRole('ROLE_BA') OR hasRole('ROLE_ADMIN')").and().exceptionHandling()
				.accessDeniedHandler(accessDeniedHandler).authenticationEntryPoint(unauthorizedHandler).and()
				.addFilterBefore(new AuthFilter(authenticationManager()), BasicAuthenticationFilter.class);

		http.authorizeRequests().antMatchers("/data/user/invalidate/**", "/data/user/get/**").authenticated().and()
				.addFilterBefore(new AuthFilter(authenticationManager()), BasicAuthenticationFilter.class);
	}

	@Bean
	public CorsConfigurationSource corsConfigSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(Arrays.asList("*"));
		configuration.setAllowedMethods(Arrays.asList("*"));
		configuration.setAllowCredentials(true);
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}

	@Override
	@Bean
	public AuthenticationManager authenticationManager() throws Exception {
		return new ProviderManager(Arrays.asList(jdbcAuthProvider));
	}
}