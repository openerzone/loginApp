package kr.or.koroad.auth.service;

import java.util.Optional;

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
		KoroadUserDetails guest = new KoroadUserDetails(account);
		
		//응용단에서 username(사번) 으로 사용자 조회
		Optional<UserDetails> user = loadSiteUserByUserId(username);
		
		return (user.isEmpty())? guest: user.get();
	}

	public abstract Optional<UserDetails> loadSiteUserByUserId(String userId);
	
}
