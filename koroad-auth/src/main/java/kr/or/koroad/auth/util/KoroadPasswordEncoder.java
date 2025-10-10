package kr.or.koroad.auth.util;

import org.jasypt.util.password.ConfigurablePasswordEncryptor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Jasypt의 ConfigurablePasswordEncryptor를 사용하여 SHA-256으로 비밀번호를 처리하는
 * Spring Security의 PasswordEncoder 구현체입니다.
 */
public class KoroadPasswordEncoder implements PasswordEncoder {

    private final ConfigurablePasswordEncryptor passwordEncryptor;

    public KoroadPasswordEncoder() {
        this.passwordEncryptor = new ConfigurablePasswordEncryptor();
        // 암호화 알고리즘을 SHA-256으로 설정합니다.
        this.passwordEncryptor.setAlgorithm("SHA-256");
        // 평문 다이제스트가 아닌 Base64로 인코딩된 문자열을 사용하도록 설정합니다.
//        this.passwordEncryptor.setStringOutputType("base64");
        this.passwordEncryptor.setPlainDigest(true);
    }

    /**
     * 주어진 원본 비밀번호를 SHA-256으로 암호화합니다.
     *
     * @param rawPassword 암호화할 원본 비밀번호
     * @return 암호화된 비밀번호 문자열
     */
    @Override
    public String encode(CharSequence rawPassword) {
        if (rawPassword == null) {
            throw new IllegalArgumentException("rawPassword cannot be null");
        }
        return passwordEncryptor.encryptPassword(rawPassword.toString());
    }

    /**
     * 원본 비밀번호와 암호화된 비밀번호가 일치하는지 확인합니다.
     *
     * @param rawPassword     검증할 원본 비밀번호
     * @param encodedPassword 데이터베이스 등에 저장된 암호화된 비밀번호
     * @return 일치하면 true, 그렇지 않으면 false
     */
    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        if (rawPassword == null || encodedPassword == null) {
            return false;
        }
        return passwordEncryptor.checkPassword(rawPassword.toString(), encodedPassword);
    }
}
