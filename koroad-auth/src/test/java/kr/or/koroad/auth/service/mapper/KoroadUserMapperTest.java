package kr.or.koroad.auth.service.mapper;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:test-context.xml"})
public class KoroadUserMapperTest {

	@Autowired
	private MemberMapper memberMapper;
	
	@Test
	public void testAssertMapper() {
		assertNotNull(memberMapper);
	}
	
	@Test
	public void testSelectById() {
		assertTrue(memberMapper.selectMemberById("admin").isPresent());
	}

}
