package kr.or.koroad.auth.model;

public class Member {

	private String mberId;
	private String password;
	private String mberNm;
	
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
	
}
