package com.tuananhdo.configure;

import com.tuananhdo.security.CustomUserDetailsService;
import com.tuananhdo.security.oauth.OAuth2BlogUserService;
import com.tuananhdo.security.oauth.OAuth2LoginSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class ConfigurationSecurity {

    @Autowired
    private OAuth2BlogUserService oAuth2UserService;
    @Autowired
    private OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

    @Bean
    public UserDetailsService userDetailsService() {
        return new CustomUserDetailsService();
    }

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public DaoAuthenticationProvider authenticationProvider(){
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    public void configure(AuthenticationManagerBuilder builder){
        builder.authenticationProvider(authenticationProvider());
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeHttpRequests((authorize) -> authorize
                        .antMatchers("/resources/**").permitAll()
                        .antMatchers("/register/**").permitAll()
                        .antMatchers("/admin/**").hasAnyAuthority("ROLE_ADMIN")
                        .antMatchers("/").permitAll()
                        .antMatchers("/posts/**").permitAll())
                .formLogin(form -> {
                    try {
                        form.loginPage("/login")
                                .defaultSuccessUrl("/admin/users")
                                .and()
                                .oauth2Login()
                                .loginPage("/login").userInfoEndpoint()
                                .userService(oAuth2UserService)
                                .and()
                                .successHandler(oAuth2LoginSuccessHandler)
                                .and()
                                .rememberMe().tokenValiditySeconds(86400)
                                .and().logout().logoutSuccessUrl("/login?logout")
                                .permitAll();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                })
                .logout(logout -> logout.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                        .permitAll());
        return http.build();

    }
}
