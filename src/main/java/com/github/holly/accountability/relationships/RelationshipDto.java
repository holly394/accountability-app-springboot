package com.github.holly.accountability.relationships;

import com.github.holly.accountability.user.UserDto;

public class RelationshipDto {

    private Long id;
    private RelationshipStatus status;

    private UserDto user;
    private UserDto partner;

    public RelationshipDto() {
    }

    public RelationshipDto(Long id, RelationshipStatus status, UserDto user, UserDto partner) {
        this.id = id;
        this.status = status;
        this.user = user;
        this.partner = partner;
    }

    public RelationshipDto flipped()  {
        return new RelationshipDto(id, status, partner, user);
    }

    public RelationshipStatus getStatus() {
        return status;
    }

    public void setStatus(RelationshipStatus status) {
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public UserDto getUser() {
        return user;
    }

    public UserDto getPartner() {
        return partner;
    }
}
