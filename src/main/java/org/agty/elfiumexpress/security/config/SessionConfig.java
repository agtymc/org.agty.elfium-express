package org.agty.elfiumexpress.security.config;

import org.agty.elfiumexpress.config.LocalConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.jdbc.config.annotation.web.http.EnableJdbcHttpSession;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

@Configuration
@EnableJdbcHttpSession(
        tableName = "spring_users_session",
        maxInactiveIntervalInSeconds = 2592000
)
public class SessionConfig {
    @Bean
    public CookieSerializer cookieSerializer() {
        DefaultCookieSerializer serializer = new DefaultCookieSerializer();
        serializer.setCookieName(LocalConfig.getString("session.cookie.name", "SESSION"));
        serializer.setCookiePath("/");
        serializer.setUseHttpOnlyCookie(true);
        serializer.setUseSecureCookie(LocalConfig.getBoolean("session.cookie.secure", false));
        serializer.setSameSite(LocalConfig.getString("session.cookie.same-site", "Lax"));
        serializer.setCookieMaxAge(LocalConfig.getInt("session.cookie.max-age", 2592000));
        return serializer;
    }
}
