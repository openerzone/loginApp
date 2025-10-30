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
		
		if (loadedUser.isOtpEnabled())
			loadedUser.addAuthority("ROLE_2FA_PENDING");
		
		return loadedUser;
	}

	public abstract UserDetails loadSiteUserByAccount(Account account);
	
}
