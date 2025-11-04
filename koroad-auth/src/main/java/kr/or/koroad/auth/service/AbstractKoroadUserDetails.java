package kr.or.koroad.auth.service;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import kr.or.koroad.auth.model.Account;

public abstract class AbstractKoroadUserDetails implements UserDetails{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Account account;
	
	private Set<GrantedAuthority> authorities = new HashSet<>();
	
	protected AbstractKoroadUserDetails(Account account) {
		this.account = account;
	}
	
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return this.authorities;
	}
	// --- 권한 추가 메소드 (기존) ---
    public void addAuthority(GrantedAuthority authority) {
        this.authorities.add(authority);
    }
    
    public void addAuthority(String roleName) {
    	this.authorities.add(new SimpleGrantedAuthority(roleName));
    }
    
    // --- 권한 삭제 메소드 (새로 추가) 🚀 ---
    /**
     * 지정된 이름의 권한(Role)을 authorities Set에서 제거합니다.
     * @param roleName 제거할 권한 이름 (예: "ROLE_USER")
     * @return 성공적으로 제거했는지 여부
     */
    public boolean removeAuthority(String roleName) {
        // SimpleGrantedAuthority 객체를 생성하여 Set에서 제거
        return this.authorities.remove(new SimpleGrantedAuthority(roleName));
    }
    
	public String getUserId() {
		return account.getMberId();
	}
	@Override
	public final String getPassword() {
		return account.getPassword();
	}
	
	@Override
	public final String getUsername() {
		return account.getMberId();
	}

	public boolean isOtpEnabled() {
		return true;
	}
	
	public boolean isSsoEnabled() {
		return true;
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
		// Account의 비밀번호 만료일 체크
//		return account.isPasswordNonExpired();
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
