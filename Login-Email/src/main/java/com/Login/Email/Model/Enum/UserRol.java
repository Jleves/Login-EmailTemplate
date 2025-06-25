package com.Login.Email.Model.Enum;

import java.util.Set;

public enum UserRol {
    INVITADO(Set.of(Permissions.READ)),
    USER(Set.of(Permissions.READ,Permissions.CREATED)),
    ADMINISTRADOR(Set.of(Permissions.CREATED,Permissions.READ,Permissions.UPDATE)),
    SUPERADMINISTRADOR(Set.of(Permissions.CREATED,Permissions.READ,Permissions.UPDATE,Permissions.DELETE));
    private final Set<Permissions> permisos;




    UserRol(Set<Permissions> permisos) {
        this.permisos = permisos;
    }
    public Set<Permissions> getPermisos() {
        return permisos;
    }

    public String getAuthority() {
        return "ROLE_" + this.name();
    }

}
