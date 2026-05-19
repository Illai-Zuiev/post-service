package practical.post.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import practical.post.model.constants.ApiErrorMessage;
import practical.post.model.entity.User;
import practical.post.repository.UserRepository;
import practical.post.security.CustomUserDetails;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmailAndDeletedFalse(email).orElseThrow(
                () -> new UsernameNotFoundException(ApiErrorMessage.USER_NOT_FOUND_BY_EMAIL.getMessage(email))
        );

        List<SimpleGrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
                .toList();

        return new CustomUserDetails(user, authorities);
    }
}
