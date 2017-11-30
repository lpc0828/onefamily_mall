package com.onefamily.ucenter.controller;

import com.google.common.collect.Maps;
import com.onefamily.common.enums.YesNoEnum;
import com.onefamily.platform.controller.BaseController;
import com.onefamily.platform.dataformat.JsonWrapper;
import com.onefamily.platform.dataformat.ResultVO;
import com.onefamily.ucenter.constants.Headers;
import com.onefamily.ucenter.model.User;
import com.onefamily.ucenter.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RequestMapping("uc")
@Controller
public class AuthController extends BaseController {

    @Autowired
    private IUserService userService;

    @RequestMapping("auth.do")
    @ResponseBody JsonWrapper<Map<String, Object>> authorize(HttpServletRequest request) {
        JsonWrapper<Map<String, Object>> jsonWrapper = initJsonWrapper(request);
        try {
            ResultVO<User> userResultVO = userService.auth(request.getHeader(Headers.UID), request.getHeader(Headers.Platform), request.getHeader(Headers.UToken));
            if (!userResultVO.isRetBool()) {
                jsonWrapper.setStatus(JsonWrapper.StatusEnum.AuthFail.getCode());
                jsonWrapper.setErrorMsg(userResultVO.getMessage());
            } else {
                Map<String, Object> userMap = Maps.newHashMap();
                userMap.put("utoken", userResultVO.getT().getAppToken());
                userMap.put("uid", userResultVO.getT().getId());
                userMap.put("headimgurl", userResultVO.getT().getHeadimgurl());
                userMap.put("nickName", userResultVO.getT().getNickName());
                userMap.put("ctime", userResultVO.getT().getCreatedDate().getTime());
                userMap.put("certYn", YesNoEnum.codeOf(userResultVO.getT().getCertificationYn()).getValue());
                jsonWrapper.setStatus(JsonWrapper.StatusEnum.Success.getCode());
                jsonWrapper.setErrorMsg(userResultVO.getMessage());
                jsonWrapper.setData(userMap);
            }
        } finally {
            jsonWrapper.timeWatchStop();
        }
        return jsonWrapper;
    }
}
