package com.example.bookstore.role;

import jakarta.persistence.*;
import lombok.Getter;

import java.io.Serializable;

@Entity
public class UserRole {
    @Embeddable
    public static class Id implements Serializable {
        private static final long serialVersionUID = 1322120000551624359L;

        @Column
        protected Long userId;

        @Enumerated(EnumType.STRING)
        @Column
        protected Role role;

        public Id() { }

        public Id(Long userId, Role role) {
            this.userId = userId;
            this.role = role;
        }
    }

    @EmbeddedId
    Id id = new Id();

    @Getter
    @Enumerated(EnumType.STRING)
    @Column(name = "ROLE", insertable=false, updatable=false)
    protected Role role;

}
