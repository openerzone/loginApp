<<<<<<< HEAD
# LoginApp - Spring Security 인증 모듈 적용 가이드

전자정부 프레임워크 기반의 Spring Security 인증 모듈 적용 가이드입니다.

## 📋 목차
- [프로젝트 개요](#프로젝트-개요)
- [모듈 구조](#모듈-구조)
- [koroad-auth 모듈 적용 가이드](#koroad-auth-모듈-적용-가이드)
  - [1. Maven 의존성 추가](#1-maven-의존성-추가)
  - [2. web.xml 설정](#2-webxml-설정)
  - [3. Spring Context 설정](#3-spring-context-설정)
  - [4. Properties 파일 설정](#4-properties-파일-설정)
  - [5. UserDetailsService 구현](#5-userdetailsservice-구현)
- [데이터베이스 설정](#데이터베이스-설정)
- [비밀번호 암호화](#비밀번호-암호화)
- [커스터마이징](#커스터마이징)

---

## 프로젝트 개요

본 프로젝트는 **전자정부 표준프레임워크 4.2.0** 기반의 멀티 모듈 프로젝트로, Spring Security를 활용한 인증/인가 기능을 제공합니다.

### 기술 스택
- **Spring Framework**: 5.3.27
- **Spring Security**: 5.8.3
- **eGovFrame**: 4.2.0
- **MyBatis**: 3.5.7
- **Thymeleaf**: 3.0.15
- **Java**: 8+

---

## 모듈 구조

```
loginApp/
├── koroad-auth/              # 인증 모듈 (재사용 가능한 라이브러리)
│   ├── src/main/java/
│   │   └── kr.or.koroad.auth/
│   │       ├── handler/      # 인증 성공/실패 핸들러
│   │       ├── model/        # 도메인 모델 (Account)
│   │       ├── security/     # Security 관련 클래스
│   │       ├── service/      # UserDetailsService 구현
│   │       ├── util/         # 암호화 유틸리티
│   │       └── web/          # 로그인 컨트롤러
│   └── src/main/resources/
│       ├── META-INF/
│       │   ├── auth/         # Properties 파일
│       │   ├── mappers/      # MyBatis Mapper XML
│       │   ├── spring/       # Spring 설정 파일
│       │   └── templates/    # Thymeleaf 템플릿
│       └── ...
│
└── koroad-web/               # 웹 애플리케이션 모듈
    ├── src/main/java/
    │   └── egovframework/
    │       └── com/auth/     # koroad-auth 확장 구현
    └── src/main/resources/
        └── auth/             # Properties 오버라이드
```

---

## koroad-auth 모듈 적용 가이드

### 1. Maven 의존성 추가

#### 1.1 부모 POM (pom.xml)

멀티 모듈 프로젝트의 루트 `pom.xml`에서 공통 버전을 관리합니다.

```xml
<properties>
    <spring.maven.artifact.version>5.3.27</spring.maven.artifact.version>
    <org.egovframe.rte.version>4.2.0</org.egovframe.rte.version>
    <spring.security.version>5.8.3</spring.security.version>
    <thymeleaf.version>3.0.15.RELEASE</thymeleaf.version>
</properties>

<modules>
    <module>koroad-auth</module>
    <module>koroad-web</module>
</modules>

<dependencyManagement>
    <dependencies>
        <!-- Spring Security -->
        <dependency>
            <groupId>org.springframework.security</groupId>
            <artifactId>spring-security-web</artifactId>
            <version>${spring.security.version}</version>
        </dependency>
        
        <dependency>
            <groupId>org.springframework.security</groupId>
            <artifactId>spring-security-config</artifactId>
            <version>${spring.security.version}</version>
        </dependency>
        
        <!-- Thymeleaf -->
        <dependency>
            <groupId>org.thymeleaf</groupId>
            <artifactId>thymeleaf</artifactId>
            <version>${thymeleaf.version}</version>
        </dependency>
        
        <dependency>
            <groupId>org.thymeleaf</groupId>
            <artifactId>thymeleaf-spring5</artifactId>
            <version>${thymeleaf.version}</version>
        </dependency>
    </dependencies>
</dependencyManagement>
```

#### 1.2 웹 모듈 POM (koroad-web/pom.xml)

웹 애플리케이션 모듈에서 koroad-auth 의존성을 추가합니다.

```xml
<dependencies>
    <!-- koroad-auth 모듈 의존성 -->
    <dependency>
        <groupId>egov</groupId>
        <artifactId>koroad-auth</artifactId>
        <version>1.0.2</version>
    </dependency>

    <!-- Spring Security -->
    <dependency>
        <groupId>org.springframework.security</groupId>
        <artifactId>spring-security-web</artifactId>
    </dependency>
    
    <dependency>
        <groupId>org.springframework.security</groupId>
        <artifactId>spring-security-config</artifactId>
    </dependency>
    
    <!-- 기타 의존성... -->
</dependencies>
```

---

### 2. web.xml 설정

`koroad-web/src/main/webapp/WEB-INF/web.xml`에 Spring Security 필터와 Context 설정을 추가합니다.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee 
         http://xmlns.jcp.org/xml/ns/javaee/web-app_3_1.xsd"
         version="3.1">
    
    <display-name>egovframework.sht</display-name>
    
    <!-- 1. Spring Security Filter -->
    <filter>
        <filter-name>springSecurityFilterChain</filter-name>
        <filter-class>org.springframework.web.filter.DelegatingFilterProxy</filter-class>
    </filter>
    <filter-mapping>
        <filter-name>springSecurityFilterChain</filter-name>
        <url-pattern>/*</url-pattern>
    </filter-mapping>
    
    <!-- 2. Character Encoding Filter -->
    <filter>
        <filter-name>encodingFilter</filter-name>
        <filter-class>org.springframework.web.filter.CharacterEncodingFilter</filter-class>
        <init-param>
            <param-name>encoding</param-name>
            <param-value>utf-8</param-value>
        </init-param>
    </filter>
    <filter-mapping>
        <filter-name>encodingFilter</filter-name>
        <url-pattern>*.do</url-pattern>
    </filter-mapping>
    <filter-mapping>
        <filter-name>encodingFilter</filter-name>
        <url-pattern>/auth/*</url-pattern>
    </filter-mapping>
    
    <!-- 3. Root ApplicationContext 설정 -->
    <context-param>
        <param-name>contextConfigLocation</param-name>
        <param-value>
            <!-- 기존 eGovFrame Context -->
            classpath*:egovframework/spring/com/context-*.xml
            
            <!-- koroad-auth 모듈 Context (필수) -->
            classpath*:META-INF/spring/koroad-auth-context.xml
            classpath*:META-INF/spring/koroad-auth-security.xml
        </param-value>
    </context-param>
    
    <listener>
        <listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
    </listener>

    <!-- 4. 인증 서블릿 (auth) -->
    <servlet>
        <servlet-name>auth</servlet-name>
        <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
        <init-param>
            <param-name>contextConfigLocation</param-name>
            <param-value>
                classpath*:META-INF/spring/koroad-auth-mvc.xml
            </param-value>
        </init-param>
        <load-on-startup>1</load-on-startup>
    </servlet>
    <servlet-mapping>
        <servlet-name>auth</servlet-name>
        <url-pattern>/auth/*</url-pattern>
    </servlet-mapping>

    <!-- 5. 업무 서블릿 (action) -->
    <servlet>
        <servlet-name>action</servlet-name>
        <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
        <init-param>
            <param-name>contextConfigLocation</param-name>
            <param-value>
                /WEB-INF/config/egovframework/springmvc/*.xml
            </param-value>
        </init-param>
        <load-on-startup>2</load-on-startup>
    </servlet>
    <servlet-mapping>
        <servlet-name>action</servlet-name>
        <url-pattern>*.do</url-pattern>
    </servlet-mapping>
    
    <session-config>
        <session-timeout>600</session-timeout>
    </session-config>
    
</web-app>
```

**핵심 포인트:**
- `springSecurityFilterChain` 필터는 **가장 먼저** 선언되어야 합니다.
- `contextConfigLocation`에 koroad-auth 모듈의 설정 파일을 포함시킵니다.
- 인증 서블릿(`/auth/*`)과 업무 서블릿(`*.do`)을 분리합니다.

---

### 3. Spring Context 설정

#### 3.1 koroad-auth 모듈의 기본 설정 파일

koroad-auth 모듈은 다음 설정 파일들을 제공합니다:

| 파일명 | 역할 | 위치 |
|--------|------|------|
| `koroad-auth-context.xml` | Root Context 설정 (Service, Repository 스캔) | `classpath:META-INF/spring/` |
| `koroad-auth-security.xml` | Spring Security 설정 | `classpath:META-INF/spring/` |
| `koroad-auth-mvc.xml` | MVC Context 설정 (Controller, Thymeleaf) | `classpath:META-INF/spring/` |
| `koroad-auth-datasource.xml` | DataSource 및 MyBatis 설정 | `classpath:META-INF/spring/` |

#### 3.2 koroad-auth-context.xml 구조

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="...">

    <!-- DataSource 및 MyBatis 설정 import -->
    <import resource="koroad-auth-datasource.xml"/>

    <!-- Service, Repository, Component 스캔 -->
    <context:component-scan base-package="kr.or.koroad.auth">
        <context:include-filter type="annotation" 
                                expression="org.springframework.stereotype.Service"/>
        <context:include-filter type="annotation" 
                                expression="org.springframework.stereotype.Repository"/>
        <context:include-filter type="annotation" 
                                expression="org.springframework.stereotype.Component"/>
        <context:exclude-filter type="annotation" 
                                expression="org.springframework.stereotype.Controller"/>
    </context:component-scan>

    <!-- Property Placeholder 설정 -->
    <bean id="authPropertyConfigurer"
          class="org.springframework.beans.factory.config.PropertyPlaceholderConfigurer">
        <property name="locations">
            <list>
                <!-- 1. koroad-auth 모듈의 기본 properties -->
                <value>classpath:/META-INF/auth/koroad-auth-context.properties</value>
                <!-- 2. koroad-web에서 오버라이드 (우선순위 높음) -->
                <value>classpath:/auth/koroad-auth-context.properties</value>
            </list>
        </property>
        <property name="ignoreResourceNotFound" value="true"/>
        <property name="ignoreUnresolvablePlaceholders" value="true"/>
        <property name="order" value="1"/>
    </bean>
</beans>
```

#### 3.3 koroad-auth-security.xml 구조

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:security="http://www.springframework.org/schema/security"
       xsi:schemaLocation="...">

    <!-- Security Configuration -->
    <security:http auto-config="true" use-expressions="true">
        <!-- 로그인 설정 -->
        <security:form-login 
            login-page="/auth/login"
            authentication-failure-url="/auth/login?error=true"
            username-parameter="username"
            password-parameter="password"
            authentication-success-handler-ref="koroadAuthenticationSuccessHandler" />

        <!-- 로그아웃 설정 -->
        <security:logout
            logout-url="/auth/logout"
            logout-success-url="/auth/login?logout" />

        <!-- URL 접근 권한 설정 -->
        <security:intercept-url pattern="/auth/login" access="permitAll()" />
        <security:intercept-url pattern="/auth/logout" access="isAuthenticated()" />
        <security:intercept-url pattern="/css/**" access="permitAll()" />
        <security:intercept-url pattern="/js/**" access="permitAll()" />
        <security:intercept-url pattern="/images/**" access="permitAll()" />
        <security:intercept-url pattern="/**" access="permitAll()" />

        <!-- CSRF 보호 활성화 -->
        <security:csrf />
    </security:http>

    <!-- Authentication Manager -->
    <security:authentication-manager>
        <security:authentication-provider ref="koroadAuthenticationProvider" />
    </security:authentication-manager>

    <!-- Password Encoder -->
    <bean id="passwordEncoder" class="kr.or.koroad.auth.util.EgovPasswordEncoder" />

    <!-- Custom AuthenticationProvider (ID를 salt로 사용) -->
    <bean id="koroadAuthenticationProvider" 
          class="kr.or.koroad.auth.security.KoroadDaoAuthenticationProvider">
        <constructor-arg ref="passwordEncoder" />
        <property name="userDetailsService" ref="allbaroUserDetailsService" />
    </bean>
</beans>
```

#### 3.4 koroad-auth-datasource.xml 구조

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:mybatis="http://mybatis.org/schema/mybatis-spring"
       xsi:schemaLocation="...">

    <!-- DataSource 설정 (MySQL) -->
    <bean id="dataSource-auth" 
          class="org.apache.commons.dbcp2.BasicDataSource" 
          destroy-method="close">
        <property name="driverClassName" value="net.sf.log4jdbc.DriverSpy"/>
        <property name="url" value="jdbc:log4jdbc:mysql://127.0.0.1:3306/pst" />
        <property name="username" value="root"/>
        <property name="password" value="1234"/>
    </bean>

    <!-- SqlSessionFactory 설정 -->
    <bean id="sqlSessionFactory-auth" 
          class="org.mybatis.spring.SqlSessionFactoryBean">
        <property name="dataSource" ref="dataSource-auth" />
        <property name="mapperLocations" value="classpath:/META-INF/mappers/**/*.xml" />
        <property name="typeAliasesPackage" value="kr.or.koroad.auth.model" />
    </bean>

    <!-- MyBatis Mapper 스캔 -->
    <mybatis:scan base-package="kr.or.koroad.auth.service.mapper" 
                  annotation="org.apache.ibatis.annotations.Mapper"
                  factory-ref="sqlSessionFactory-auth" />
</beans>
```

---

### 4. Properties 파일 설정

#### 4.1 Properties 오버라이드 메커니즘

koroad-auth 모듈은 **2단계 Properties 로딩**을 지원합니다:

1. **기본 설정**: `koroad-auth/src/main/resources/META-INF/auth/*.properties`
2. **오버라이드**: `koroad-web/src/main/resources/auth/*.properties` (우선순위 높음)

#### 4.2 koroad-auth-context.properties

**koroad-auth 모듈의 기본값** (`META-INF/auth/koroad-auth-context.properties`):
```properties
# 사이트 제목
auth.site.title=올바로

# 로그인 성공 후 이동 경로
auth.success.redirect.path=/cmm/main/mainPage.do
```

**koroad-web에서 오버라이드** (`auth/koroad-auth-context.properties`):
```properties
# 사이트 제목 오버라이드
auth.site.title=통합경영지원

# 로그인 성공 후 이동 경로 오버라이드
auth.success.redirect.path=/main/dashboard.do
```

#### 4.3 koroad-auth-mvc.properties

로그인 화면 텍스트 커스터마이징:

```properties
# 로그인 화면 텍스트
auth.login.welcome=환영합니다
auth.login.username=사용자명
auth.login.password=비밀번호
auth.login.submit=로그인

# 에러 메시지
auth.login.error.invalid=사용자명 또는 비밀번호가 올바르지 않습니다.
auth.login.error.expired=세션이 만료되었습니다. 다시 로그인해주세요.

# 성공 메시지
auth.login.success.logout=성공적으로 로그아웃되었습니다.
```

---

### 5. UserDetailsService 구현

#### 5.1 AbstractKoroadUserDetailsService 확장

koroad-auth 모듈은 `AbstractKoroadUserDetailsService`를 제공합니다. 이를 확장하여 사용자 정의 UserDetailsService를 구현합니다.

**koroad-web 모듈에서 구현** (`egovframework/com/auth/AllbaroUserDetailsService.java`):

```java
package egovframework.com.auth;

import java.util.Optional;
import javax.annotation.Resource;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import egovframework.com.cmm.LoginVO;
import egovframework.let.uat.uia.service.impl.LoginDAO;
import kr.or.koroad.auth.service.AbstractKoroadUserDetailsService;

@Service("allbaroUserDetailsService")
public class AllbaroUserDetailsService extends AbstractKoroadUserDetailsService {

    @Resource(name = "loginDAO")
    private LoginDAO loginDAO;
    
    @Override
    public Optional<UserDetails> loadSiteUserByUsername(String username) {
        
        LoginVO vo = new LoginVO();
        vo.setId(username);
        
        try {
            // LoginDAO에서 사용자 조회 (Optional 반환)
            Optional<LoginVO> loginOptional = loginDAO.searchId(vo);
            
            // Optional이 값을 가지고 있으면 AllbaroUser로 변환
            return loginOptional.map(login -> {
                System.out.println("Login Found: " + login.getId() + " / " + login.getName());
                return (UserDetails) new AllbaroUser(login);
            });
            
        } catch (Exception e) {
            System.err.println("Error loading user: " + username);
            e.printStackTrace();
            return Optional.empty();
        }
    }
}
```

#### 5.2 UserDetails 구현 (AllbaroUser)

```java
package egovframework.com.auth;

import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;
import egovframework.com.cmm.LoginVO;
import kr.or.koroad.auth.service.KoroadUserDetails;

public class AllbaroUser extends KoroadUserDetails {

    private static final long serialVersionUID = 1L;
    
    private LoginVO login;
    
    public AllbaroUser(LoginVO login) {
        super();
        this.login = login;
    }
    
    @Override
    public String getUsername() {
        return login.getId();
    }
    
    @Override
    public String getPassword() {
        return login.getPassword();
    }
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // TODO: 권한 정보를 데이터베이스에서 조회하여 반환
        return java.util.Collections.emptyList();
    }
    
    // LoginVO 접근을 위한 getter
    public LoginVO getLogin() {
        return login;
    }
}
```

#### 5.3 DAO에서 Optional 지원

```java
package egovframework.let.uat.uia.service.impl;

import java.util.Optional;
import egovframework.com.cmm.LoginVO;
import org.egovframe.rte.psl.dataaccess.EgovAbstractMapper;
import org.springframework.stereotype.Repository;

@Repository("loginDAO")
public class LoginDAO extends EgovAbstractMapper {

    /**
     * 아이디를 찾는다.
     * @param vo LoginVO
     * @return Optional<LoginVO>
     * @exception Exception
     */
    public Optional<LoginVO> searchId(LoginVO vo) throws Exception {
        return Optional.ofNullable((LoginVO) selectOne("loginDAO.searchId", vo));
    }
}
```

---

## 데이터베이스 설정

### 회원 테이블 구조

koroad-auth 모듈은 기본적으로 다음 테이블 구조를 사용합니다:

```sql
-- 일반 회원 테이블 (전자정부 표준 테이블)
CREATE TABLE LETTNGNRLMBER (
    MBER_ID VARCHAR(20) NOT NULL,          -- 회원 ID
    PASSWORD VARCHAR(200) NOT NULL,         -- 비밀번호 (암호화)
    MBER_NM VARCHAR(50),                    -- 회원명
    MBER_EMAIL_ADRES VARCHAR(50),          -- 이메일
    GROUP_ID VARCHAR(20),                   -- 그룹 ID
    MBER_STTUS VARCHAR(15),                -- 회원 상태
    PRIMARY KEY (MBER_ID)
);

-- 샘플 데이터 삽입 (비밀번호는 EgovFileScrty.encryptPassword로 암호화)
-- 비밀번호: 1234
INSERT INTO LETTNGNRLMBER (MBER_ID, PASSWORD, MBER_NM, MBER_EMAIL_ADRES, GROUP_ID, MBER_STTUS)
VALUES ('admin', 'jA0WCF9xPPZNnHcVttCxryI9bcf+PWKDzDVfpaTqRuc=', '관리자', 'admin@example.com', 'GROUP_00000000000000', 'P');
```

### MyBatis Mapper 설정

**AccountMapper.xml** (`koroad-auth/src/main/resources/META-INF/mappers/`):

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="kr.or.koroad.auth.service.mapper.AccountMapper">

    <select id="selectAccountById" resultType="kr.or.koroad.auth.model.Account">
        SELECT 
            MBER_ID as mberId,
            PASSWORD as password,
            MBER_NM as mberNm
        FROM LETTNGNRLMBER 
        WHERE MBER_ID = #{id}
    </select>
    
</mapper>
```

---

## 비밀번호 암호화

### EgovPasswordEncoder

koroad-auth 모듈은 전자정부 프레임워크의 `EgovFileScrty.encryptPassword()` 메소드를 사용하는 `EgovPasswordEncoder`를 제공합니다.

#### 암호화 방식

1. **SHA-256 해시**: 단방향 암호화 (복호화 불가)
2. **Salt 지원**: 사용자 ID를 salt로 사용하여 보안 강화
3. **Base64 인코딩**: 해시 결과를 Base64로 인코딩

#### 사용 방법

**비밀번호 암호화 (테스트 코드)**:

```java
import kr.or.koroad.auth.util.EgovFileScrty;

// 방법 1: Salt 없이 암호화 (deprecated)
String encrypted = EgovFileScrty.encryptPassword("1234");

// 방법 2: 사용자 ID를 Salt로 사용 (권장)
String encrypted = EgovFileScrty.encryptPassword("1234", "admin");
// 결과: jA0WCF9xPPZNnHcVttCxryI9bcf+PWKDzDVfpaTqRuc=
```

**Spring Security 설정**:

```xml
<!-- EgovPasswordEncoder 사용 -->
<bean id="passwordEncoder" class="kr.or.koroad.auth.util.EgovPasswordEncoder" />

<!-- Custom AuthenticationProvider (ID를 salt로 사용) -->
<bean id="koroadAuthenticationProvider" 
      class="kr.or.koroad.auth.security.KoroadDaoAuthenticationProvider">
    <constructor-arg ref="passwordEncoder" />
    <property name="userDetailsService" ref="allbaroUserDetailsService" />
</bean>
```

### KoroadDaoAuthenticationProvider

사용자 ID를 salt로 사용하는 비밀번호 검증을 위한 Custom AuthenticationProvider입니다:

```java
package kr.or.koroad.auth.security;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

import kr.or.koroad.auth.util.EgovPasswordEncoder;

public class KoroadDaoAuthenticationProvider extends DaoAuthenticationProvider {

    private EgovPasswordEncoder egovPasswordEncoder;
    
    public KoroadDaoAuthenticationProvider(EgovPasswordEncoder egovPasswordEncoder) {
        this.egovPasswordEncoder = egovPasswordEncoder;
        super.setPasswordEncoder(egovPasswordEncoder);
    }
    
    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails,
            UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        
        if (authentication.getCredentials() == null) {
            throw new BadCredentialsException("Bad credentials");
        }

        String presentedPassword = authentication.getCredentials().toString();
        String username = userDetails.getUsername();
        
        // EgovPasswordEncoder의 matchesWithSalt 메소드 사용
        // username(ID)을 salt로 사용하여 검증
        if (!egovPasswordEncoder.matchesWithSalt(presentedPassword, 
                                                  userDetails.getPassword(), 
                                                  username)) {
            throw new BadCredentialsException("Bad credentials");
        }
    }
}
```

---

## 커스터마이징

### 1. 로그인 성공 후 처리

`KoroadAuthenticationSuccessHandler`를 확장하여 로그인 성공 시 추가 처리를 구현할 수 있습니다.

```java
package egovframework.com.auth;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.security.core.Authentication;
import kr.or.koroad.auth.handler.KoroadAuthenticationSuccessHandler;

@Component("customSuccessHandler")
public class CustomAuthenticationSuccessHandler extends KoroadAuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, 
                                        HttpServletResponse response,
                                        Authentication authentication) 
            throws IOException, ServletException {
        
        // 추가 처리: 로그인 이력 저장, 세션 정보 설정 등
        HttpSession session = request.getSession();
        session.setAttribute("loginTime", System.currentTimeMillis());
        
        // 부모 클래스의 로직 실행 (리다이렉트)
        super.onAuthenticationSuccess(request, response, authentication);
    }
}
```

### 2. 로그인 화면 커스터마이징

Thymeleaf 템플릿을 수정하여 로그인 화면을 커스터마이징할 수 있습니다.

**위치**: `koroad-auth/src/main/resources/META-INF/templates/auth/login.html`

```html
<!DOCTYPE html>
<html lang="ko" xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title th:text="'로그인 : ' + ${title}">로그인</title>
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" 
          rel="stylesheet">
</head>
<body>
    <div class="login-container">
        <h2 th:text="${@environment.getProperty('auth.login.title', '로그인')}">로그인</h2>
        
        <!-- 에러 메시지 -->
        <div th:if="${errorMessage}" class="alert alert-danger">
            <span th:text="${errorMessage}"></span>
        </div>
        
        <!-- 로그인 폼 -->
        <form th:action="@{/login}" method="post">
            <input type="text" name="username" 
                   th:placeholder="${@environment.getProperty('auth.login.username', '사용자명')}"
                   required>
            <input type="password" name="password" 
                   th:placeholder="${@environment.getProperty('auth.login.password', '비밀번호')}"
                   required>
            
            <!-- CSRF 토큰 -->
            <input type="hidden" th:name="${_csrf.parameterName}" 
                   th:value="${_csrf.token}" />
            
            <button type="submit" 
                    th:text="${@environment.getProperty('auth.login.submit', '로그인')}">
                로그인
            </button>
        </form>
    </div>
</body>
</html>
```

### 3. URL 패턴 변경

로그인 URL 패턴을 변경하려면 `koroad-auth-security.xml`을 수정합니다:

```xml
<security:form-login 
    login-page="/custom/login"
    authentication-failure-url="/custom/login?error=true"
    username-parameter="userId"
    password-parameter="userPw"
    authentication-success-handler-ref="koroadAuthenticationSuccessHandler" />
```

### 4. 권한 기반 접근 제어

URL 패턴에 따라 접근 권한을 설정할 수 있습니다:

```xml
<!-- 로그인 필수 -->
<security:intercept-url pattern="/admin/**" access="hasRole('ROLE_ADMIN')" />
<security:intercept-url pattern="/user/**" access="isAuthenticated()" />

<!-- 특정 권한 필요 -->
<security:intercept-url pattern="/manager/**" 
                        access="hasAnyRole('ROLE_ADMIN', 'ROLE_MANAGER')" />

<!-- 모두 접근 가능 -->
<security:intercept-url pattern="/public/**" access="permitAll()" />
```

---

## 빌드 및 실행

### Maven 빌드

```bash
# 프로젝트 루트에서 전체 빌드
mvn clean install

# koroad-auth 모듈만 빌드
cd koroad-auth
mvn clean install

# koroad-web 모듈만 빌드
cd koroad-web
mvn clean package
```

### 실행

```bash
# Tomcat에 배포 후 실행
cp koroad-web/target/koroad-web.war $TOMCAT_HOME/webapps/

# 또는 Maven 플러그인으로 실행
mvn tomcat7:run
```

### 접속

- **로그인 페이지**: http://localhost:8080/auth/login
- **메인 페이지**: http://localhost:8080/cmm/main/mainPage.do

---

## 트러블슈팅

### 1. NoSuchBeanDefinitionException: MemberMapper

**증상**: `MemberMapper` 또는 `AccountMapper` 빈을 찾을 수 없음

**해결**:
- `koroad-auth-datasource.xml`이 `koroad-auth-context.xml`에 import되어 있는지 확인
- `SqlSessionFactory` 빈이 정의되어 있는지 확인
- MyBatis 스캔 설정이 올바른지 확인

### 2. CSRF Token 오류 (403 Forbidden)

**증상**: 로그인 또는 로그아웃 시 403 에러 발생

**해결**:
- 폼에 CSRF 토큰이 포함되어 있는지 확인
  ```html
  <input type="hidden" th:name="${_csrf.parameterName}" th:value="${_csrf.token}" />
  ```
- 또는 Spring Security 태그 사용
  ```html
  <sec:csrfInput/>
  ```

### 3. 비밀번호 불일치

**증상**: 정확한 비밀번호를 입력해도 로그인 실패

**해결**:
- DB에 저장된 비밀번호가 `EgovFileScrty.encryptPassword(password, id)`로 암호화되었는지 확인
- `KoroadDaoAuthenticationProvider`가 제대로 설정되어 있는지 확인
- Salt(사용자 ID)가 올바르게 전달되는지 확인

### 4. Properties 값이 적용되지 않음

**증상**: Properties 파일의 값이 화면에 표시되지 않음

**해결**:
- Properties 파일 경로 확인: `classpath:/auth/koroad-auth-context.properties`
- `PropertyPlaceholderConfigurer`의 `order` 설정 확인
- `ignoreResourceNotFound`가 `true`로 설정되어 있는지 확인

---

## 라이선스

본 프로젝트는 전자정부 표준프레임워크의 라이선스 정책을 따릅니다.

---

## 참고 자료

- [전자정부 표준프레임워크 포털](https://www.egovframe.go.kr/)
- [Spring Security 공식 문서](https://docs.spring.io/spring-security/reference/)
- [MyBatis 공식 문서](https://mybatis.org/mybatis-3/)
- [Thymeleaf 공식 문서](https://www.thymeleaf.org/)

---

**문서 버전**: 1.0.0  
**최종 수정일**: 2025-10-11
=======
# loginApp
로그인 모듈 테스트

## koroad-auth 모듈
>>>>>>> 202af5ed1606c35978d3a4782c7ef18c5fcd66c4
