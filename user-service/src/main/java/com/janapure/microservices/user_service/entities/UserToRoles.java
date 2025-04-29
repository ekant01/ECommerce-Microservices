package com.janapure.microservices.user_service.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

@Data
@Entity
@Table(name = "user_to_roles")
public class UserToRoles {

    @EmbeddedId
    private UserRolePrimaryKey userRolePrimaryKey;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("roleId")
    @JoinColumn(name = "role_id", nullable = false)
    private Roles role;

    @Data
    @Embeddable
    public static class UserRolePrimaryKey implements Serializable {
        @Column(name = "user_id")
        private Long userId;

        @Column(name = "role_id")
        private Long roleId;
    }


}
