package kr.or.koroad.auth.authentication;

import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class OtpAuthenticationToken extends AbstractAuthenticationToken{

	private static final long serialVersionUID = 1L;
	
	private final Object principal; // 사용자 이름 (String)
    private Object credentials; // OTP 코드 (String)
    
 // 인증 요청 시 사용할 생성자 (principal, credentials)
    public OtpAuthenticationToken(Object principal, Object credentials) {
        super(null); // 권한은 아직 없음
        this.principal = principal;
        this.credentials = credentials;
        setAuthenticated(false); // 아직 인증 안 됨
    }

    // 인증 성공 후 사용할 생성자 (principal, authorities)
    public OtpAuthenticationToken(Object principal, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.credentials = null; // 인증 후 비번/OTP는 제거
        setAuthenticated(true); // 인증 완료
    }
    
	@Override
	public Object getCredentials() {
		return this.credentials;
	}

	@Override
	public Object getPrincipal() {
		return this.principal;
	}

}
