package me.kanmodel.gra.pms.entity;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 用户账户信息
 */
@Entity
@Table(name = "sys_users")
@Data
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "user_login", columnDefinition = "varchar(20)  default ''", unique = true, nullable = false)
    private String login;

    @Column(name = "user_pass", columnDefinition = "varchar(255)  default ''", nullable = false)
    private String pass;

    @ManyToMany(cascade = {CascadeType.REFRESH}, fetch = FetchType.EAGER)
    private List<SysRole> roles;

    @Column(name = "user_email", columnDefinition = "varchar(20)  default ''", nullable = false)
    private String email;

    @Column(name = "user_registered", columnDefinition = "datetime default current_timestamp")
    private Timestamp registered;

    @Column(name = "user_display", columnDefinition = "varchar(20)  default ''", nullable = false)
    private String display;

    @Column(name = "is_delete", columnDefinition = "bit(1) default 0")
    private Boolean isDelete  = false;

    public User(){}

    public User(String user_login, String user_pass) {
        this.login = user_login;
        this.pass = user_pass;
        this.email = "";
        this.registered = new Timestamp(System.currentTimeMillis());
        this.display = user_login;
        this.roles = new ArrayList<>();
        roles.add(new SysRole(1L, "ROLE_USER"));
    }

    public User(String user_login, String user_pass, Role role) {
        this.login = user_login;
        this.pass = user_pass;
        this.email = "";
        this.registered = new Timestamp(System.currentTimeMillis());
        this.display = user_login;
        this.roles = new ArrayList<>();
        roles.add(new SysRole(role.getId(), role.getName()));
    }

    public User(String user_login, String user_pass, ArrayList<SysRole> roles) {
        this.login = user_login;
        this.pass = user_pass;
        this.email = "";
        this.registered = new Timestamp(System.currentTimeMillis());
        this.display = user_login;
        this.roles = roles;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> auths = new ArrayList<>();
        List<SysRole> roles = this.getRoles();
        for (SysRole role : roles) {
            auths.add(new SimpleGrantedAuthority(role.getName()));
        }
        return auths;
    }

    @Override
    public String getPassword() {
        return this.pass;
    }

    @Override
    public String getUsername() {
        return this.login;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !isDelete;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    /**
     * 判断是否有对应权限
     * @param roleName 权限名称
     */
    public boolean haveRole(String roleName) {
        for (SysRole role : roles)
            if (role.getName().equals(roleName)) return true;
        return false;
    }
}