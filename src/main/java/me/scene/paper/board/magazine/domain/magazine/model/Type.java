package me.scene.paper.board.magazine.domain.magazine.model;


import me.scene.paper.board.magazine.domain.magazine.exception.TypeMismatchException;

public enum Type {

    OPEN, MANAGED, EXCLUSIVE;

    public void check(Type expected) {
        if (this == expected) return;
        throw new TypeMismatchException(expected, this);
    }

}