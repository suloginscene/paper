package me.scene.dinner.board.domain.magazine.event;

import org.springframework.context.ApplicationEvent;

public class MagazineChangedEvent extends ApplicationEvent {

    public MagazineChangedEvent(Object source) {
        super(source);
    }

}