package me.scene.paper.service.integration.service.board.magazine;

import me.scene.paper.service.board.magazine.application.cache.BestMagazineCache;
import me.scene.paper.service.board.magazine.application.command.MagazineService;
import me.scene.paper.service.board.magazine.application.command.request.MagazineCreateRequest;
import me.scene.paper.service.board.magazine.application.command.request.MagazineUpdateRequest;
import me.scene.paper.service.board.magazine.domain.magazine.model.Type;
import me.scene.paper.service.integration.utils.MagazineTestHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.atLeastOnce;


@SpringBootTest
@DisplayName("Magazine_service")
class MagazineServiceTest {

    @Autowired MagazineService service;

    @MockBean BestMagazineCache cache;

    @Autowired MagazineTestHelper helper;


    @AfterEach
    void clear() {
        helper.clearMagazines();
    }


    @Nested class OnCreate {
        @Test
        void publishes() {
            MagazineCreateRequest request = new MagazineCreateRequest("owner", "title", "short", "long", "OPEN");
            Long id = service.save(request);

            then(cache).should().update(id);
        }
    }

    @Nested class OnUpdate {
        @Test
        void publishes() {
            Long id = helper.createMagazine("owner", "title", Type.OPEN);

            MagazineUpdateRequest request = new MagazineUpdateRequest("owner", "new", "short", "long");
            service.update(id, request);

            then(cache).should(atLeastOnce()).update(id);
        }
    }

    @Nested class OnDelete {
        @Test
        void publishes() {
            Long id = helper.createMagazine("owner", "title", Type.OPEN);

            service.delete(id, "owner");

            then(cache).should(atLeastOnce()).update(id);
        }
    }

}
