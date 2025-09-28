package com.zfh.config;

import com.zfh.constant.URLConstant;
import com.zfh.filter.UserTokenVerifyFilter;
import com.zfh.filter.UsernamePasswordJsonFilter;
import com.zfh.handler.LoginFailHandler;
import com.zfh.handler.LoginSuccessHandler;
import com.zfh.handler.LogoutHandlerImpl;
import com.zfh.property.CaptchaProperties;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
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

    @PostConstruct
    public void init() {
        // 关键配置：启用线程继承
        SecurityContextHolder.setStrategyName(
                SecurityContextHolder.MODE_INHERITABLETHREADLOCAL
        );
    }

    //配置密码编码器
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    // 获取AuthenticationManager
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    //注册一个过滤器实例
    @Bean
    public UsernamePasswordJsonFilter usernamePasswordJsonFilter( AuthenticationManager authenticationManager) throws  Exception{
        UsernamePasswordJsonFilter filter = new UsernamePasswordJsonFilter(stringRedisTemplate,captchaProperties);
        //设置认证管理器
        filter.setAuthenticationManager(authenticationManager);
        //设置登录请求
        filter.setFilterProcessesUrl(URLConstant.USER_LOGIN_URL);
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



    //配置安全拦截机制
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, CorsConfigurationSource corsConfigurationSource, AuthenticationManager authenticationManager) throws Exception {
        //合并不需身份验证的路径
        String[] array = Stream.of(urlConfig.CLIENT_WHITE_URL_LIST, urlConfig.ADMIN_WHITE_URL_LIST, urlConfig.COMMON_WHITE_URL_LIST)
                .flatMap(List::stream)
                .toArray(String[]::new);
        return http
                // 禁用默认的表单登录
                .formLogin(AbstractHttpConfigurer::disable)
                // 禁用HTTP Basic认证
                .httpBasic(AbstractHttpConfigurer::disable)
                //添加用户token校验过滤器
                .addFilterBefore(userTokenVerifyFilter, UsernamePasswordJsonFilter.class)
                // 添加自定义的JSON登录过滤器
                .addFilterAt(usernamePasswordJsonFilter(authenticationManager), UsernamePasswordAuthenticationFilter.class)
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
                .build();
    }
}
