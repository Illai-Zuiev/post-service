package practical.post.service;

import practical.post.model.entity.RefreshToken;
import practical.post.model.entity.User;

public interface RefreshTokenService {
    RefreshToken generateOrUpdateRefreshToken(User user);

    RefreshToken validateAndRefreshToken(String refreshToken);
}
