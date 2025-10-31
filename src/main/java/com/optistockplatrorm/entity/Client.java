package com.optistockplatrorm.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Table(name = "clients")
public class Client extends User {

    @NotBlank(message = "Phone is required")
    @Column(name = "phone", nullable = false)
    private String phone;

}