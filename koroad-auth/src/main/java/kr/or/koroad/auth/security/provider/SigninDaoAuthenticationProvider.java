package kr.or.koroad.auth.security.provider;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

import kr.or.koroad.auth.service.AbstractKoroadUserDetails;
import kr.or.koroad.auth.util.EgovPasswordEncoder;

public class SigninDaoAuthenticationProvider extends DaoAuthenticationProvider {

	private EgovPasswordEncoder egovPasswordEncoder;
	
	public SigninDaoAuthenticationProvider(EgovPasswordEncoder egovPasswordEncoder) {
		this.egovPasswordEncoder = egovPasswordEncoder;
		// 기본 PasswordEncoder도 설정 (필수)
		super.setPasswordEncoder(egovPasswordEncoder);
	}
	
	@Override
	protected void additionalAuthenticationChecks(UserDetails userDetails,
			UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
		
		if (authentication.getCredentials() == null) {
			this.logger.debug("Failed to authenticate since no credentials provided");
			throw new BadCredentialsException(
				this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
		}

		String presentedPassword = authentication.getCredentials().toString();
		String userId = ((AbstractKoroadUserDetails)userDetails).getUserId();
		
		// EgovPasswordEncoder의 matchesWithSalt 메소드를 사용하여 검증
		// username(ID)을 salt로 사용
		if (!egovPasswordEncoder.matchesWithSalt(presentedPassword, userDetails.getPassword(), userId)) {
			this.logger.debug("Failed to authenticate since password does not match stored value");
			throw new BadCredentialsException(
				this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
		}
	}
}

