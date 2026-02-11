package com.game.coup.entities;

import com.game.coup.definitions.Role;

import java.util.*;

public class Card {

    private final String id;
    private final Role type;

    public Card(Role type) {
        this.id = UUID.randomUUID().toString();
        this.type = type;
    }


    public String getId() {
        return id;
    }

    public Role getType() {
        return type;
    }
    
    public boolean sameType(Card other) {
        if (other == null) return false;
        return this.type == other.type;
    }

    public boolean hasType(Role role) {
        return this.type == role;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Card other)) return false;
        return Objects.equals(this.id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}