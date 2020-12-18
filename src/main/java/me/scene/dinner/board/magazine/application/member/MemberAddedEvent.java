package me.scene.dinner.board.magazine.application.member;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter @EqualsAndHashCode
public class MemberAddedEvent {

    private final Long magazineId;
    private final String magazineTitle;
    private final String member;

}