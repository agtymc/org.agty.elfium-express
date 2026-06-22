package org.agty.elfiumexpress.security.service;

import org.agty.elfiumexpress.config.LocalConfig;
import org.agty.elfiumexpress.security.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class SetupStateService {
    private final UserRepository userRepository;

    public SetupStateService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean isSetupEnabled() {
        return LocalConfig.getBoolean("app.setup.enabled", true);
    }

    public boolean isSetupRequired() {
        return isSetupEnabled() && userRepository.countRegisteredUsers() == 0;
    }

    public boolean isPublicRegistrationAllowed() {
        return LocalConfig.getBoolean("app.registration.public", false);
    }

    public boolean canAccessRegistration() {
        return !isSetupRequired() && isPublicRegistrationAllowed();
    }
}
