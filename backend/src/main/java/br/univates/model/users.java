package br.univates.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Entity
public class users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    @JsonIgnore
    @ToString.Exclude
    private String password;

    @Column(unique = true)
    private String login;

    private String situation;

    private LocalDateTime createdAt;
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

}
