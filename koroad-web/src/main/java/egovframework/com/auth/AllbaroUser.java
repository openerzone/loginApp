package egovframework.com.auth;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;

import egovframework.com.cmm.LoginVO;
import kr.or.koroad.auth.service.KoroadUserDetails;

public class AllbaroUser extends KoroadUserDetails{

	private static final long serialVersionUID = 1L;
	
	private LoginVO login;
	
	public AllbaroUser(LoginVO login) {
		super();
		this.login = login;
	}
	
	@Override
	public String getUsername() {
		return login.getId();
	}
	
	@Override
	public String getPassword() {
		return login.getPassword();
	}
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// TODO: 권한 정보를 데이터베이스에서 조회하여 반환하도록 구현 필요
		// 임시로 빈 컬렉션 반환
		return java.util.Collections.emptyList();
	}
	
	// LoginVO 접근을 위한 getter
	public LoginVO getLogin() {
		return login;
	}

}
