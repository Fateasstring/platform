package com.fateasstring.platform.controller;

import com.fateasstring.platform.config.VerificationCode;
import com.fateasstring.platform.model.RespBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;

@RestController
public class LoginController {

    @GetMapping("/login")
    public RespBean login(){

        return RespBean.error("尚未登录，请登录！");
    }

//    @GetMapping("/verifyCode")
//    public void verifyCode(HttpSession session, HttpServletResponse resp) throws IOException{
//        VerificationCode code = new VerificationCode();
//        BufferedImage image = code.getI
//    }

}
