package com.cleverdev.oldsystem.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class ClientProfile {
    @Id
    private String guid;

    private String agency;
    private String firstName;
    private String lastName;
    private String status;
    @Column(length = 10)
    private String dob;
    private LocalDateTime createdDateTime;
}
