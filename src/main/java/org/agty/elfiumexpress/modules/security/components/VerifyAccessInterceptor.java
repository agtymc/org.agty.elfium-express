package org.agty.elfiumexpress.modules.security.components;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.agty.elfiumexpress.repository.UserRepository;
import org.agty.elfiumexpress.modules.security.entity.User;
import org.agty.elfiumexpress.modules.security.service.UserDetailsCustom;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class VerifyAccessInterceptor implements HandlerInterceptor {
    private final UserRepository userRepository;

    public VerifyAccessInterceptor(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getName())) {
            User user = getUser();

            if (user == null  || user.isDisabled()) {
                return logout(request, response);
            } else {
                return updateUserDetails(user);
            }
        }

        return true;
    }

    /**
     * Fetch user details
     * @return User
     */
    private User getUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(principal instanceof UserDetailsCustom now)) {
            return null;
        }
        return userRepository.findById(now.getUser().getId());
    }

    /**
     * Logout from a server
     * @param request HttpServletRequest
     * @param response  HttpServletResponse
     * @return bool
     */
    private boolean logout(HttpServletRequest request, HttpServletResponse response) throws Exception {
        new SecurityContextLogoutHandler().logout(request, response, SecurityContextHolder.getContext().getAuthentication());
        new DefaultRedirectStrategy().sendRedirect(request, response, "/login");
        return false;
    }

    /**
     * Update user details
     * @param user User
     * @return bool
     */
    private boolean updateUserDetails(User user) {
        UserDetailsCustom userDetailsCustom = new UserDetailsCustom(user);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetailsCustom,
                userDetailsCustom.getPassword(),
                userDetailsCustom.getAuthorities()
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        return true;
    }
}
