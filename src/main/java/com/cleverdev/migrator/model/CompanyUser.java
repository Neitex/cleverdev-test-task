package com.cleverdev.migrator.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "company_user")
public class CompanyUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "login", nullable = false, unique = true)
    private String login;

    @OneToMany(mappedBy = "createdByUser")
    private Set<PatientNote> createdNotes;

    @OneToMany(mappedBy = "lastModifiedByUser")
    private Set<PatientNote> modifiedNotes;

    @PrePersist
    @PreUpdate
    private void ensureLoginIsLowercase() {
        if (login != null) {
            login = login.toLowerCase();
        }
    }
}
