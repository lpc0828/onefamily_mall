package com.onefamily.mall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@SpringBootApplication
public class MallApplication {

	@RequestMapping("/")
	@ResponseBody String index() {
		return "hello world!!";
	}

	public static void main(String[] args) {
		SpringApplication.run(MallApplication.class, args);
	}
}
