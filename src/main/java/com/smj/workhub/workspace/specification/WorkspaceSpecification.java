package com.smj.workhub.workspace.specification;

import com.smj.workhub.workspace.entity.Workspace;
import org.springframework.data.jpa.domain.Specification;

public class WorkspaceSpecification {

    public static Specification<Workspace> hasDeleted(Boolean deleted) {
        return (root, query, cb) ->
                deleted == null ? null : cb.equal(root.get("deleted"), deleted);
    }

    public static Specification<Workspace> nameContains(String name) {
        return (root, query, cb) ->
                name == null ? null :
                        cb.like(cb.lower(root.get("name")),
                                "%" + name.toLowerCase() + "%");
    }
}
