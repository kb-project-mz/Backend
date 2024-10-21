package fingertips.backend.security.config;

import fingertips.backend.security.account.filter.AuthenticationErrorFilter;
import fingertips.backend.security.account.filter.JwtAuthenticationFilter;
import fingertips.backend.security.account.filter.JwtUsernamePasswordAuthenticationFilter;
import fingertips.backend.security.handle.CustomAccessDeniedHandler;
import fingertips.backend.security.handle.CustomAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
@Log4j
@MapperScan(basePackages = {"fingertips.backend.member.mapper"})
@ComponentScan(basePackages = {"fingertips.backend.security"})
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private JwtUsernamePasswordAuthenticationFilter jwtUsernamePasswordAuthenticationFilter;

    private final AuthenticationErrorFilter authenticationErrorFilter;

    private final CustomAccessDeniedHandler accessDeniedHandler;

    private final CustomAuthenticationEntryPoint authenticationEntryPoint;

    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOriginPattern("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

    public CharacterEncodingFilter encodingFilter() {
        CharacterEncodingFilter encodingFilter = new CharacterEncodingFilter();
        encodingFilter.setEncoding("UTF-8");
        encodingFilter.setForceEncoding(true);
        return encodingFilter;
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/assets/**", "/*", "/api/member/**");
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .cors()
                .and()
                .addFilterBefore(encodingFilter(), CsrfFilter.class)
                .addFilterBefore(authenticationErrorFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtUsernamePasswordAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        http
                .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint)
                .accessDeniedHandler(accessDeniedHandler);

        http
                .cors()
                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.OPTIONS).permitAll()
                .antMatchers("/admin/**").hasRole("ADMIN")
                .antMatchers("/memberHomePage").authenticated()
                .antMatchers(HttpMethod.POST,"/api/v1/test/**").permitAll()
                .antMatchers(HttpMethod.POST, "/api/v1/member/join").permitAll()
                .antMatchers(HttpMethod.POST, "/api/v1/member/login").permitAll()
                .antMatchers(HttpMethod.POST, "/api/v1/member/email/**").permitAll()
                .antMatchers(HttpMethod.POST, "/api/v1/member/password/**").permitAll()
                .antMatchers(HttpMethod.GET,"/api/v1/test/**").permitAll()
                .antMatchers(HttpMethod.GET, "/api/v1/member/email/**").permitAll()
                .antMatchers(HttpMethod.GET, "/api/v1/member/memberName/**").permitAll()
                .antMatchers(HttpMethod.GET, "/api/v1/member/check-memberId/**").permitAll()
                .antMatchers(HttpMethod.GET, "/api/v1/member/memberId/**").permitAll()
                .antMatchers(HttpMethod.GET, "/api/v1/member/memberId/**/**").permitAll()
                .antMatchers(HttpMethod.GET, "/api/v1/member/memberId/{memberName}/{email}").permitAll()
                .antMatchers(HttpMethod.GET, "/api/v1/member/password/**").permitAll()
                .antMatchers(HttpMethod.GET, "/api/v1/member/password/**/**").permitAll()
                .antMatchers(HttpMethod.GET, "/api/v1/member/password/{memberName}/{email}").permitAll()
                .antMatchers(HttpMethod.GET, "/api/v1/member/login/google/**").permitAll()
                .antMatchers(HttpMethod.POST, "/api/v1/member/logout").authenticated()
                .antMatchers(HttpMethod.POST, "/api/v1/member/refresh").authenticated()
                .antMatchers(HttpMethod.POST, "/api/v1/member/withdraw").authenticated()
                .antMatchers("/api/v1/asset/**").authenticated()
                .antMatchers("/api/v1/challenge/**").authenticated()
                .antMatchers("/api/v1/consumption/**").authenticated()
                .anyRequest().permitAll();

        http.httpBasic().disable()
                .csrf().disable()
                .formLogin().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }
}
