package kr.or.koroad.auth.security;

import java.util.List;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

/**
 * Custome AuthenticationManager
 * 
 * 원래 AuthenticationManager 의 동작은 ProviderManager가 순서대로 각 Provider에게 인증을 위임
 * 인증 성공 시 첫 번째로 성공한 Provider에서 즉시 인증 종료 (이후 Provider는 호출되지 않음)
 * 인증 실패 시 다음 Provider로 순차적으로 위임
 * 
 * 그러나 해당 AuthenticationManager 은 ProviderManager가 순서대로 각 Provider에게 인증을 위임하여
 * 모든 AuthenticationProvider가 순차적으로 모두 성공해야 최종 인증 완료!!
 * 
 * @author opene
 *
 */
public class KoroadAuthenticationManager implements AuthenticationManager {

	private final List<AuthenticationProvider> providers;
	
	public KoroadAuthenticationManager(List<AuthenticationProvider> providers) {
		this.providers = providers;
	}
	
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Authentication current = authentication;
        Class<?> previousType = null;
        int maxIterations = 10; // 무한 루프 방지
        int iteration = 0;
        
        // 토큰 타입이 변경되면 다시 Provider 체인 실행
        while (iteration < maxIterations) {
            Class<?> currentType = current.getClass();
            
            System.out.println("=== While 루프 시작 (iteration: " + iteration + ", 토큰 타입: " + currentType.getSimpleName() + ") ===");
            
            // 토큰 타입이 변경되지 않았고 이미 한 번 실행했으면 종료
            if (previousType != null && previousType.equals(currentType)) {
                System.out.println("=== 토큰 타입 변경 없음, 종료 ===");
                break;
            }
            
            previousType = currentType;
            boolean providerExecuted = false;
            
            for (AuthenticationProvider provider : providers) {
                System.out.println("Provider 확인: " + provider.getClass().getSimpleName() + 
                                 ", supports(" + current.getClass().getSimpleName() + "): " + 
                                 provider.supports(current.getClass()));
                
                if (provider.supports(current.getClass())) {
                    System.out.println("=== Provider 실행: " + provider.getClass().getSimpleName() + 
                                     " (토큰 타입: " + current.getClass().getSimpleName() + ") ===");
                    
                    Authentication result = provider.authenticate(current);
                    
                    if (result == null || !result.isAuthenticated()) {
                        //TODO failureHandler를 어떻게 구현할 지 고민....
                        throw new AuthenticationException("Failed at: " + provider.getClass().getSimpleName()) {};
                    }
                    
                    current = result;
                    providerExecuted = true;
                    
                    // 토큰 타입이 변경되었으면 다시 처음부터 Provider 체인 실행
                    if (!current.getClass().equals(currentType)) {
                        System.out.println("=== 토큰 타입 변경: " + currentType.getSimpleName() + 
                                         " -> " + current.getClass().getSimpleName() + ", Provider 체인 재실행 ===");
                        break; // for 루프 탈출 후 while 루프로 재시작
                    }
                }
            }
            
            // Provider가 실행되지 않았으면 종료
            if (!providerExecuted) {
                System.out.println("=== Provider 실행 없음, 종료 ===");
                break;
            }
            
            iteration++;
        }
        
        System.out.println("=== 최종 인증 완료 (토큰 타입: " + current.getClass().getSimpleName() + ") ===");
        return current; 
	}

}
