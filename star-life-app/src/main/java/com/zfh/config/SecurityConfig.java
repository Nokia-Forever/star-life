package com.zfh.config;

import com.zfh.constant.URLConstant;
import com.zfh.filter.UserTokenVerifyFilter;
import com.zfh.filter.UsernamePasswordJsonFilter;
import com.zfh.handler.LoginFailHandler;
import com.zfh.handler.LoginSuccessHandler;
import com.zfh.handler.LogoutHandlerImpl;
import com.zfh.property.CaptchaProperties;
import com.zfh.service.IAdminService;
import com.zfh.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * 登录认证配置
 */
@EnableMethodSecurity(prePostEnabled = true)
@Configuration
public class SecurityConfig {
    @Autowired
    private LoginSuccessHandler loginSuccessHandler;
    @Autowired
    private LoginFailHandler loginFailHandler;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private CaptchaProperties captchaProperties;
    @Autowired
    private LogoutHandlerImpl logoutHandler;
    @Autowired
    private UserTokenVerifyFilter userTokenVerifyFilter;
    @Autowired
    private URLConfig urlConfig;
    @Autowired
    @Lazy
    private IUserService userService;
    @Autowired
    @Lazy
    private IAdminService adminService;


    //配置密码编码器
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean("clientAuthenticationManager")
    @Primary
    public AuthenticationManager clientAuthenticationManager() throws Exception {
        ProviderManager providerManager = new ProviderManager(Arrays.asList(clientAuthenticationProvider()));
        providerManager.setEraseCredentialsAfterAuthentication(false);
        return providerManager;
    }

    @Bean("adminAuthenticationManager")
    public AuthenticationManager adminAuthenticationManager() throws Exception {
        ProviderManager providerManager = new ProviderManager(Arrays.asList(adminAuthenticationProvider()));
        providerManager.setEraseCredentialsAfterAuthentication(false);
        return providerManager;
    }



    //注册一个客户端过滤器实例
    @Bean("clientUsernamePasswordJsonFilter")
    public UsernamePasswordJsonFilter clientUsernamePasswordJsonFilter(@Qualifier("clientAuthenticationManager") AuthenticationManager clientAuthenticationManager) throws  Exception{
        UsernamePasswordJsonFilter filter = new UsernamePasswordJsonFilter(stringRedisTemplate,captchaProperties);
        //设置认证管理器
        filter.setAuthenticationManager(clientAuthenticationManager);
        //设置登录请求
        filter.setFilterProcessesUrl(URLConstant.USER_LOGIN_URL);
        //设置认证成功处理器
        filter.setAuthenticationSuccessHandler(loginSuccessHandler);
        //设置认证失败处理器
        filter.setAuthenticationFailureHandler(loginFailHandler);
        return filter;
    }

    //注册一个管理端过滤器实例
    @Bean("adminUsernamePasswordJsonFilter")
    public UsernamePasswordJsonFilter adminUsernamePasswordJsonFilter(@Qualifier("adminAuthenticationManager") AuthenticationManager adminAuthenticationManager) throws  Exception{
        UsernamePasswordJsonFilter filter = new UsernamePasswordJsonFilter(stringRedisTemplate,captchaProperties);
        //设置认证管理器
        filter.setAuthenticationManager(adminAuthenticationManager);
        //设置登录请求
        filter.setFilterProcessesUrl(URLConstant.ADMIN_LOGIN_URL);
        //设置认证成功处理器
        filter.setAuthenticationSuccessHandler(loginSuccessHandler);
        //设置认证失败处理器
        filter.setAuthenticationFailureHandler(loginFailHandler);
        return filter;
    }

    //配置跨域
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // 允许的源（可以指定具体的域名，*表示允许所有源）
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        // 允许的HTTP方法
        configuration.setAllowedMethods(Arrays.asList("*"));
        // 允许的请求头
        configuration.setAllowedHeaders(Arrays.asList("*"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean("clientAuthenticationProvider")
    public DaoAuthenticationProvider clientAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean("adminAuthenticationProvider")
    public DaoAuthenticationProvider adminAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(adminService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    //客户端过滤器
    @Bean("clientFilterChain")
    @Order(1)
    public SecurityFilterChain clientFilterChain(HttpSecurity http, CorsConfigurationSource corsConfigurationSource,
                                                 @Qualifier("clientAuthenticationManager") AuthenticationManager clientAuthenticationManager) throws Exception {
        //合并不需身份验证的路径
        String[] array = Stream.of(urlConfig.CLIENT_WHITE_URL_LIST, urlConfig.COMMON_WHITE_URL_LIST)
                .flatMap(List::stream)
                .toArray(String[]::new);
        return http
                .securityMatcher("/client/**","/common/**")//匹配指定路径
                // 禁用默认的表单登录
                .formLogin(AbstractHttpConfigurer::disable)
                // 禁用HTTP Basic认证
                .httpBasic(AbstractHttpConfigurer::disable)
                //添加用户token校验过滤器
                .addFilterBefore(userTokenVerifyFilter, UsernamePasswordJsonFilter.class)
                // 添加自定义的JSON登录过滤器
                .addFilterAt(clientUsernamePasswordJsonFilter(clientAuthenticationManager), UsernamePasswordAuthenticationFilter.class)
                // 配置HTTP请求的授权规则
                .authorizeHttpRequests(authorizeRequests -> {
                    // 定义不需要身份验证即可访问的路径
                    authorizeRequests
                            .requestMatchers(array).permitAll()
                            .anyRequest().authenticated();
                })
                //配置登出处理
                .logout(logout -> {
                    logout.logoutUrl(URLConstant.USER_LOGOUT_URL)
                            .logoutSuccessHandler(logoutHandler)
                            .clearAuthentication( true);
                })
                //禁止csrf
                .csrf(csrf -> csrf.disable())
                //允许跨域请求
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                //暂停使用session
                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .userDetailsService(userService)
                .build();
    }

    //客户端过滤器
    @Bean("adminFilterChain")
    @Order(2)
    public SecurityFilterChain  adminFilterChain(HttpSecurity http, CorsConfigurationSource corsConfigurationSource,
                                                 @Qualifier("adminAuthenticationManager") AuthenticationManager adminAuthenticationManager) throws Exception {
        //合并不需身份验证的路径
        String[] array = Stream.of( urlConfig.ADMIN_WHITE_URL_LIST, urlConfig.COMMON_WHITE_URL_LIST)
                .flatMap(List::stream)
                .toArray(String[]::new);
        return http
                .securityMatcher("/admin/**","/common/**")//匹配指定路径
                // 禁用默认的表单登录
                .formLogin(AbstractHttpConfigurer::disable)
                // 禁用HTTP Basic认证
                .httpBasic(AbstractHttpConfigurer::disable)
                //添加用户token校验过滤器//TODO 先不加
                //.addFilterBefore(userTokenVerifyFilter, UsernamePasswordJsonFilter.class)
                // 添加自定义的JSON登录过滤器
                .addFilterAt(adminUsernamePasswordJsonFilter(adminAuthenticationManager), UsernamePasswordAuthenticationFilter.class)
                // 配置HTTP请求的授权规则
                .authorizeHttpRequests(authorizeRequests -> {
                    // 定义不需要身份验证即可访问的路径
                    authorizeRequests
                            .requestMatchers(array).permitAll()
                            .anyRequest().authenticated();
                })
                //配置登出处理
                .logout(logout -> {
                    logout.logoutUrl(URLConstant.ADMIN_LOGOUT_URL)
                            .logoutSuccessHandler(logoutHandler)
                            .clearAuthentication( true);
                })
                //禁止csrf
                .csrf(csrf -> csrf.disable())
                //允许跨域请求
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                //暂停使用session
                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .userDetailsService(adminService)
                .build();
    }
}
