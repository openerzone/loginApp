package kr.or.koroad.auth.authentication;

import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class SsoAuthenticationToken extends AbstractAuthenticationToken{

	private static final long serialVersionUID = 1L;
	
	private final Object principal; // 사용자 이름 (String)
	private Object credentials;

	 // 인증 요청 시 사용할 생성자 (principal, credentials)
    public SsoAuthenticationToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities) {
        super(authorities); 
        this.principal = principal;
        this.credentials = credentials;	//인증 후 비번 제거
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
