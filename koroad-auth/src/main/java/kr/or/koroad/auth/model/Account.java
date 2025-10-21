package kr.or.koroad.auth.model;

import java.time.LocalDateTime;

public class Account {

	private String mberId;
	private String password;
	private String mberNm;
//	private LocalDateTime passwordExpiryDate; // 비밀번호 만료일
	
	public String getMberId() {
		return mberId;
	}
	public void setMberId(String mberId) {
		this.mberId = mberId;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getMberNm() {
		return mberNm;
	}
	public void setMberNm(String mberNm) {
		this.mberNm = mberNm;
	}
//	
//	public LocalDateTime getPasswordExpiryDate() {
//		return passwordExpiryDate;
//	}
//	
//	public void setPasswordExpiryDate(LocalDateTime passwordExpiryDate) {
//		this.passwordExpiryDate = passwordExpiryDate;
//	}
	
	/**
	 * 비밀번호 만료 여부 확인
	 * @return true: 만료되지 않음, false: 만료됨
	 */
//	public boolean isPasswordNonExpired() {
//		if (passwordExpiryDate == null) {
//			return true; // 만료일이 설정되지 않으면 만료되지 않은 것으로 간주
//		}
//		return LocalDateTime.now().isBefore(passwordExpiryDate);
//	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Account {" + 
				" mberId=" + mberId +
				" password=" + password +
				" mberNm=" + mberNm +
//				" passwordExpiryDate=" + passwordExpiryDate +
				"}";
	}
	
	
}
