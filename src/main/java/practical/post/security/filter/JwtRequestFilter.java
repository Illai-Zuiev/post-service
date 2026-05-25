package practical.post.security.filter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import practical.post.model.constants.ApiErrorMessage;
import practical.post.model.entity.User;
import practical.post.model.exceptions.NotFoundException;
import practical.post.repository.UserRepository;
import practical.post.security.CustomUserDetails;
import practical.post.security.JwtTokenProvider;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String LOGIN_PATH = "/auth/login";
    private static final String REGISTRATION_PATH = "/auth/registration";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        Optional<String> authHeader = Optional.ofNullable(request.getHeader(AUTHORIZATION_HEADER));
        String requestUrl = request.getRequestURI();

        if (authHeader.isPresent() && authHeader.get().startsWith(BEARER_PREFIX)) {
            String token = authHeader.get().substring(BEARER_PREFIX.length());

            try {
                if (!jwtTokenProvider.validateToken(token)) {
                    throw new ExpiredJwtException(null, null, ApiErrorMessage.EXPIRED_TOKEN.getMessage());
                }

                Optional<String> emailOptional = Optional.ofNullable(jwtTokenProvider.getEmailFromToken(token));

                if (emailOptional.isPresent()) {
                    if (SecurityContextHolder.getContext().getAuthentication() == null) {
                        User user = userRepository.findByEmailAndDeletedFalse(emailOptional.get()).orElseThrow(
                                () -> new NotFoundException(ApiErrorMessage.USER_NOT_FOUND_BY_EMAIL.getMessage(emailOptional.get()))
                        );
                        List<SimpleGrantedAuthority> authorities = jwtTokenProvider.getRoles(token).stream()
                                .map(SimpleGrantedAuthority::new)
                                .toList();

                        CustomUserDetails customUserDetails = new CustomUserDetails(user, authorities);
                        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(customUserDetails, token, authorities);
                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    }
                }
            } catch (ExpiredJwtException e) {
                handleTokenExpirationException(requestUrl, token, response);
                return;
            } catch (MalformedJwtException e) {
                handleSignatureException(response);
                return;
            } catch (Exception e) {
                handleUnexpectedException(response, e);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private void handleTokenExpirationException(String requestUrl, String token, HttpServletResponse response) throws IOException {
        if (requestUrl.contains(LOGIN_PATH) || requestUrl.contains(REGISTRATION_PATH)) {
            String refreshToken = jwtTokenProvider.refreshToken(token);
            response.setHeader(AUTHORIZATION_HEADER, BEARER_PREFIX + refreshToken);
        } else {
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED, ApiErrorMessage.EXPIRED_TOKEN.getMessage());
        }
    }

    private void handleSignatureException(HttpServletResponse response) throws IOException {
        sendErrorResponse(response, HttpStatus.UNAUTHORIZED, ApiErrorMessage.INVALID_TOKEN_SIGNATURE.getMessage());
    }

    private void handleUnexpectedException(HttpServletResponse response, Exception e) throws IOException {
        log.error(ApiErrorMessage.ERROR_DURING_JWT_PROCESSING.getMessage(), e);

        sendErrorResponse(response, HttpStatus.INTERNAL_SERVER_ERROR, ApiErrorMessage.ERROR_DURING_JWT_PROCESSING.getMessage());
    }

    private void sendErrorResponse(HttpServletResponse response, HttpStatus status, String message) throws IOException {
        response.setStatus(status.value());
        response.getWriter().write(message);
    }
}
