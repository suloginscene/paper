package me.scene.dinner.plain;

import me.scene.dinner.board.article.domain.Article;
import me.scene.dinner.board.magazine.domain.common.AuthorizationException;
import me.scene.dinner.board.magazine.domain.exclusive.ExclusiveMagazine;
import me.scene.dinner.board.topic.domain.Topic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;


@DisplayName("Magazine(exclusive)")
class ExclusiveMagazineTest {

    ExclusiveMagazine magazine;

    @BeforeEach
    void create() {
        magazine = new ExclusiveMagazine("owner", "magazine", "short", "long");
    }


    @Nested class OnCreateChild {
        @Nested class WithNotOwner {
            @Test
            void throwsException() {
                assertThrows(
                        AuthorizationException.class,
                        () -> new Topic(magazine, "notOwner", "title", "short", "long")
                );

                Topic topic = new Topic(magazine, "owner", "title", "short", "long");
                assertThrows(
                        AuthorizationException.class,
                        () -> new Article(topic, "notOwner", "title", "content", true)
                );
            }
        }
    }

}
