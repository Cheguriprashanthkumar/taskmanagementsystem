package com.prashanth.taskmanagementsystem.repository;

import com.prashanth.taskmanagementsystem.model.entity.Role;
import com.prashanth.taskmanagementsystem.model.enums.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRoleType(RoleType roleType);
}
