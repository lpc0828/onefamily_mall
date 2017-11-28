package com.onefamily.ucenter.controller;

import com.onefamily.mall.service.sms.ISmsService;
import com.onefamily.platform.BaseController;
import com.onefamily.platform.JsonWrapper;
import com.onefamily.platform.ResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@RequestMapping("verifyCode")
@Controller
public class VerifyCodeController extends BaseController {

    private final String ModuleCode = "ucenter";
    @Autowired
    private ISmsService smsService;

    @RequestMapping("sendCode.do")
    @ResponseBody
    JsonWrapper<String> sendVerifyCode(HttpServletRequest request, String mobile) {
        JsonWrapper<String> jsonWrapper = initJsonWrapper(request);
        ResultVO<String> resultVO = smsService.sendVerifyCode(mobile, ModuleCode, -1, null);
        if (resultVO.isRetBool()) {
            jsonWrapper.setData(resultVO.getT());
            jsonWrapper.setErrorMsg(resultVO.getMessage());
        } else {
            jsonWrapper.setStatus(JsonWrapper.StatusEnum.Fail.getCode());
            jsonWrapper.setErrorMsg(resultVO.getMessage());
        }
        jsonWrapper.timeWatchStop();

        return jsonWrapper;
    }

    @RequestMapping("verifyCode.do")
    @ResponseBody
    JsonWrapper<String> doVerifyCode(HttpServletRequest request, String mobile, String verifyCode) {
        JsonWrapper<String> jsonWrapper = initJsonWrapper(request);
        ResultVO<String> resultVO = smsService.authVerifyCode(mobile, ModuleCode, verifyCode);
        if (resultVO.isRetBool()) {
            jsonWrapper.setErrorMsg(resultVO.getMessage());
        } else {
            jsonWrapper.setStatus(JsonWrapper.StatusEnum.Fail.getCode());
            jsonWrapper.setErrorMsg(resultVO.getMessage());
        }
        jsonWrapper.timeWatchStop();

        return jsonWrapper;
    }
}
