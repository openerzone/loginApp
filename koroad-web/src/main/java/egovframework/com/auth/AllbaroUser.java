package egovframework.com.auth;

import java.util.Collection;
import java.util.Optional;

import org.springframework.security.core.GrantedAuthority;

import egovframework.com.cmm.LoginVO;
import kr.or.koroad.auth.model.Account;
import kr.or.koroad.auth.service.AbstractKoroadUserDetails;

public class AllbaroUser extends AbstractKoroadUserDetails{

	private static final long serialVersionUID = 1L;
	
	private LoginVO user;
	
	public AllbaroUser(Account account, Optional<LoginVO> siteUser) {
		super(account);
		this.user = (siteUser.isEmpty())? new LoginVO() : siteUser.get();
	}
	
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// TODO: 권한 정보를 데이터베이스에서 조회하여 반환하도록 구현 필요
		// 임시로 빈 컬렉션 반환
		return java.util.Collections.emptyList();
	}

	public String getEmail() {
		return this.user.getEmail();
	}
}
