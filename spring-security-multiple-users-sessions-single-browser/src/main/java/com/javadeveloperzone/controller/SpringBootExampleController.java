package com.javadeveloperzone.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.User;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;
import org.springframework.session.web.http.HttpSessionManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.javadeveloperzone.service.LoginService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.security.Principal;


/**
 * Created by Java Developer Zone on 19-07-2017.
 */
@Controller
public class SpringBootExampleController {
	
	@Autowired
	private LoginService loginService;

    @RequestMapping(value = "/login")
    @ResponseBody
    public String login(HttpServletRequest httpRequest, HttpServletResponse httpResponse, ModelMap map) throws Exception {

    	loginService.doFilter(httpRequest, httpResponse);
    	
        return "login";
    }

    @RequestMapping(value = "loginSuccess")
    public String loginSuccess(HttpServletRequest request,HttpSession session,Principal pricipal,ModelMap modelMap){
       Integer integer =(Integer) session.getAttribute("hitCounter");
       if(integer==null){
           integer=new Integer(0);
           integer++;
           request.getSession().setAttribute("hitCounter",integer);
       }else{
           integer++;
           request.getSession().setAttribute("hitCounter",integer);
       }
        modelMap.put("currentLoginUser",pricipal.getName());
        return "welcome";
    }

    @RequestMapping(value = "loginFailed")
    public String loginFailed(){
        return "loginFailed";
    }


}
