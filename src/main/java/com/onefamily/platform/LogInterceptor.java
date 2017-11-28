package com.onefamily.platform;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LogInterceptor extends HandlerInterceptorAdapter {
    private static final String TrackerID = "trackID";
    private static final String UserID = "userID";
    private static final String IpAddr = "ipAddr";

    private static Logger log = LoggerFactory.getLogger(LogInterceptor.class);
    private static InheritableThreadLocal<Long> start = new InheritableThreadLocal<Long>();

    private static List<String> headers = new ArrayList<String>();

    static {
        headers.add("uid");
        headers.add("utoken");
        headers.add("platform");
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        start.set(System.currentTimeMillis());
        MDC.put(TrackerID, UUID.randomUUID().toString());
        MDC.put(UserID, request.getHeader("uid"));
        MDC.put(IpAddr, WebUtil.getClientIp(request));
        log.info(">> path:{}, method:{}, params:{}, headers:{}", WebUtil.getRequestURI(request), request.getMethod(),
                WebUtil.getRequestParameters(request), WebUtil.getHttpHeaderInfo(request, headers));
        return super.preHandle(request, response, handler);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
        long cost = System.currentTimeMillis() - start.get();
        start.remove();
        String url = WebUtil.getRequestURI(request);
        if (ex != null) {
            log.info("<< path:[{}], cost:{}, error, cause:[{}]", url, cost, ex.getMessage(), ex);
        } else {
            log.info("<< path:[{}], success, time:[{}]", url, cost);
        }
        super.afterCompletion(request, response, handler, ex);
    }
}
