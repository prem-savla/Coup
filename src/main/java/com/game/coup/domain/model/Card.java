package com.game.coup.domain.model;

import java.util.Objects;
import java.util.UUID;

import com.game.coup.domain.definitions.Role;

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