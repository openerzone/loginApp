package kr.or.koroad.auth.service;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import kr.or.koroad.auth.model.Account;

public class KoroadUserDetails implements UserDetails{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Account account;
	
	public KoroadUserDetails() {};
	
	protected KoroadUserDetails(Account account) {
		this.account = account;
	}
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// TODO: 권한 정보를 데이터베이스에서 조회하여 반환하도록 구현 필요
		// 임시로 빈 컬렉션 반환
		return java.util.Collections.emptyList();
	}

	@Override
	public String getPassword() {
		return account.getPassword();
	}

	public String getUserId() {
		return account.getMberId();
	}
	
	@Override
	public String getUsername() {
		return account.getMberNm();
	}

	@Override
	public boolean isAccountNonExpired() {
		// 계정 만료 여부 (true = 만료되지 않음)
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		// 계정 잠김 여부 (true = 잠기지 않음)
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		// 자격증명 만료 여부 (true = 만료되지 않음)
		return true;
	}

	@Override
	public boolean isEnabled() {
		// 계정 활성화 여부 (true = 활성화)
		return true;
	}
	
	// Account 객체 접근을 위한 getter
	public Account getAccount() {
		return account;
	}

}
