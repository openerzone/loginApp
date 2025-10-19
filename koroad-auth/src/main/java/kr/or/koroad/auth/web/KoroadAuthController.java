package kr.or.koroad.auth.web;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.egovframe.rte.fdl.cmmn.trace.LeaveaTrace;
import org.egovframe.rte.fdl.property.EgovPropertyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import kr.or.koroad.auth.service.AbstractKoroadUserDetails;
import kr.or.koroad.auth.service.OtpService;

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
	
	@Value("${auth.success.redirect.path}")
	private String successRedirectPath;

	/** EgovPropertyService */
	@Resource(name = "propertiesService")
	protected EgovPropertyService propertiesService;
	
	@Autowired
	private OtpService otpService;
	
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
    public String loginFail(HttpServletRequest request, Model model) {
    	
    	Boolean error = (Boolean) request.getAttribute("error");
    	String message = (String) request.getAttribute("message");
    	
    	if (error) {
    		model.addAttribute("errorMessage", message);
    	}
    	
    	model.addAttribute("title", title);
    	
    	return "/login";
    }
    
    /**
     * OTP 입력 페이지
     */
    @GetMapping("/otp")
    public String otpPage(HttpServletRequest request, Model model) {
    	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    	AbstractKoroadUserDetails userDetails = (AbstractKoroadUserDetails) authentication.getPrincipal();
        
    	// Authentication 객체의 권한을 확인 (UserDetails가 아님!)
    	boolean isOtpEnabled = authentication.getAuthorities().stream()
    			.anyMatch(a -> a.getAuthority().equals("ROLE_2FA_PENDING"));
    	
    	if (isOtpEnabled) {
    		System.out.println("=== OTP 인증 페이지 접근 ===");
    		System.out.println("사용자: " + userDetails.getUsername());
    		System.out.println("ROLE_2FA_PENDING 권한 확인됨");
    		
    		// OTP 생성
    		String username = userDetails.getUsername();
    		String otpCode = otpService.generateOtp(username);
    		
    		// 세션에 OTP 인증 대기 상태 저장
    		HttpSession session = request.getSession();
    		session.setAttribute("OTP_PENDING_USER", username);
    		session.setAttribute("OTP_AUTHENTICATED", false);
    		session.setAttribute("OTP_CODE_FOR_TESTING", otpCode);
    		
    		model.addAttribute("title", title);
    		model.addAttribute("username", username);
    		model.addAttribute("otpCode", otpCode);
    		model.addAttribute("infoMessage", "OTP 인증번호가 발송되었습니다.");
    		
    		return "/otp";
    	} else {
    		System.out.println("ROLE_2FA_PENDING not exists - 로그인 페이지로 리다이렉트");
    		return "redirect:/auth/login";
    	}
    }
    
    /**
     * OTP 검증 처리
     */
    @PostMapping("/otp/verify")
    public String verifyOtp(@RequestParam("otpCode") String otpCode,
                           HttpServletRequest request,
                           Model model) {
    	
    	HttpSession session = request.getSession();
    	String username = (String) session.getAttribute("OTP_PENDING_USER");
    	
    	// OTP 인증 대기 상태가 아니면 로그인 페이지로 리다이렉트
    	if (username == null) {
    		return "redirect:/auth/login";
    	}
    	
    	// OTP 검증
    	boolean isValid = otpService.verifyOtp(username, otpCode);
    	
    	if (isValid) {
    		// OTP 인증 성공
    		session.setAttribute("OTP_AUTHENTICATED", true);
    		session.removeAttribute("OTP_PENDING_USER");
    		session.removeAttribute("OTP_CODE_FOR_TESTING");
    		
    		// ROLE_2FA_PENDING 권한 제거
    		Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();
    		AbstractKoroadUserDetails userDetails = (AbstractKoroadUserDetails) currentAuth.getPrincipal();
    		
    		// ROLE_2FA_PENDING을 제외한 원래 권한만 유지
    		List<GrantedAuthority> finalAuthorities = currentAuth.getAuthorities().stream()
    				.filter(auth -> !auth.getAuthority().equals("ROLE_2FA_PENDING"))
    				.collect(java.util.stream.Collectors.toList());
    		
    		// 최종 인증 객체 생성 (원래 권한만 포함)
    		Authentication finalAuth = new UsernamePasswordAuthenticationToken(
    				userDetails,
    				null,
    				finalAuthorities
    		);
    		
    		// SecurityContext 업데이트
    		SecurityContextHolder.getContext().setAuthentication(finalAuth);
    		
    		System.out.println("=== OTP 인증 성공 ===");
    		System.out.println("사용자: " + username);
    		System.out.println("ROLE_2FA_PENDING 제거됨");
    		System.out.println("최종 권한: " + finalAuthorities);
    		System.out.println("최종 목적지로 리다이렉트: " + successRedirectPath);
    		
    		// 최종 목적지로 리다이렉트
    		return "redirect:" + successRedirectPath;
    	} else {
    		// OTP 인증 실패
    		String testOtp = (String) session.getAttribute("OTP_CODE_FOR_TESTING");
    		
    		model.addAttribute("title", title);
    		model.addAttribute("username", username);
    		model.addAttribute("otpCode", testOtp);
    		model.addAttribute("errorMessage", "인증번호가 올바르지 않습니다. 다시 시도해주세요.");
    		
    		return "/otp";
    	}
    }
    
    /**
     * OTP 재전송
     */
    @PostMapping("/otp/resend")
    public String resendOtp(HttpServletRequest request, Model model) {
    	HttpSession session = request.getSession();
    	String username = (String) session.getAttribute("OTP_PENDING_USER");
    	
    	// OTP 인증 대기 상태가 아니면 로그인 페이지로 리다이렉트
    	if (username == null) {
    		return "redirect:/auth/login";
    	}
    	
    	// 새로운 OTP 생성 및 발송
    	String newOtpCode = otpService.resendOtp(username);
    	
    	// 테스트용: 세션에 새 OTP 코드 저장
    	session.setAttribute("OTP_CODE_FOR_TESTING", newOtpCode);
    	
    	model.addAttribute("title", title);
    	model.addAttribute("username", username);
    	model.addAttribute("otpCode", newOtpCode);
    	model.addAttribute("successMessage", "인증번호가 재전송되었습니다.");
    	
    	return "/otp";
    }
    
}