package com.github.holly.accountability.users;

public class UserDto {
    private Long id;

    private String username;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean equals(Object compared){
        if(this == compared){
            return true;
        }

        if(!(this instanceof UserDto)){
            return false;
        }

        UserDto userDtoCompared = (UserDto) compared;

        if(this.id == userDtoCompared.id &&
                this.username == userDtoCompared.username){
            return true;
        }
        return false;

    }
}
