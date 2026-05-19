package practical.post.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import practical.post.model.constants.ApiErrorMessage;
import practical.post.model.entity.RefreshToken;
import practical.post.model.entity.User;
import practical.post.model.exceptions.NotFoundException;
import practical.post.repository.RefreshTokenRepository;
import practical.post.service.RefreshTokenService;
import practical.post.utils.ApiUtils;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public RefreshToken generateOrUpdateRefreshToken(User user) {
        return refreshTokenRepository.findByUserId(user.getId())
                .map(refreshToken -> {
                    refreshToken.setCreated(LocalDateTime.now());
                    refreshToken.setToken(ApiUtils.generateUUIDWithoutDash());

                    return refreshTokenRepository.save(refreshToken);
                })
                .orElseGet(
                        () -> {
                            RefreshToken refreshToken = new RefreshToken();

                            refreshToken.setUser(user);
                            refreshToken.setCreated(LocalDateTime.now());
                            refreshToken.setToken(ApiUtils.generateUUIDWithoutDash());

                            return refreshTokenRepository.save(refreshToken);
                        }
                );
    }

    @Override
    public RefreshToken validateAndRefreshToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token).orElseThrow(
                () -> new NotFoundException(ApiErrorMessage.REFRESH_TOKEN_NOT_FOUND_BY_TOKEN.getMessage(token))
        );

        refreshToken.setCreated(LocalDateTime.now());
        refreshToken.setToken(ApiUtils.generateUUIDWithoutDash());

        refreshToken = refreshTokenRepository.save(refreshToken);

        return refreshToken;
    }
}
