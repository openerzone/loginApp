package kr.or.koroad.auth.security.provider;

import java.util.Collections;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import kr.or.koroad.auth.security.authentication.SigninAuthenticationToken;
import kr.or.koroad.auth.service.AbstractKoroadUserDetails;
import kr.or.koroad.auth.util.EgovPasswordEncoder;

public class SigninAuthenticationProvider implements AuthenticationProvider {

	private UserDetailsService userDetailsService;
	private EgovPasswordEncoder passwordEncoder;
	
	public SigninAuthenticationProvider(EgovPasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}
	
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		
		String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        AbstractKoroadUserDetails userDetails = (AbstractKoroadUserDetails)userDetailsService.loadUserByUsername(username); 
        
        // 비밀번호 검증
        if (!passwordEncoder.matchesWithSalt(password, userDetails.getPassword(), username)) {
            throw new BadCredentialsException("Invalid username or password.");
        }
            
        return new UsernamePasswordAuthenticationToken(
            userDetails,
            null,
            (userDetails.isOtpEnabled())? 
            		Collections.singletonList(new SimpleGrantedAuthority("ROLE_2FA_PENDING")):userDetails.getAuthorities()
        );

	}

	@Override
	public boolean supports(Class<?> authentication) {
		return //SigninAuthenticationToken.class.isAssignableFrom(authentication)
				UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
	}

	public void setUserDetailsService(UserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}
	
}
