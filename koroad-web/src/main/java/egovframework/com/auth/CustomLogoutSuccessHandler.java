package egovframework.com.auth;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import egovframework.com.cmm.web.EgovAtchFileIdPropertyEditor;

public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {
    
	private static final Logger LOGGER = LoggerFactory.getLogger(EgovAtchFileIdPropertyEditor.class);
	
    private String defaultTargetUrl;
    
    @Override
    public void onLogoutSuccess(HttpServletRequest request, 
                                HttpServletResponse response,
                                Authentication authentication) 
            throws IOException, ServletException {
        
        // 1. 로그아웃 이력 저장
        if (authentication != null) {
        	LOGGER.info("User logged out: {}", authentication.getName());
            // logoutHistoryService.save(authentication.getName());
        }
        
        // 2. 세션 정리
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.removeAttribute("customData");
        }
        
        // 3. 로그아웃 메시지 설정
        request.getSession().setAttribute("logoutMessage", 
            "성공적으로 로그아웃되었습니다.");
        
        // 4. 리다이렉트
        response.sendRedirect(request.getContextPath() + defaultTargetUrl);
    }
    
    public void setDefaultTargetUrl(String defaultTargetUrl) {
        this.defaultTargetUrl = defaultTargetUrl;
    }
}