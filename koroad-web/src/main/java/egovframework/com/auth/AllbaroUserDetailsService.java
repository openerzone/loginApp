package egovframework.com.auth;

import javax.annotation.Resource;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import egovframework.com.cmm.LoginVO;
import egovframework.let.uat.uia.service.impl.LoginDAO;
import kr.or.koroad.auth.security.KoroadUserDetails;
import kr.or.koroad.auth.service.AbstractKoroadUserDetailsService;

@Service
public class AllbaroUserDetailsService extends AbstractKoroadUserDetailsService {

	@Resource(name = "loginDAO")
	private LoginDAO loginDAO;
	
	@Override
	public void loadKoroadUserByUsername(String username, KoroadUserDetails userDetails) {
		
		LoginVO vo = new LoginVO();
		vo.setId(username);
		
		try {
			LoginVO login = loginDAO.searchId(vo);
			
//			if (login == null || "".equals(login.getId()))	// 해당 케이스는 임시계정일 때 
//				throw new UsernameNotFoundException("");
			
		} catch (Exception e) {
//			throw e;
		}
	}

}
