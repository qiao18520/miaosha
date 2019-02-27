package com.miaoshaproject.controller;

import com.alibaba.druid.util.StringUtils;
import com.miaoshaproject.controller.viewobject.UserVo;
import com.miaoshaproject.erro.BusinessException;
import com.miaoshaproject.erro.EmBusinessError;
import com.miaoshaproject.response.CommonReturnType;
import com.miaoshaproject.service.UserService;
import com.miaoshaproject.service.model.UserModel;

import sun.misc.BASE64Encoder;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import org.apache.tomcat.util.security.MD5Encoder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller("user")
@RequestMapping("/user")
@CrossOrigin(allowCredentials="true",allowedHeaders="*")
//CrossOrigin可以任何的跨域请求,但是做不了session共享--
//allowedHeaders.DEFAULT_ALLOWED_HEADERS:允许跨域传输所有的headers参数,将用于使用token放入herder域做session共享的跨域请求
public class UserController extends BaseController{

    @Autowired
    private UserService userService;
    
    @Autowired
    private HttpServletRequest httpServletRequest;

    //登录接口
    @RequestMapping(value="/login",method= {RequestMethod.POST},consumes= {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType login(@RequestParam(name="telphone")String telphone,
    							@RequestParam(name="password")String password) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {
    	//入参校验
    	if(org.apache.commons.lang3.StringUtils.isEmpty(telphone)||StringUtils.isEmpty(password)) {
    		throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
    	}
    	
    	//用户登录服务器,用来校验用户登录是否合法
    	UserModel userModel = userService.validateLogin(telphone, this.EncodeByMd5(password));
    	
    	//将登陆凭证加入到用户登录成功的session内
    	this.httpServletRequest.getSession().setAttribute("IS_LOGIN",true);
    	this.httpServletRequest.getSession().setAttribute("LOGIN_USER", userModel);
    	
    	return CommonReturnType.create(null);
    	
    }
    
    //注册接口
    @RequestMapping(value="/register",method= {RequestMethod.POST},consumes= {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType register(@RequestParam(name="telphone")String telphone,
    								@RequestParam(name="otpCode")String otpCode,
    								@RequestParam(name="name")String name,
    								@RequestParam(name="gender")Byte gender,
    								@RequestParam(name="age")Integer age,
    								@RequestParam(name="password")String password) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {
    	//验证手机号和对应的otpcode相符合
    	String inSessionOtpCode = (String) this.httpServletRequest.getSession().getAttribute(telphone);
    	if(!StringUtils.equals(otpCode, inSessionOtpCode)) {
    		throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,"短信验证码不符合");
    	}
    	//用户的注册流程
    	UserModel userModel = new UserModel();
    	userModel.setName(name);
    	userModel.setGender(gender);
    	userModel.setAge(age);
    	userModel.setTelphone(telphone);
    	userModel.setRegisterMode("byphone");
    	userModel.setEncrptPassword(this.EncodeByMd5(password));
    	
    	userService.register(userModel);
		return CommonReturnType.create(null);
    	
    }
    
    public String EncodeByMd5(String str) throws UnsupportedEncodingException, NoSuchAlgorithmException {
    	//确认计算方法
    	MessageDigest md5 = MessageDigest.getInstance("MD5");
    	BASE64Encoder base64en = new BASE64Encoder();
    	//加密字符串
    	String newstr = base64en.encode(md5.digest(str.getBytes("utf-8")));
    	return newstr;
    	
    }
    
    //用户获取otp短信接口
    @RequestMapping(value="/getotp",method= {RequestMethod.POST},consumes= {CONTENT_TYPE_FORMED})
    @ResponseBody
    @CrossOrigin
    public CommonReturnType getOtp(@RequestParam(name="telphone")String telphone) {
    	//需要按照一定的规则生成OTP验证码
    	Random random = new Random();
    	int randomInt = random.nextInt(99999);
    	randomInt += 10000;
    	String otpCode = String.valueOf(randomInt);
    	
    	//将OTP验证码同对应用户的手机号关联,使用httpsession的方式绑定他的手机号与OTPCODE
    	httpServletRequest.getSession().setAttribute(telphone, otpCode);
    	
    	//将OTP验证码通过短信通道发送给用户，省略
    	System.err.println(telphone+"+"+otpCode);
//    	UserVo userVo = new UserVo();
//    	userVo.setOtpCode(otpCode);
    	return CommonReturnType.create(otpCode);
    }
    
    
    @RequestMapping("/get")
    @ResponseBody
    public CommonReturnType getUser(@RequestParam(name="id") Integer id) throws BusinessException{
        //调用Service服务获取对应id的用户对象并返回给前端
        UserModel userModel = userService.getUserById(id);
        
        //若获取的对应用户信息不存在
        if(userModel == null) {
        	throw new BusinessException(EmBusinessError.USER_NOT_EXIST);
        }
        
        UserVo userVo = convertFromMode(userModel);
        //返回通用对象
        return CommonReturnType.create(userVo);
    }
    private UserVo convertFromMode(UserModel userModel) {
    	if(userModel == null) {
    		return null;
    	}
    	UserVo userVo = new UserVo();
    	BeanUtils.copyProperties(userModel, userVo);
    	return userVo;
    }
    
    
}
