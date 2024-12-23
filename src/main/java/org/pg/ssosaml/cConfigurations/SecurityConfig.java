package org.pg.ssosaml.cConfigurations;

import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.csrf().disable()
                .authorizeRequests()
                .antMatchers("/saml2/**","/saml2/authenticate","/error").permitAll()
                .anyRequest().authenticated()
                .and()
                .saml2Login(withDefaults());
//        http
//                .authorizeRequests()
//                .antMatchers("/admin/**").hasRole("admin")
//                .antMatchers("/user/**").hasRole("user")
//                .and()
//                .httpBasic();
    }
}

//http://localhost:8085/saml2/authenticate/idptwo