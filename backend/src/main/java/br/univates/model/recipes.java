package br.univates.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
public class recipes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private BigDecimal price;
    private String recipe_type;

    private LocalDateTime CreatedAt;
    @PrePersist
    protected void onCreate() {
        CreatedAt = LocalDateTime.now();
    }

}