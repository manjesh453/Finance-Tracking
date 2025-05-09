package com.users.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@Entity
@Data
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate = new Date();
    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastModifiedDate;
    @Version
    private Long version = 0L;
    private float totalExpenses;
    private float totalIncome;
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private Users users;
}
