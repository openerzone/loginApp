package kr.or.koroad.auth.web;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.egovframe.rte.fdl.cmmn.trace.LeaveaTrace;
import org.egovframe.rte.fdl.property.EgovPropertyService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

	/** EgovPropertyService */
	@Resource(name = "propertiesService")
	protected EgovPropertyService propertiesService;
	
//	@Resource(name = "koroadAuthConfigure")
//	private KoroadAuthConfigure koroadAuthConfigure;

	/** TRACE */
	@Resource(name = "leaveaTrace")
	LeaveaTrace leaveaTrace;

    /**
     * 로그인 페이지
     */
    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
                       @RequestParam(value = "logout", required = false) String logout,
                       @RequestParam(value = "expired", required = false) String expired,
                       Model model) {
        
        if (error != null) {
            model.addAttribute("errorMessage", "사용자명 또는 비밀번호가 올바르지 않습니다.");
        }
        
        if (logout != null) {
            model.addAttribute("successMessage", "성공적으로 로그아웃되었습니다.");
        }
        
        if (expired != null) {
            model.addAttribute("errorMessage", "세션이 만료되었습니다. 다시 로그인해주세요.");
        }
        
        model.addAttribute("title", title);
        
        return "/login";
    }
    
    @PostMapping("/failure")
    public String loginFail(@RequestParam(value = "error", required = false) String error,
    						HttpServletRequest request,
				    		Model model) {
    	
    	if (error != null) {
    		model.addAttribute("errorMessage", request.getAttribute("errorMessage"));
    	}
    	
    	
    	model.addAttribute("title", title);
    	
    	return "/login";
    }
    
}