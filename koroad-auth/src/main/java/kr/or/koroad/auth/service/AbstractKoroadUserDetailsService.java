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
		System.out.println("---------------------------------------");
		System.out.println(account);
		System.out.println("---------------------------------------");
		//KoroadUserDetails userDetails = new KoroadUserDetails(account);
		
		//TODO 응용단에서 username 으로 다시 조회를 하고 나온 객체와 Member 를 가지고 KoroadUserDetails 를 만들어야 한다??
		Optional<UserDetails> user = loadSiteUserByUsername(username);
		
		UserDetails userDetails = (user.isEmpty())? new KoroadUserDetails(account): user.get();
		
		return userDetails;
	}

	public abstract Optional<UserDetails> loadSiteUserByUsername(String username);
	
}
