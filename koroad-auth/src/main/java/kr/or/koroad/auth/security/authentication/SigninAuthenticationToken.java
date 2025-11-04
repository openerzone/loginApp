package kr.or.koroad.auth.security.authentication;

import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class SigninAuthenticationToken extends AbstractAuthenticationToken {

	private static final long serialVersionUID = 1L;
	
	private final Object principal;
    private final Object credentials;
    
    // 1단계 인증 성공 시 생성자: 임시 권한(ROLE_PRE_AUTH)을 부여합니다.
    public SigninAuthenticationToken(Object principal, Object credentials, 
                        Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.credentials = credentials;
        
        // 1단계만 완료했으므로, 인증은 완료되지 않음 (false 유지)
        super.setAuthenticated(false); 
    }

    @Override
    public Object getCredentials() {
        return this.credentials; // 비밀번호/토큰 (2단계로 전달될 정보)
    }

    @Override
    public Object getPrincipal() {
        return this.principal; // 사용자 정보 (UserDetails)
    }
    
    // ⭐ 핵심: 1단계만 완료했으므로 최종 인증 상태는 false입니다.
    @Override
    public boolean isAuthenticated() {
        return super.isAuthenticated(); // false를 반환합니다.
    }
}
