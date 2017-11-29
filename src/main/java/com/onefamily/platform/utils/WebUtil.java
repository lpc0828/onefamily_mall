package com.onefamily.platform.utils;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * 一个用于操作 ServletRequest
 *
 * @author pc.liu <mailto:pengcheng.liu0828@gmail.com/>
 */
public class WebUtil {

    /**
     * 获取给定请求的某个属性值
     *
     * @param request
     * @param attributeName
     * @param <T>
     * @return
     */
    public static <T> T getSessionAttr(HttpServletRequest request, String attributeName) {
        return (T) request.getSession().getAttribute(attributeName);
    }

    /**
     * 设置属性的某个值
     *
     * @param request
     * @param attrbuteName
     * @param object
     * @param <T>
     */
    public static <T> void putSessionAttr(HttpServletRequest request, String attrbuteName, T object) {
        request.getSession().setAttribute(attrbuteName, object);
    }

    /**
     * 获取请求的URI, 比如 http://www.baidu.com/a/b/c?name=d 得到值 /a/b/c
     *
     * @param request
     * @return String
     */
    public static String getRequestURI(HttpServletRequest request) {
        return request.getRequestURI();
    }

    /**
     * 获取不包含应用名字的URI的路径, 并去掉最前面的"/", <br>
     * 如路径为http://localhost:8080/appName/user/list.do, 得到的值为"user/list.do",其中appNames为应用的名字
     *
     * @param request
     * @return String
     */
    public static String getNoAppNamedRequestURI(HttpServletRequest request) {
        String contextPath = request.getContextPath();
        String uri = request.getRequestURI();
        if (uri.indexOf(contextPath) >= 0) {
            uri = uri.substring(contextPath.length());
        }
        while (uri.startsWith("/")) {
            uri = uri.substring(1);
        }
        uri = uri.replaceAll("/+", "/");
        return uri;
    }

    /**
     * 获取请求指定的header信息
     * @param request 请求
     * @param headers header的信息集合
     * @return
     */
    public static String getHttpHeaderInfo(HttpServletRequest request, List<String> headers) {
        StringBuilder buf = new StringBuilder(0);
        for (String header : headers) {
            buf.append(header + ": " + StringUtils.trimToEmpty(request.getHeader(header)) + "; ");
        }
        return buf.toString();
    }


    public static String getHttpHeaderInfo(HttpServletRequest request) {
        Enumeration<String> names = request.getHeaderNames();
        StringBuilder buf = new StringBuilder(0);
        while (names.hasMoreElements()) {
            String name = (String) names.nextElement();
            buf.append(name + ": " + request.getHeader(name) + " ;");
        }

        return buf.toString();
    }

    public static Map<String, Object> getRequestParameters(HttpServletRequest request) {
        Map<String, String[]> reqMap = request.getParameterMap();
        Map<String, Object> req = new HashMap<String, Object>();
        if (!reqMap.isEmpty()) {
            for (String key : reqMap.keySet()) {
                String[] args = reqMap.get(key);
                if (args != null && args.length > 1) {
                    req.put(key, Arrays.asList(args));
                } else if (args != null) {
                    req.put(key, args[0]);
                }
            }
        }

        return req;
    }

    /**
     * 获取应用的根目录
     *
     * @param request
     * @return
     */
    public static String getContextPath(HttpServletRequest request) {
        String contextPath = request.getContextPath();
        if (contextPath.equals("/")) {
            return "";
        }
        return contextPath;
    }


    /**
     * 获取完整请求路径(含内容路径及请求参数)
     *
     * @param request
     * @return
     */
    public static String getRequestURIWithParam(HttpServletRequest request) {
        return getRequestURI(request) + (request.getQueryString() == null ? "" : "?" + request.getQueryString());
    }


    /**
     * 添加cookie
     *
     * @param response
     * @param name     cookie的名称
     * @param value    cookie的值
     * @param maxAge   cookie存放的时间(以秒为单位,假如存放三天,即3*24*60*60; 如果值为0,cookie将随浏览器关闭而清除)
     */
    public static void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        if (maxAge > 0) {
            cookie.setMaxAge(maxAge);
        }
        response.addCookie(cookie);
    }

    public static void addCookie(HttpServletResponse response, String name, String value, int maxAge, String domain) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        if (maxAge > 0) {
            cookie.setMaxAge(maxAge);
        }
        cookie.setDomain(domain);
        response.addCookie(cookie);
    }

    /**
     * 获取cookie的值
     *
     * @param request
     * @param name    cookie的名称
     * @return
     */
    public static String getCookieByName(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (null != cookies) {
            for (int i = 0; i < cookies.length; i++) {
                if (cookies[i].getName().equals(name)) {
                    return cookies[i].getValue();
                }
            }
        }
        return null;
    }

    public static String getOSName() {
        String os = "Windows";
        if (System.getProperty("os.name").indexOf("Linux") != -1) {
            os = "Linux";
        } else if (System.getProperty("os.name").indexOf("Windows") != -1) {
            os = "Windows";
        }
        return os;
    }

    public static void main(String[] s) {
        String os = WebUtil.getOSName();
        System.out.println(os);
    }

    private static String[] getVisitorIp(HttpServletRequest request) {
        if (request == null) {
            return new String[]{"127.0.0.1"};
        }
        String ip = request.getHeader("X-Real-IP");
        if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip))
            ip = request.getHeader("X-Forwarded-For");
        if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip))
            ip = request.getRemoteAddr();
        return ip.split("\\s,\\s");
    }

    public static String getClientIp(HttpServletRequest request) {
        String[] ips = getVisitorIp(request);
        return (ips == null || ips.length == 0) ? "127.0.0.1" : ips[0];
    }

}
