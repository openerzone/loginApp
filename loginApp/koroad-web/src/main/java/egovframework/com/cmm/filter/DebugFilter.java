package egovframework.com.cmm.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 디버깅용 Filter
 * 요청이 어떤 servlet으로 처리되는지 확인
 */
public class DebugFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("=== DebugFilter 초기화됨 ===");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        String requestURI = httpRequest.getRequestURI();
        String servletPath = httpRequest.getServletPath();
        String contextPath = httpRequest.getContextPath();
        
        System.out.println("=== DebugFilter 요청 처리 시작 ===");
        System.out.println("=== Request URI: " + requestURI + " ===");
        System.out.println("=== Servlet Path: " + servletPath + " ===");
        System.out.println("=== Context Path: " + contextPath + " ===");
        System.out.println("=== Request Method: " + httpRequest.getMethod() + " ===");
        System.out.println("=== Thread: " + Thread.currentThread().getName() + " ===");
        System.out.println("=== Server Name: " + httpRequest.getServerName() + " ===");
        System.out.println("=== Server Port: " + httpRequest.getServerPort() + " ===");
        System.out.println("=== Request URL: " + httpRequest.getRequestURL() + " ===");
        System.out.println("=== Path Info: " + httpRequest.getPathInfo() + " ===");
        System.out.println("=== Servlet Name: " + httpRequest.getServletContext().getServletContextName() + " ===");
        
        try {
            chain.doFilter(request, response);
            System.out.println("=== DebugFilter 요청 처리 완료 (정상) ===");
        } catch (Exception e) {
            System.out.println("=== DebugFilter 요청 처리 중 오류: " + e.getMessage() + " ===");
            throw e;
        }
    }

    @Override
    public void destroy() {
        System.out.println("=== DebugFilter 종료됨 ===");
    }
}
