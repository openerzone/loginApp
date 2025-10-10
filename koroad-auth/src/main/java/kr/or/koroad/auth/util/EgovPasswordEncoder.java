package kr.or.koroad.auth.util;

import java.security.MessageDigest;

import org.apache.commons.codec.binary.Base64;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * EgovFileScrty의 encryptPassword 메소드를 이용한 Spring Security PasswordEncoder 구현
 * SHA-256 해시 알고리즘을 사용하여 비밀번호를 암호화합니다.
 * 
 * @see EgovFileScrty#encryptPassword(String)
 */
public class EgovPasswordEncoder implements PasswordEncoder {

	/**
	 * 비밀번호를 암호화합니다.
	 * SHA-256 해시 알고리즘을 사용하여 평문 비밀번호를 암호화합니다.
	 * 
	 * @param rawPassword 평문 비밀번호
	 * @return Base64로 인코딩된 암호화된 비밀번호
	 */
	@Override
	public String encode(CharSequence rawPassword) {
		if (rawPassword == null) {
			return "";
		}
		
		try {
			byte[] plainText = rawPassword.toString().getBytes();
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] hashValue = md.digest(plainText);
			
			return new String(Base64.encodeBase64(hashValue));
		} catch (Exception e) {
			throw new RuntimeException("Failed to encode password", e);
		}
	}

	/**
	 * 평문 비밀번호와 암호화된 비밀번호가 일치하는지 검증합니다.
	 * 
	 * @param rawPassword 평문 비밀번호
	 * @param encodedPassword 암호화된 비밀번호 (Base64 인코딩)
	 * @return 일치 여부
	 */
	@Override
	public boolean matches(CharSequence rawPassword, String encodedPassword) {
		if (rawPassword == null) {
			return false;
		}
		
		try {
			// 입력된 평문 비밀번호를 암호화
			String hashedInput = encode(rawPassword);
			
			// 암호화된 결과와 저장된 암호화 비밀번호 비교
			return MessageDigest.isEqual(
				hashedInput.getBytes(), 
				encodedPassword.getBytes()
			);
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * Salt를 사용하여 비밀번호를 암호화합니다.
	 * 
	 * @param rawPassword 평문 비밀번호
	 * @param salt Salt (일반적으로 사용자 ID 사용)
	 * @return Base64로 인코딩된 암호화된 비밀번호
	 */
	public String encodeWithSalt(String rawPassword, String salt) {
		if (rawPassword == null) {
			return "";
		}
		
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.reset();
			md.update(salt.getBytes());
			byte[] hashValue = md.digest(rawPassword.getBytes());
			
			return new String(Base64.encodeBase64(hashValue));
		} catch (Exception e) {
			throw new RuntimeException("Failed to encode password with salt", e);
		}
	}
	
	/**
	 * Salt를 사용하여 비밀번호를 검증합니다.
	 * 
	 * @param rawPassword 평문 비밀번호
	 * @param encodedPassword 암호화된 비밀번호
	 * @param salt Salt (일반적으로 사용자 ID 사용)
	 * @return 일치 여부
	 */
	public boolean matchesWithSalt(String rawPassword, String encodedPassword, String salt) {
		if (rawPassword == null) {
			return false;
		}
		
		try {
			String hashedInput = encodeWithSalt(rawPassword, salt);
			return MessageDigest.isEqual(
				hashedInput.getBytes(), 
				encodedPassword.getBytes()
			);
		} catch (Exception e) {
			return false;
		}
	}

}
