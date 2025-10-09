package kr.or.koroad.auth.service.mapper;

import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;

import kr.or.koroad.auth.model.Member;

@Mapper
public interface MemberMapper {

	public Optional<Member> selectMemberById(String id);
}
