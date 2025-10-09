package kr.or.koroad.auth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import kr.or.koroad.auth.model.Member;
import kr.or.koroad.auth.security.KoroadUserDetails;
import kr.or.koroad.auth.service.mapper.MemberMapper;

public abstract class AbstractKoroadUserDetailsService implements UserDetailsService {

	@Autowired
	private MemberMapper memberMapper;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		Member member = memberMapper.selectMemberById(username).orElseThrow(() -> new UsernameNotFoundException("Not found loginId : " + username));
		KoroadUserDetails userDetails = new KoroadUserDetails(member);
		
		//TODO 응용단에서 username 으로 다시 조회를 하고 나온 객체와 Member 를 가지고 KoroadUserDetails 를 만들어야 한다??
		loadKoroadUserByUsername(username, userDetails);
		
		return userDetails;
	}

	public abstract void loadKoroadUserByUsername(String username, KoroadUserDetails userDetails);
	
}
