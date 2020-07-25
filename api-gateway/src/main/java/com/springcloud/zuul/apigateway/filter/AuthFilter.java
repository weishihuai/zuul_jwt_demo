package com.springcloud.zuul.apigateway.filter;

import com.alibaba.fastjson.JSONObject;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.springcloud.zuul.apigateway.utils.JwtUtils;
import com.springcloud.zuul.apigateway.utils.PathMatchUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * @author weixiaohuai
 * @Date 2020-07-19 14:40
 * @description 验证授权的过滤器
 */
@Component
public class AuthFilter extends ZuulFilter {

    private static Logger logger = LoggerFactory.getLogger(AuthFilter.class);

    /**
     * 读取配置文件中排除不需要授权的URL
     */
    @Value("${exclude.auth.url}")
    private String excludeAuthUrl;

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public String filterType() {
        //由于授权需要在请求之前调用，所以这里使用前置过滤器
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 2;
    }

    @Override
    public boolean shouldFilter() {
        //路径与配置文件中的相匹配，则执行过滤
        RequestContext ctx = RequestContext.getCurrentContext();
        String requestURI = ctx.getRequest().getRequestURI();
        List<String> excludesUrlList = Arrays.asList(excludeAuthUrl.split(","));
        return !excludesUrlList.contains(requestURI);
    }

    @Override
    public Object run() {
        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletRequest httpServletRequest = requestContext.getRequest();
        String token = httpServletRequest.getHeader("token");
        Claims claims;
        try {
            //解析没有异常则表示token验证通过，如有必要可根据自身需求增加验证逻辑
            claims = jwtUtils.parseJwtToken(token);
            //对请求进行路由
            requestContext.setSendZuulResponse(true);
            //请求头加入userId，传给具体的微服务
            requestContext.addZuulRequestHeader("userId", claims.get("userId").toString());
        } catch (ExpiredJwtException expiredJwtEx) {
            logger.error("token : {} 已过期", token);
            //不对请求进行路由
            requestContext.setSendZuulResponse(false);
            JSONObject resultJSONObject = new JSONObject();
            resultJSONObject.put("code", "40002");
            resultJSONObject.put("msg", "token已过期");
            requestContext.setResponseBody(resultJSONObject.toJSONString());
            HttpServletResponse response = requestContext.getResponse();
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.setContentType("application/json;charset=utf-8");
        } catch (Exception ex) {
            logger.error("token : {} 验证失败", token);
            //不对请求进行路由
            requestContext.setSendZuulResponse(false);
            JSONObject resultJSONObject = new JSONObject();
            resultJSONObject.put("code", "40001");
            resultJSONObject.put("msg", "非法token");
            requestContext.setResponseBody(resultJSONObject.toJSONString());
            HttpServletResponse response = requestContext.getResponse();
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.setContentType("application/json;charset=utf-8");
        }
        return null;
    }
}
