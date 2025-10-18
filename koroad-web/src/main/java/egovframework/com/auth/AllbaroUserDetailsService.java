package egovframework.com.auth;

import java.util.Optional;

import javax.annotation.Resource;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import egovframework.com.cmm.LoginVO;
import egovframework.let.uat.uia.service.impl.LoginDAO;
import kr.or.koroad.auth.model.Account;
import kr.or.koroad.auth.service.AbstractKoroadUserDetailsService;

@Service
public class AllbaroUserDetailsService extends AbstractKoroadUserDetailsService {

	@Resource(name = "loginDAO")
	private LoginDAO loginDAO;
	
	@Override
	public UserDetails loadSiteUserByAccount(Account account) {
		
		LoginVO param = new LoginVO();
		param.setId(account.getMberId());
		
		// LoginDAO.searchId()가 Optional<LoginVO>를 반환
		Optional<LoginVO> optionalUser = loginDAO.searchId(param);
		
		// Optional이 값을 가지고 있으면 AllbaroUser로 변환
		return new AllbaroUser(account, optionalUser.get());
	}

}
