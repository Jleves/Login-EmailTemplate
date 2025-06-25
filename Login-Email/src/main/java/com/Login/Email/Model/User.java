package com.Login.Email.Model;


import com.Login.Email.Model.AbstracClass.UserBase;
import com.Login.Email.Model.Enum.Permissions;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Usuarios")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class User extends UserBase implements UserDetails {


    private String email;
    private boolean isEnabled;




    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();

        // Rol
        authorities.add(new SimpleGrantedAuthority(getRol().getAuthority()));

        // Permisos
        for (Permissions permiso : getRol().getPermisos()) {
            authorities.add(new SimpleGrantedAuthority(permiso.getAuthority()));
        }

        return authorities;
    }


    /* Mas de un rol:  --> Activar en clase abstracta UserBase

   for (Rol rol : getRoles()) {
    authorities.add(new SimpleGrantedAuthority(rol.getAuthority()));
    for (Permissions permiso : rol.getPermisos()) {
        authorities.add(new SimpleGrantedAuthority(permiso.getAuthority()));
    }
}


    */



    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }


}
