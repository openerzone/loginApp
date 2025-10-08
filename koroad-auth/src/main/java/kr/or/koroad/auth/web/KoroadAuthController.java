package kr.or.koroad.auth.web;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.egovframe.rte.fdl.cmmn.trace.LeaveaTrace;
import org.egovframe.rte.fdl.property.EgovPropertyService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import kr.or.koroad.auth.config.KoroadAuthConfigure;
import kr.or.koroad.auth.service.EgovLoginService;

/**
 * 일반 로그인을 처리하는 컨트롤러 클래스
 * @author 공통서비스 개발팀 박지욱
 * @since 2009.03.06
 * @version 1.0
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *  수정일      수정자      수정내용
 *  -------            --------        ---------------------------
 *  2009.03.06  박지욱     최초 생성
 *  2011.08.31  JJY            경량환경 템플릿 커스터마이징버전 생성
 *
 *  </pre>
 */
@Controller
//@RequestMapping("/auth")
public class KoroadAuthController {

	@Value("${auth.site.title}")
	private String title;

	/** EgovLoginService */
	@Resource(name = "loginService")
	private EgovLoginService loginService;

	/** EgovPropertyService */
	@Resource(name = "propertiesService")
	protected EgovPropertyService propertiesService;
	
//	@Resource(name = "koroadAuthConfigure")
//	private KoroadAuthConfigure koroadAuthConfigure;

	/** TRACE */
	@Resource(name = "leaveaTrace")
	LeaveaTrace leaveaTrace;

	@PostConstruct
	public void init() {
		System.out.println("=== KoroadAuthController 빈이 초기화되었습니다 ===");
		System.out.println("=== RequestMapping: /auth ===");
		System.out.println("=== @GetMapping(\"/login\") 메서드가 등록되었습니다 ===");
		System.out.println("=== 전체 URL: /auth/login ===");
		System.out.println("=== auth 서블릿 컨텍스트에서 초기화됨 ===");
	}

    /**
     * 로그인 페이지
     */
    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
                       @RequestParam(value = "logout", required = false) String logout,
                       @RequestParam(value = "expired", required = false) String expired,
                       Model model) {
        System.out.println("=== KoroadAuthController.login() 메서드가 호출되었습니다 ===");
        System.out.println("=== GET /auth/login 요청 처리 중 ===");
        System.out.println("=== error: " + error + ", logout: " + logout + ", expired: " + expired + " ===");
        System.out.println("=== 현재 Thread: " + Thread.currentThread().getName() + " ===");
        System.out.println("=== 현재 Context: " + this.getClass().getClassLoader() + " ===");
        System.out.println("=== Controller 매핑이 정상적으로 작동하고 있습니다 ===");
        
        if (error != null) {
            model.addAttribute("errorMessage", "사용자명 또는 비밀번호가 올바르지 않습니다.");
        }
        
        if (logout != null) {
            model.addAttribute("successMessage", "성공적으로 로그아웃되었습니다.");
        }
        
        if (expired != null) {
            model.addAttribute("errorMessage", "세션이 만료되었습니다. 다시 로그인해주세요.");
        }
        
//        model.addAttribute("authConfig", koroadAuthConfigure);
        System.out.println("title : " + title);
        model.addAttribute("title", title);
        
        return "/login";
    }
    
}