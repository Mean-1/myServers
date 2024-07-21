package main.java.com.service.control.impl;

import main.java.com.service.control.Controller;
import main.java.com.service.control.IController;
import main.java.com.service.control.Request;
import main.java.com.service.proto.http.HttpRequest;
import main.java.com.service.proto.http.HttpResponse;

/**
 * @ClassName: HelloWorldController
 * @Description:
 * @author: liuchen
 * @date: 2024/7/16 0:02
 */
@Controller
public class HelloWorldController implements IController {

    @Request(url = "/hello")
    public String hello(HttpRequest request){


        return "请求类型："+request.getMethod()+"  body："+request.getMessage();
    }

}
