package com.onefamily.platform.controller;

import com.onefamily.platform.dataformat.JsonWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class BaseController {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    public <T> JsonWrapper<T> initJsonWrapper(HttpServletRequest request) {
        JsonWrapper<T> jsonWrapper = new JsonWrapper<T>();
        jsonWrapper.setStatus(JsonWrapper.StatusEnum.Success.getCode());
        jsonWrapper.setErrorMsg(JsonWrapper.StatusEnum.Success.getDesc());
        jsonWrapper.setTrackId(request.getParameter("trackId"));
        return jsonWrapper;
    }

}
