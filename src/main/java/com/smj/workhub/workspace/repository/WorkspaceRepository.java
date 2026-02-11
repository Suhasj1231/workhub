package com.smj.workhub.workspace.repository;

import com.smj.workhub.workspace.entity.Workspace;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;

public interface WorkspaceRepository extends JpaRepository<Workspace, Long> {

    Optional<Workspace> findByName(String name);

    // ✅ Find active workspace by ID
    Optional<Workspace> findByIdAndDeletedFalse(Long id);


    Page<Workspace> findAllByDeletedFalse(Pageable pageable);


    // ✅ Check duplicate name only among active workspaces
    boolean existsByNameAndDeletedFalse(String name);

}
