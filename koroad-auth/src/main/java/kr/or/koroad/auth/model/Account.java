package kr.or.koroad.auth.model;

public class Account {

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
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "Account {" + 
				" mberId=" + mberId +
				" password=" + password +
				" mberNm=" + mberNm +
				"}";
	}
	
	
}
