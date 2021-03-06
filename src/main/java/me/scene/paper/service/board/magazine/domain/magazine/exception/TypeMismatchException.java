package me.scene.paper.service.board.magazine.domain.magazine.exception;


import me.scene.paper.service.board.magazine.domain.magazine.model.Type;

public class TypeMismatchException extends org.hibernate.TypeMismatchException {

    public TypeMismatchException(Type expected, Type actual) {
        super(String.format("expected: %s, actual: %s", expected.name(), actual.name()));
    }

}
