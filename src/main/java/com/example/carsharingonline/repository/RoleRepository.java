package com.example.carsharingonline.repository;

import com.example.carsharingonline.model.Role;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> getByRole(Role.RoleName roleName);
}
