package kr.or.koroad.auth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import kr.or.koroad.auth.model.Account;
import kr.or.koroad.auth.service.mapper.AccountMapper;

public abstract class AbstractKoroadUserDetailsService implements UserDetailsService {

	@Autowired
	private AccountMapper accountMapper;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		//TODO ESB로 던져야 한다.
		Account account = accountMapper.selectAccountById(username).orElseThrow(() -> new UsernameNotFoundException("Not found loginId : " + username));
		
		
		AbstractKoroadUserDetails loadedUser = (AbstractKoroadUserDetails)loadSiteUserByAccount(account);
		
//		if (loadedUser.isOtpEnabled())
//			loadedUser.addAuthority("ROLE_2FA_PENDING");
		
		return loadedUser;
	}

	public abstract UserDetails loadSiteUserByAccount(Account account);
	
	/**
	 * OTP 검증 후 최종 권한을 로드할 때 사용하는 메서드
	 * ROLE_2FA_PENDING 권한을 추가하지 않음
	 */
	public UserDetails loadUserByUsernameWithoutOtpPending(String username) throws UsernameNotFoundException {
		Account account = accountMapper.selectAccountById(username).orElseThrow(() -> new UsernameNotFoundException("Not found loginId : " + username));
		return loadSiteUserByAccount(account);
	}
	
}
