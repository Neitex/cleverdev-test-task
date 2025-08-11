package com.cleverdev.oldsystem.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonValue;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.springframework.cglib.core.Local;

@Entity
@Getter @Setter
public class ClientNote {
    @Id private String guid;

    @ManyToOne @JoinColumn(name = "client_guid")
    private ClientProfile client;
    @Column(length = 4000)
    private String comments;
    private String loggedUser;
    private LocalDateTime datetime;
    private LocalDateTime createdDateTime;
    private LocalDateTime modifiedDateTime;
}
