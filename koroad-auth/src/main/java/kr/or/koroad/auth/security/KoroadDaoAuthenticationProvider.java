package kr.or.koroad.auth.security;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

import kr.or.koroad.auth.util.EgovPasswordEncoder;

/**
 * 사용자 ID를 salt로 사용하는 비밀번호 검증을 위한 Custom DaoAuthenticationProvider
 * EgovFileScrty.encryptPassword(password, id) 방식으로 암호화된 비밀번호를 검증합니다.
 */
public class KoroadDaoAuthenticationProvider extends DaoAuthenticationProvider {

	private EgovPasswordEncoder egovPasswordEncoder;
	
	public KoroadDaoAuthenticationProvider(EgovPasswordEncoder egovPasswordEncoder) {
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
		String username = userDetails.getUsername();
		
		// EgovPasswordEncoder의 matchesWithSalt 메소드를 사용하여 검증
		// username(ID)을 salt로 사용
		if (!egovPasswordEncoder.matchesWithSalt(presentedPassword, userDetails.getPassword(), username)) {
			this.logger.debug("Failed to authenticate since password does not match stored value");
			throw new BadCredentialsException(
				this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
		}
	}
}

