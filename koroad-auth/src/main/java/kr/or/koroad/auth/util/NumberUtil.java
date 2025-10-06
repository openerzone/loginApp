package kr.or.koroad.auth.util;

import java.security.SecureRandom;

public class NumberUtil {
	
    public static int getRandomNum(int startNum, int endNum) {
		int randomNum = 0;

		// 랜덤 객체 생성
		SecureRandom rnd = new SecureRandom();

		do {
			// 종료숫자내에서 랜덤 숫자를 발생시킨다.
			randomNum = rnd.nextInt(endNum + 1);
		} while (randomNum < startNum); // 랜덤 숫자가 시작숫자보다 작을경우 다시 랜덤숫자를 발생시킨다.

		return randomNum;
    }
}
