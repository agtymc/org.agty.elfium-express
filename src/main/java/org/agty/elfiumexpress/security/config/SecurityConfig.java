package org.agty.elfiumexpress.security.config;

import org.agty.elfiumexpress.security.components.VerifyAccessInterceptor;
import org.agty.elfiumexpress.security.service.UserServiceInterface;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
@EnableWebMvc
public class SecurityConfig implements WebMvcConfigurer {
    private final UserServiceInterface userServiceInterface;
    private final VerifyAccessInterceptor verifyAccessInterceptor;
    private final BCryptPasswordEncoder passwordEncoder;

    public SecurityConfig(UserServiceInterface userServiceInterface,
                          VerifyAccessInterceptor verifyAccessInterceptor,
                          BCryptPasswordEncoder passwordEncoder) {
        this.userServiceInterface = userServiceInterface;
        this.verifyAccessInterceptor = verifyAccessInterceptor;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
        auth.setUserDetailsService(userServiceInterface);
        auth.setPasswordEncoder(passwordEncoder);
        return auth;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(verifyAccessInterceptor).addPathPatterns("/**");
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .authenticationProvider(authenticationProvider())
                .httpBasic(httpBasic -> httpBasic.disable())
                .authorizeHttpRequests(
                        auth -> auth
                                .requestMatchers("/login", "/registration**", "/setup**", "/js/**", "/css/**", "/img/**").permitAll()
                                .requestMatchers("/admin/**").hasRole("ADMIN")
                                .anyRequest().authenticated()

                ).formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true)
                        .permitAll()
                )
                .headers(headers -> headers
                        .frameOptions(frameOptions -> frameOptions.sameOrigin())
                )
                .logout(
                        logout -> logout
                                .logoutUrl("/logout")
                                .invalidateHttpSession(true)
                                .clearAuthentication(true)
                                .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "POST"))
                                .logoutSuccessUrl("/login?logout")
                )
                .build();
    }

    /**
     * При implements WebMvcConfigurer и @EnableWebMvc не работает static содержимое
     * Этот метод возвращает типичное поведение
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler(
                "/static/**",
                        "/images/**",
                        "/css/**",
                        "/js/**",
                        "/img/**",
                        "/modules/express/css/**",
                        "/modules/express/js/**",
                        "/modules/express/img/**"
                )
                .addResourceLocations("classpath:/static/",
                        "classpath:/static/images/",
                        "classpath:/static/js/",
                        "classpath:/static/css/",
                        "classpath:/static/img/",
                        "classpath:/static/modules/express/js/",
                        "classpath:/static/modules/express/css/",
                        "classpath:/static/modules/express/img/"
                )
                .setCachePeriod(3600);
    }
}
