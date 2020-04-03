package com.fateasstring.platform.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fateasstring.platform.model.Hr;
import com.fateasstring.platform.model.RespBean;
import com.fateasstring.platform.service.HrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    HrService hrService;

    /** 加密密码 */
    @Bean
    PasswordEncoder passwordEncoder(){

        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {

        auth.userDetailsService(hrService);
    }



    /** 无论登陆成功还是失败,后端都只需要返回json,告知前端结果,但后端不需要做页面跳转 */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .anyRequest().authenticated()  /** 所有的请求都需要认证 */
                .and()
                .formLogin()  /** 表单登陆 */
                .usernameParameter("username")
                .passwordParameter("password")
                .loginProcessingUrl("/doLogin")  /** 登陆处理 */
                .loginPage("/login")
                .successHandler(new AuthenticationSuccessHandler() { /** 登陆成功的回调 */
                @Override                                                                     /** 登录成功的用户信息保存在authentication*/
                public void onAuthenticationSuccess(HttpServletRequest req, HttpServletResponse resp, Authentication authentication) throws IOException, ServletException {
                    /** 如果登陆成功,则返回 */
                    resp.setContentType("application/json;charset=utf-8"); /** 将内容指定为JSON格式，以UTF-8字符编码进行编码。 */
                    PrintWriter out = resp.getWriter();
                    Hr hr = (Hr)authentication.getPrincipal(); /** 强转为登陆成功的hr对象 */
                    hr.setPassword(null);  /** 防止将密码以json的格式返回 */
                    RespBean ok = RespBean.ok("登陆成功", hr);
                    String s = new ObjectMapper().writeValueAsString(ok); /** 写成字符串 */
                    out.write(s);
                    out.flush();
                    out.close();
                }
                })
                .failureHandler(new AuthenticationFailureHandler() {  /** 登陆失败的回调 */
                @Override
                public void onAuthenticationFailure(HttpServletRequest req, HttpServletResponse resp, AuthenticationException exception) throws IOException, ServletException {
                    /** 如果登陆失败,则返回如下 */
                    resp.setContentType("application/json;charset=utf-8"); /** 将内容指定为JSON格式，以UTF-8字符编码进行编码。 */
                    PrintWriter out = resp.getWriter();

                    RespBean respBean = RespBean.error("登陆失败");

                    /** 判断登陆失败的原因 */
                    if(exception instanceof LockedException){
                        respBean.setMsg("账户被锁定,请联系管理员!");
                    }else if (exception instanceof CredentialsExpiredException){
                        respBean.setMsg("密码过期,请联系管理员！");
                    }else if (exception instanceof AccountExpiredException){
                        respBean.setMsg("账户过期，请联系管理员！");
                    }else if (exception instanceof DisabledException){
                        respBean.setMsg("账户被禁用，请联系管理员！");
                    }else if (exception instanceof BadCredentialsException){
                        respBean.setMsg("用户名或者密码输入错误，请重新输入！");
                    }

                    out.write(new ObjectMapper().writeValueAsString(respBean));
                    out.flush();
                    out.close();
                }
                })
                .permitAll()
                .and()
                .logout()
                .logoutSuccessHandler(new LogoutSuccessHandler() { /** 注销登陆 */
                @Override
                public void onLogoutSuccess(HttpServletRequest req, HttpServletResponse resp, Authentication authentication) throws IOException, ServletException {
                    resp.setContentType("application/json;charset=utf-8");
                    PrintWriter out = resp.getWriter();
                    out.write(new ObjectMapper().writeValueAsString(RespBean.ok("注销成功！")));
                    out.flush();
                    out.close();
                }
                })
                .permitAll()
                .and()
                .csrf().disable();//.exceptionHandling()

                /** 没有认证时，在这里处理结果，不要重定向 */
//                .authenticationEntryPoint(new AuthenticationEntryPoint() {
//                    @Override
//                    public void commence(HttpServletRequest req, HttpServletResponse resp, AuthenticationException authException) throws IOException, ServletException {
//                        resp.setContentType("application/json;charset=utf-8");
//                        resp.setStatus(401);
//                        PrintWriter out = resp.getWriter();
//                        RespBean respBean = RespBean.error("访问失败!");
//                        if (authException instanceof InsufficientAuthenticationException){
//                            respBean.setMsg("请求失败，请联系管理员！");
//                        }
//                        out.write(new ObjectMapper().writeValueAsString(respBean));
//                        out.flush();
//                        out.close();
//                    }
//                });
    }



}