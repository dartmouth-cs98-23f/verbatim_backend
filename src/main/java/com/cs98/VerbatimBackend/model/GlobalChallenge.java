package com.cs98.VerbatimBackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.util.List;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GlobalChallenge {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "q1_id")
    private Question q1;

    @ManyToOne
    @JoinColumn(name = "q2_id")
    private Question q2;

    @ManyToOne
    @JoinColumn(name = "q3_id")
    private Question q3;

    private Date date;
}
