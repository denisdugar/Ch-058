/*
 * The following code have been created by Yaroslav Zhyravov (shrralis).
 * The code can be used in non-commercial way for everyone.
 * But for any commercial way it needs a author's agreement.
 * Please contact the author for that:
 *  - https://t.me/Shrralis
 *  - https://twitter.com/Shrralis
 *  - shrralis@gmail.com
 *
 * Copyright (c) 2017 by shrralis (Yaroslav Zhyravov).
 */

package com.shrralis.ssdemo1.configuration;

import com.shrralis.ssdemo1.security.AuthProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author shrralis (https://t.me/Shrralis)
 * @version 1.0
 */

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	private AuthenticationSuccessHandler authenticationSuccessHandler;

	@Autowired
	protected void configureGlobalSecurity(AuthenticationManagerBuilder auth) {
		auth.authenticationProvider(authProvider());
		auth.eraseCredentials(true);
	}

	@Bean
	public AuthenticationProvider authProvider() {
		AuthProvider authenticationProvider = new AuthProvider();

		authenticationProvider.setUserDetailsService(userDetailsService);
		authenticationProvider.setPasswordEncoder(passwordEncoder());
		return authenticationProvider;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**").allowedOrigins("http://localhost:8081");
			}
		};
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
				.authorizeRequests()
//					next line should allow passing all unauthorized requests
				.antMatchers("/**").permitAll()
//					.antMatchers("/", "/auth/login", "/signUp").permitAll()
//					.antMatchers("/auth/logout").authenticated()
//					.antMatchers("/admin**/**").access("hasRole('ADMIN')")
//					.antMatchers("/leader**/**").access("hasRole('LEADER')")
//					.antMatchers("/user**/**").access("hasRole('LEADER') or hasRole('USER')")
//					.antMatchers("/askhelp").authenticated()
				.and()
				.formLogin()
				.loginPage("/auth/login")
				.usernameParameter("login")
				.passwordParameter("password")
				.loginProcessingUrl("/auth/login")
				.successHandler(authenticationSuccessHandler)
				.failureUrl("/auth/login?error=true")
				.and()
				.logout()
				.invalidateHttpSession(true)
				.logoutSuccessUrl("/logout")
				.deleteCookies("JSESSIONID", "XSRF-TOKEN", "locale-cookie")
				.and()
				.exceptionHandling()
				.accessDeniedPage("/accessDenied")
				.and()
//					.addFilterAfter(csrfHeaderFilter(), CsrfFilter.class)
				.csrf()
				.disable();
//				.csrfTokenRepository(csrfTokenRepository())
//				.requireCsrfProtectionMatcher(new AndRequestMatcher(new NegatedRequestMatcher(
//						new AntPathRequestMatcher("/auth/login")), AnyRequestMatcher.INSTANCE));
	}

	/*private Filter csrfHeaderFilter() {
		return new OncePerRequestFilter() {
			@Override
			protected void doFilterInternal(
					HttpServletRequest request,
					HttpServletResponse response,
					FilterChain filterChain
			) throws ServletException, IOException {
				CsrfToken csrf = (CsrfToken) request.getAttribute(CsrfToken.class.getName());

				if (csrf != null) {
					Cookie cookie = WebUtils.getCookie(request, "XSRF-TOKEN");
					String token = csrf.getToken();

					if (cookie == null || token != null && !token.equals(cookie.getValue())) {
						cookie = new Cookie("XSRF-TOKEN", token);

						cookie.setPath("/");
						response.addCookie(cookie);
					}
				}
				filterChain.doFilter(request, response);
			}
		};
	}

	private CsrfTokenRepository csrfTokenRepository() {
		HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();

		repository.setHeaderName("X-XSRF-TOKEN");
		return repository;
	}*/

	@Bean
	public AuthenticationTrustResolver getAuthenticationTrustResolver() {
		return new AuthenticationTrustResolverImpl();
	}
}
