package lol.apex.feature.module.base;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Category {

    COMBAT("Combat"),
    MOVEMENT("Movement"),
    PLAYER("Player"),
    VISUAL("Visual"),
    LEGIT("Legit"),
    OTHER("Other");

    private final String name;

    @Override
    public String toString() {
        return name;
    }
}