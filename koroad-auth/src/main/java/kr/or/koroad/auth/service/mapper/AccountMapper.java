package kr.or.koroad.auth.service.mapper;

import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;

import kr.or.koroad.auth.model.Account;

@Mapper
public interface AccountMapper {

	public Optional<Account> selectAccountById(String id);
}
