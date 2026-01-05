package com.nexsol.tpa.core.support.auth;

import com.nexsol.tpa.core.domain.AdminUser;
import com.nexsol.tpa.core.domain.LoginAdmin;
import com.nexsol.tpa.core.support.error.CoreException;
import com.nexsol.tpa.core.support.error.ErrorType;
import jakarta.servlet.http.HttpServletRequest;
import org.jspecify.annotations.Nullable;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class LoginAdminArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(LoginAdmin.class)
                && AdminUser.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public @Nullable Object resolveArgument(MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();

        // 1. Gateway에서 넘겨준 헤더 값 추출 (예: X-User-Id)
        Long userId = Long.valueOf(request.getHeader("X-User-Id"));
        String role = request.getHeader("X-Role"); // 필요시

        // 2. 헤더가 없으면 예외 발생 (Gateway를 통하지 않은 비정상 접근 차단)
        if (userId == null) {
            throw new CoreException(ErrorType.INVALID_REQUEST);
        }

        // 3. 컨트롤러에 주입할 객체 생성 및 반환
        return new AdminUser(userId, role);
    }

}
