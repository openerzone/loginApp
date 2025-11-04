package kr.or.koroad.auth.web;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
import kr.or.koroad.auth.util.EgovPasswordEncoder;

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
	
//	@Autowired
//	private kr.or.koroad.auth.handler.OtpAuthenticationSuccessHandler otpAuthenticationSuccessHandler;
	
	@Autowired
	private EgovPasswordEncoder passwordEncoder;
	
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
//    		String username = userDetails.getUsername();
    		String otpCode = "123456"; //otpService.generateOtp(username);
    		
    		// 세션에 OTP 인증 대기 상태 저장
//    		HttpSession session = request.getSession();
//    		session.setAttribute("OTP_PENDING_USER", username);
//    		session.setAttribute("OTP_AUTHENTICATED", false);
//    		session.setAttribute("OTP_CODE_FOR_TESTING", otpCode);
    		
    		model.addAttribute("title", title);
//    		model.addAttribute("username", username);
    		model.addAttribute("otpCode", otpCode);
//    		model.addAttribute("infoMessage", "OTP 인증번호가 발송되었습니다.");
    		
    		return "/otp";
    	} else {
    		System.out.println("ROLE_2FA_PENDING not exists - 로그인 페이지로 리다이렉트");
    		return "redirect:/auth/login";
    	}
    }

    @PostMapping("/otp/failure")
    public String otpFail(HttpServletRequest request, Model model) {
    	
//    	OTP 실패 시 SecurityContextHolder가 비워져 Authentication이 없다.
    	Boolean error = (Boolean) request.getAttribute("error");
    	String message = (String) request.getAttribute("message");
    	
    	if (error) {
    		model.addAttribute("errorMessage", message);
    	}
    	
    	// 세션에서 사용자명 가져오기 (Authentication이 없을 수 있음)
    	//HttpSession session = request.getSession();
    	//String username = (String) session.getAttribute("OTP_PENDING_USER");
    	String username = (String) request.getAttribute("username");
    	
    	// 세션에 사용자명이 없으면 로그인 페이지로
    	if (username == null || username.trim().isEmpty()) {
    		System.out.println("OTP 실패 처리: 세션에 사용자명 없음 - 로그인 페이지로 이동");
    		return "redirect:/auth/login";
    	}
    	
    	// 새로운 OTP 생성
    	String otpCode = "123456";//otpService.generateOtp(username);
		
		// 세션에 OTP 인증 대기 상태 저장
//		session.setAttribute("OTP_PENDING_USER", username);
//		session.setAttribute("OTP_AUTHENTICATED", false);
//		session.setAttribute("OTP_CODE_FOR_TESTING", otpCode);
		
		model.addAttribute("title", title);
//		model.addAttribute("username", username);
		model.addAttribute("otpCode", otpCode);
    	
    	return "/otp";
    }
    
    /**
     * OTP 검증 처리
     */
//    @PostMapping("/otp/verify")
    public String verifyOtp(@RequestParam("otpCode") String otpCode,
                           HttpServletRequest request,
                           HttpServletResponse response,
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
    		
    		// 최종 AuthenticationSuccessHandler 호출
    		try {
    			System.out.println("최종 AuthenticationSuccessHandler 실행");
//    			otpAuthenticationSuccessHandler.getFinalSuccessHandler()
//    				.onAuthenticationSuccess(request, response, finalAuth);
    			return null; // 핸들러가 리다이렉트 처리함
    		} catch (Exception e) {
    			System.err.println("최종 핸들러 실행 중 오류 발생: " + e.getMessage());
    			e.printStackTrace();
    			// 폴백: 기본 리다이렉트
    			return "redirect:" + successRedirectPath;
    		}
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
    
    /**
     * 비밀번호 변경 페이지
     */
    @GetMapping("/change-password")
    public String changePasswordPage(Model model) {
    	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    	
    	// 인증되지 않았으면 로그인 페이지로
    	if (authentication == null || !authentication.isAuthenticated()) {
    		return "redirect:/auth/login";
    	}
    	
    	// ROLE_PASSWORD_CHANGE_REQUIRED 권한 확인
    	boolean isPasswordChangeRequired = authentication.getAuthorities().stream()
    			.anyMatch(auth -> auth.getAuthority().equals("ROLE_PASSWORD_CHANGE_REQUIRED"));
    	
    	AbstractKoroadUserDetails userDetails = (AbstractKoroadUserDetails) authentication.getPrincipal();
    	
    	model.addAttribute("title", title);
    	model.addAttribute("username", userDetails.getUsername());
    	
    	if (isPasswordChangeRequired) {
    		model.addAttribute("infoMessage", "비밀번호가 만료되었습니다. 새로운 비밀번호로 변경해주세요.");
    	}
    	
    	return "/change-password";
    }
    
    /**
     * 비밀번호 변경 처리
     */
    @PostMapping("/change-password/process")
    public String changePasswordProcess(@RequestParam("currentPassword") String currentPassword,
                                       @RequestParam("newPassword") String newPassword,
                                       @RequestParam("confirmPassword") String confirmPassword,
                                       HttpServletRequest request,
                                       HttpServletResponse response,
                                       Model model) {
    	
    	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    	
    	// 인증되지 않았으면 로그인 페이지로
    	if (authentication == null || !authentication.isAuthenticated()) {
    		return "redirect:/auth/login";
    	}
    	
    	AbstractKoroadUserDetails userDetails = (AbstractKoroadUserDetails) authentication.getPrincipal();
    	String username = userDetails.getUsername();
    	
    	model.addAttribute("title", title);
    	model.addAttribute("username", username);
    	
    	// 1. 새 비밀번호와 확인 비밀번호 일치 확인
    	if (!newPassword.equals(confirmPassword)) {
    		model.addAttribute("errorMessage", "새 비밀번호가 일치하지 않습니다.");
    		return "/change-password";
    	}
    	
    	// 2. 현재 비밀번호 검증
    	boolean isCurrentPasswordValid = passwordEncoder.matchesWithSalt(
    			currentPassword, 
    			userDetails.getPassword(), 
    			username
    	);
    	
    	if (!isCurrentPasswordValid) {
    		model.addAttribute("errorMessage", "현재 비밀번호가 올바르지 않습니다.");
    		return "/change-password";
    	}
    	
    	// 3. TODO: 실제 DB 업데이트 로직 (여기서는 로그만 출력)
    	System.out.println("=== 비밀번호 변경 요청 ===");
    	System.out.println("사용자: " + username);
    	System.out.println("비밀번호 변경 성공");
    	
    	// 4. ROLE_PASSWORD_CHANGE_REQUIRED 권한 제거
    	List<GrantedAuthority> finalAuthorities = authentication.getAuthorities().stream()
    			.filter(auth -> !auth.getAuthority().equals("ROLE_PASSWORD_CHANGE_REQUIRED"))
    			.collect(java.util.stream.Collectors.toList());
    	
    	Authentication finalAuth = new UsernamePasswordAuthenticationToken(
    			userDetails,
    			null,
    			finalAuthorities
    	);
    	
    	SecurityContextHolder.getContext().setAuthentication(finalAuth);
    	
    	// 5. 최종 핸들러 호출
    	try {
    		System.out.println("비밀번호 변경 완료 - 최종 핸들러 실행");
//    		otpAuthenticationSuccessHandler.getFinalSuccessHandler()
//    			.onAuthenticationSuccess(request, response, finalAuth);
    		return null;
    	} catch (Exception e) {
    		System.err.println("최종 핸들러 실행 중 오류: " + e.getMessage());
    		e.printStackTrace();
    		return "redirect:" + successRedirectPath;
    	}
    }
    
}