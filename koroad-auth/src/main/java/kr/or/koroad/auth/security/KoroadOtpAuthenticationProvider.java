package kr.or.koroad.auth.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;

import kr.or.koroad.auth.authentication.OtpAuthenticationToken;
import kr.or.koroad.auth.service.AbstractKoroadUserDetails;
import kr.or.koroad.auth.service.AbstractKoroadUserDetailsService;
import kr.or.koroad.auth.service.OtpService;

/**
 * 로그인 화면에 OTP 입력이 있다면 해당 provider를 등록한다.
 * @author opene
 *
 */
public class KoroadOtpAuthenticationProvider implements AuthenticationProvider {

	private OtpService otpService;
	
	private AbstractKoroadUserDetailsService userDetailsService;
	
	/**
     * 실제 인증 로직
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    	
        String username = (String) authentication.getPrincipal();
        String otp = (String) authentication.getCredentials();

        // 1. 실제 OTP 서비스(Google Authenticator 등)를 통해 OTP 검증
         if (!otpService.verifyOtp(username, otp)) {
             throw new BadCredentialsException("Invalid OTP code");
         }

        // 2. OTP 검증 성공! 사용자의 *최종* 권한을 다시 로드.
        // 중요: 여기서는 CustomUserDetailsService가 아닌, 
        // 최종 권한(ROLE_USER)을 반환하는 별도의 UserDetailsService를 사용하거나
        // CustomUserDetailsService 내부 로직을 수정해야 할 수 있습니다.
        // 여기서는 편의상 CustomUserDetailsService가 username으로 최종 권한을 준다고 가정합니다.
        
        // UserDetails userDetails = userDetailsService.loadUserByUsername(username); 
        // -> 이렇게 하면 CustomUserDetailsService가 ROLE_MFA_PENDING을 반환할 수 있음.
        
        // 해결책: 최종 권한을 가진 UserDetails를 직접 생성하거나 별도 서비스 사용
        // UserDetails userDetails = finalUserDetailsService.loadUserByUsername(username);
        
        // OTP 검증 후이므로 ROLE_2FA_PENDING을 추가하지 않는 메서드 사용
        AbstractKoroadUserDetails userDetails = (AbstractKoroadUserDetails)userDetailsService.loadUserByUsernameWithoutOtpPending(username);
        // 3. 최종 인증 객체(Authentication) 반환
        // OtpAuthenticationToken 대신 UsernamePasswordAuthenticationToken을 반환하는 것이 일반적입니다.
        // Spring Security의 나머지 필터들이 이 토큰을 인식하기 때문입니다.
        return new UsernamePasswordAuthenticationToken(
                userDetails,
                null, // Credentials는 null
                userDetails.getAuthorities() // 최종 권한
        );
    }

    /**
     * 이 Provider가 어떤 타입의 Authentication을 처리할지 지정
     */
    @Override
    public boolean supports(Class<?> authentication) {
        // OtpAuthenticationToken 타입의 인증 요청을 처리
        return OtpAuthenticationToken.class.isAssignableFrom(authentication);
    }

	public void setUserDetailsService(AbstractKoroadUserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}
	
	public void setOtpService(OtpService otpService) {
		this.otpService = otpService;
	}
}
