package com.springcloud.zuul.apigateway.filter;

        import com.netflix.zuul.ZuulFilter;
        import com.netflix.zuul.context.RequestContext;
        import com.springcloud.zuul.apigateway.utils.JwtUtils;
        import com.springcloud.zuul.apigateway.utils.PathMatchUtil;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.beans.factory.annotation.Value;
        import org.springframework.stereotype.Component;

        import javax.servlet.http.HttpServletRequest;
        import java.util.Date;
        import java.util.HashMap;
        import java.util.Map;

/**
 * @author weixiaohuai
 * @Date 2020-07-19 14:34
 * @description 拦截登录请求后的过滤器
 */
@Component
public class LoginFilter extends ZuulFilter {

    @Value("${common.login.url}")
    private String loginUrl;

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public String filterType() {
        return "post";
    }

    @Override
    public int filterOrder() {
        return 1;
    }

    @Override
    public boolean shouldFilter() {
        //只有路径与配置文件中配置的登录路径相匹配，才会放行该过滤器,执行过滤操作
        RequestContext ctx = RequestContext.getCurrentContext();
        String requestURI = ctx.getRequest().getRequestURI();
        for (String url : loginUrl.split(",")) {
            if (PathMatchUtil.isPathMatch(url, requestURI)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Object run() {
        RequestContext requestContext = RequestContext.getCurrentContext();
        try {
            HttpServletRequest httpServletRequest = requestContext.getRequest();
            //此处简单模拟登录,并非生产环境登录使用.
            String username = httpServletRequest.getParameter("username");
            String password = httpServletRequest.getParameter("password");
            if ("weishihuai".equals(username) && "password".equals(password)) {
                //表示登录成功,服务器端需要生成token返回给客户端
                //过期时间: 2分钟
                Date expDate = new Date(System.currentTimeMillis() + 2 * 60 * 1000);
                Map<String, Object> claimsMap = new HashMap<>();
                claimsMap.put("username", "weishihuai");
                claimsMap.put("userId", "201324131147");
                claimsMap.put("expDate", expDate);
                String jwtToken = jwtUtils.createJwtToken(expDate, claimsMap);
                //响应头设置token
                requestContext.addZuulResponseHeader("token", jwtToken);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
