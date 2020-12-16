package me.scene.dinner.board.ui;

import me.scene.dinner.account.domain.account.Account;
import me.scene.dinner.board.article.domain.Article;
import me.scene.dinner.board.magazine.domain.Magazine;
import me.scene.dinner.board.magazine.domain.Policy;
import me.scene.dinner.board.article.domain.Reply;
import me.scene.dinner.board.topic.domain.Topic;
import me.scene.dinner.test.facade.FactoryFacade;
import me.scene.dinner.test.facade.RepositoryFacade;
import me.scene.dinner.test.proxy.service.ArticleServiceProxy;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.stream.Collectors;

import static me.scene.dinner.test.utils.Authenticators.login;
import static me.scene.dinner.test.utils.Authenticators.logout;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Reply")
class ReplyControllerTest {

    @Autowired MockMvc mockMvc;

    @SpyBean ArticleServiceProxy articleService;

    @Autowired FactoryFacade factoryFacade;
    @Autowired RepositoryFacade repositoryFacade;


    Account user;
    Magazine magazine;
    Topic topic;
    Article article;

    @BeforeEach
    void setup() {
        user = factoryFacade.createAccount("user");
        magazine = factoryFacade.createMagazine(user, "Test Magazine", Policy.OPEN);
        topic = factoryFacade.createTopic(magazine, user, "Test Topic");
        article = factoryFacade.createArticle(topic, user, "Test Article", true);
        login(user);
    }

    @AfterEach
    void clear() {
        repositoryFacade.deleteAll();
    }


    @Nested
    class Post {

        @Test
        void saves_And_redirectsTo_article() throws Exception {
            mockMvc.perform(
                    post("/replies")
                            .param("articleId", article.getId().toString())
                            .param("content", "Test Reply")
                            .with(csrf())
            )
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrlPattern("/articles/*"))
            ;
            article = articleService.load(article.getTitle());
            List<String> replies = article.getReplies().stream().map(Reply::getContent).collect(Collectors.toList());
            assertThat(replies).contains("Test Reply");
        }

        @Nested
        class When_unauthenticated {
            @Test
            void redirectsTo_login() throws Exception {
                logout();
                mockMvc.perform(
                        post("/replies").with(csrf())
                )
                        .andExpect(status().is3xxRedirection())
                        .andExpect(redirectedUrlPattern("**/login"))
                ;
            }
        }

    }

    @Nested
    class Delete {

        Reply reply;

        @BeforeEach
        void setup() {
            factoryFacade.createReply(article, user, "Test Reply");
            article = articleService.load(article.getTitle());
            reply = article.getReplies().stream().filter(r -> r.getContent().equals("Test Reply")).findFirst().orElseThrow();
        }

        @Test
        void deletes_And_redirectsTo_article() throws Exception {
            mockMvc.perform(
                    delete("/replies")
                            .param("articleId", article.getId().toString())
                            .param("replyId", reply.getId().toString())
                            .with(csrf())
            )
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/articles/" + article.getId()))
            ;
            article = articleService.load(article.getTitle());
            List<String> replies = article.getReplies().stream().map(Reply::getContent).collect(Collectors.toList());
            assertThat(replies).doesNotContain("Test Reply");
        }

        @Nested
        class When_notOwner {
            @Test
            void handles_exception() throws Exception {
                logout();
                Account stranger = factoryFacade.createAccount("stranger");
                login(stranger);
                mockMvc.perform(
                        delete("/replies")
                                .param("articleId", article.getId().toString())
                                .param("replyId", reply.getId().toString())
                                .with(csrf())
                )
                        .andExpect(status().isOk())
                        .andExpect(view().name("error/access"))
                ;
                article = articleService.load(article.getTitle());
                List<String> replies = article.getReplies().stream().map(Reply::getContent).collect(Collectors.toList());
                assertThat(replies).contains("Test Reply");
            }
        }

    }

}
