package me.scene.paper.service.integration.service.board.topic;

import me.scene.paper.service.board.magazine.domain.magazine.model.Type;
import me.scene.paper.service.board.topic.application.query.TopicQueryService;
import me.scene.paper.service.integration.utils.MagazineTestHelper;
import me.scene.paper.service.integration.utils.TopicTestHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@DisplayName("Topic_query")
class TopicQueryServiceTest {

    @Autowired TopicQueryService service;

    @Autowired TopicTestHelper helper;
    @Autowired MagazineTestHelper magazineHelper;


    Long id;

    @BeforeEach
    void create() {
        Long magazineId = magazineHelper.createMagazine("magazineOwner", "magazine", Type.OPEN);
        id = helper.createTopic("user", magazineId, "topic");
    }

    @AfterEach
    void clear() {
        helper.clearTopics();
        magazineHelper.clearMagazines();
    }

}
