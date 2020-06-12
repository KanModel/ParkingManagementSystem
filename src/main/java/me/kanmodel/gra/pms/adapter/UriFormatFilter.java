package me.kanmodel.gra.pms.adapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;

/**
 * uri格式化过滤器
 * 用于屏蔽双斜杠//导致报错
 * 记录访问情况
 *
 * @author: KanModel
 * @create: 2019-08-08 22:52
 */
@Component("uriFormatFilter")
public class UriFormatFilter extends OncePerRequestFilter {
    private Logger logger = LoggerFactory.getLogger(getClass());
    private static final List<String> SECRET_URL = Arrays.asList("/user/edit/mpass", "/user/edit/pass", "/user/add");
    private static final List<String> DUMP_URL = Arrays.asList("/api/scatter/count", "/api/scatter"
            ,"/webjars/bootstrap/3.3.7/css/bootstrap.min.css", "/login", "/image/res001.jpg");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String uri = request.getRequestURI();
        Enumeration em = request.getParameterNames();
        StringBuilder para = new StringBuilder("");
        while (em.hasMoreElements()) {
            String name = (String) em.nextElement(); //参数名称
            para.append("&").append(name).append("=").append(request.getParameter(name)); //根据参数名称获取到参数值
        }
        if (SECRET_URL.indexOf(uri) != -1) {
            logger.info("[" + request.getRemoteAddr() + "] [" + request.getMethod() + "] [" + uri + "]");//隐私数据隐藏参数
        } else {
            if (DUMP_URL.indexOf(uri) == -1) {
                logger.info("[" + request.getRemoteAddr() + "] [" + request.getMethod() + "] [" + uri + "]" + para);
            }
        }
        String newUri = uri.replace("//", "/");
        request = new HttpServletRequestWrapper(request) {
            @Override
            public String getRequestURI() {
                return newUri;
            }
        };

        filterChain.doFilter(request, response);
    }
}
