package kr.or.koroad.auth.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

/**
 * OTP(One-Time Password) 생성 및 검증 서비스
 */
@Service
public class OtpService {

	private static final int OTP_LENGTH = 6;
	private static final int OTP_VALID_MINUTES = 5; // OTP 유효 시간 (분)
	private static final SecureRandom random = new SecureRandom();
	
	// 사용자별 OTP 저장 (실제 운영에서는 Redis나 DB 사용 권장)
	private final Map<String, OtpData> otpStore = new ConcurrentHashMap<>();
	
	/**
	 * OTP 생성
	 * @param username 사용자명
	 * @return 생성된 6자리 OTP 코드
	 */
	public String generateOtp(String username) {
		// 6자리 랜덤 숫자 생성
		StringBuilder otp = new StringBuilder();
		for (int i = 0; i < OTP_LENGTH; i++) {
			otp.append(random.nextInt(10));
		}
		
		String otpCode = otp.toString();
		
		// OTP 저장 (만료 시간 포함)
		OtpData otpData = new OtpData(otpCode, LocalDateTime.now().plusMinutes(OTP_VALID_MINUTES));
		otpStore.put(username, otpData);
		
		// 실제 운영에서는 여기서 이메일/SMS 발송
		System.out.println("=== OTP 생성 ===");
		System.out.println("사용자: " + username);
		System.out.println("OTP 코드: " + otpCode);
		System.out.println("만료 시간: " + otpData.getExpiryTime());
		System.out.println("===============");
		
		return otpCode;
	}
	
	/**
	 * OTP 검증
	 * @param username 사용자명
	 * @param inputOtp 사용자가 입력한 OTP
	 * @return 검증 성공 여부
	 */
	public boolean verifyOtp(String username, String inputOtp) {
		OtpData otpData = otpStore.get(username);
		
		if (otpData == null) {
			System.out.println("OTP 검증 실패: OTP가 생성되지 않았습니다. (사용자: " + username + ")");
			return false;
		}
		
		// 만료 시간 확인
		if (LocalDateTime.now().isAfter(otpData.getExpiryTime())) {
			otpStore.remove(username); // 만료된 OTP 삭제
			System.out.println("OTP 검증 실패: OTP가 만료되었습니다. (사용자: " + username + ")");
			return false;
		}
		
		// OTP 코드 검증
		boolean isValid = otpData.getOtpCode().equals(inputOtp);
		
		if (isValid) {
			// 검증 성공 시 OTP 삭제 (일회용)
			otpStore.remove(username);
			System.out.println("OTP 검증 성공 (사용자: " + username + ")");
		} else {
			System.out.println("OTP 검증 실패: 코드가 일치하지 않습니다. (사용자: " + username + ", 입력: " + inputOtp + ")");
		}
		
		return isValid;
	}
	
	/**
	 * OTP 재전송 (기존 OTP 삭제 후 새로 생성)
	 * @param username 사용자명
	 * @return 새로 생성된 OTP 코드
	 */
	public String resendOtp(String username) {
		otpStore.remove(username); // 기존 OTP 삭제
		return generateOtp(username);
	}
	
	/**
	 * 특정 사용자의 OTP 조회 (테스트용)
	 * @param username 사용자명
	 * @return OTP 코드 (없으면 null)
	 */
	public String getOtpForTesting(String username) {
		OtpData otpData = otpStore.get(username);
		return otpData != null ? otpData.getOtpCode() : null;
	}
	
	/**
	 * OTP 데이터 클래스
	 */
	private static class OtpData {
		private final String otpCode;
		private final LocalDateTime expiryTime;
		
		public OtpData(String otpCode, LocalDateTime expiryTime) {
			this.otpCode = otpCode;
			this.expiryTime = expiryTime;
		}
		
		public String getOtpCode() {
			return otpCode;
		}
		
		public LocalDateTime getExpiryTime() {
			return expiryTime;
		}
	}
}

