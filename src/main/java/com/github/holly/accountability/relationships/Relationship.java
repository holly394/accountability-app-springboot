package com.github.holly.accountability.relationships;
import com.github.holly.accountability.user.User;

import jakarta.persistence.*;

@Entity
@Table(name = "relationships")
public class Relationship {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @JoinColumn(name="status")
    private RelationshipStatus status;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "partner_id", nullable = false)
    private User partner;

    public Relationship() {

    }

    public Relationship(User user, User partner, RelationshipStatus status) {
        this.user = user;
        this.partner = partner;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RelationshipStatus getStatus() {
        return status;
    }

    public void setStatus(RelationshipStatus status) {
        this.status = status;
    }

    public User getPartner() {
        return partner;
    }

    public void setPartner(User partner) {
        this.partner = partner;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Relationship flipped()  {
        Relationship flipped = new Relationship(this.getPartner(), this.getUser(), this.getStatus());
        flipped.setId(this.getId());
        return flipped;
    }
}
