package root.models;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    Admin, User;

    Role() {
    }

    @Override
    public String getAuthority() {
        return name();
    }
}
