package org.example.crmsystem.model;

import lombok.*;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Trainer extends User {
    private String specialization;
}
