package me.scene.dinner.common.security;

import lombok.RequiredArgsConstructor;
import me.scene.dinner.common.security.LoginSuccessHandler;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.web.bind.annotation.GetMapping;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;


@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;
    private final PersistentTokenRepository tokenRepository;

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.authorizeRequests()
                .mvcMatchers(GET,
                        "/", "/about", "/magazines", "/tags",
                        "/signup", "/verify", "/login", "/forgot", "/sent-to-account", "/@*",
                        "/magazines/*", "/topics/*", "/articles/*", "/tags/*",
                        "/api/best-magazines", "/api/magazines/*", "/api/articles/*",
                        "/api/topics/of/*", "/api/articles/of/*"
                ).permitAll()
                .mvcMatchers(POST,
                        "/signup", "/forgot"
                ).permitAll()
                .anyRequest().authenticated()
        ;

        http.formLogin()
                .loginPage("/login")
                .successHandler(new LoginSuccessHandler("/"))
        ;

        http.logout()
                .logoutSuccessUrl("/")
        ;

        http.rememberMe()
                .userDetailsService(userDetailsService)
                .tokenRepository(tokenRepository)
        ;

    }

    @Override
    public void configure(WebSecurity web) {

        web.ignoring()
                .mvcMatchers("/node_modules/**")
                .mvcMatchers("/fonts/**")
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations())
        ;

    }

}