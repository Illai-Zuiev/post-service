package practical.post.utils;

import jakarta.servlet.http.Cookie;
import org.springframework.http.HttpHeaders;

import java.util.UUID;

public class ApiUtils {
    public static String getMethodName() {
        return Thread.currentThread().getStackTrace()[2].getMethodName();
    }

    public static String generateUUIDWithoutDash() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static Cookie createAuthCookie(String value) {
        Cookie cookie = new Cookie(HttpHeaders.AUTHORIZATION, value);

        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60);

        return cookie;
    }
}
