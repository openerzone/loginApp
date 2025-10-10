package kr.or.koroad.auth.util;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

public class EgovPasswordEncoderTest {

	@Test
	public void test() throws Exception {
//		EgovPasswordEncoder encoder = new EgovPasswordEncoder();
//		EgovFileScrty encoder = new EgovFileScrty();
		System.out.println(EgovFileScrty.encryptPassword("1", "admin"));
	}

}
