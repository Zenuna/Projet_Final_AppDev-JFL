package cgg.informatique.jfl.webSocket.configurations;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecuriteWebConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private MonUserDetailsService monUserDetailsService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //permettre toutes les requêtes
        http.authorizeRequests()
                .antMatchers("/").permitAll()
                .antMatchers("/login/**","/login").permitAll()
                .antMatchers("/kumite/**", "/kumite/").authenticated()
                .antMatchers("/gradation/**", "/gradation/").hasAnyAuthority("VÉNÉRABLE","SENSEI")
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
                .and()
                //Activer le formulaire pour login
                .formLogin()
                .loginPage("/login")
                .successHandler(new MySavedRequestAwareAuthenticationSuccessHandler())
                .failureHandler(new SimpleUrlAuthenticationFailureHandler())
                .loginProcessingUrl("/login")
                .permitAll()
                .failureUrl("/login?error=true")
                .and()
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login")
                //pour la console H2
                .and()
                .csrf()
                .ignoringAntMatchers("/consoleBD/**")
                .disable()
                .exceptionHandling()
                .authenticationEntryPoint(new RestAuthenticationEntryPoint())
                .and()
                .headers()
                .frameOptions()
                .sameOrigin();


    }

    //Initialiser la méthode pour s’identifier
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider());
    }
    //Définir les méthodes qui permettront l’identification monUserDetailsService et le cryptage des mots de passe.
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(monUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
    //Définir la méthode de cryptage des mots de passe
    //https://en.wikipedia.org/wiki/Bcrypt
    //La valeur par defaut est 10
    //https://docs.spring.io/spring-security/site/docs/4.2.7.RELEASE/apidocs/org/springframework/security/crypto/bcrypt/BCryptPasswordEncoder.html
    //https://www.browserling.com/tools/bcrypt
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    };
}