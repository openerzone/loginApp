package kr.or.koroad.auth.service.mapper;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:test-context.xml"})
public class AccountMapperTest {

	@Autowired
	private AccountMapper accountMapper;
	
	@Test
	public void testAssertMapper() {
		assertNotNull(accountMapper);
	}
	
	@Test
	public void testSelectById() {
		assertTrue(accountMapper.selectAccountById("admin").isPresent());
	}

}
