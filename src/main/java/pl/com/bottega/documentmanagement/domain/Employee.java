package pl.com.bottega.documentmanagement.domain;

import org.hibernate.annotations.NaturalId;

import javax.persistence.*;

import java.util.Arrays;
import java.util.Set;

import static com.google.common.base.Preconditions.checkState;

/**
 * Created by maciuch on 12.06.16.
 */
@Entity
public class Employee {

    @EmbeddedId
    private EmployeeId employeeId;
    private String hashedPassword;

    @NaturalId
    private String login;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Role> roles;

    private Employee() {}

    public Employee(EmployeeId employeeId) {
        this.employeeId = employeeId;
    }

    public Employee(String login, String hashedPassword, EmployeeId employeeId) {
        this.login = login;
        this.hashedPassword = hashedPassword;
        this.employeeId = employeeId;
    }

    public boolean isRegistered() {
        return login != null;
    }

    public void setupAccount(String login, String password) {
        checkState(!isRegistered());
        this.login = login;
        this.hashedPassword = password;
    }

    public boolean hasRoles(String[] roleNames) {
        if (roleNames.length == 0)
            return true;
        return !Arrays.stream(roleNames).anyMatch((roleName) -> !roles.contains(new Role (roleName)));
    }

    public void updateRoles(Set<Role> newRoles) {
        this.roles = newRoles;
    }

    public EmployeeId getEmployeeId() {
        return employeeId;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public String getLogin() {
        return login;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public Set<Role> getRole() {
        return roles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Employee employee = (Employee) o;

        return employeeId != null ? employeeId.equals(employee.employeeId) : employee.employeeId == null;
    }

    @Override
    public int hashCode() {
        return employeeId != null ? employeeId.hashCode() : 0;
    }
}
