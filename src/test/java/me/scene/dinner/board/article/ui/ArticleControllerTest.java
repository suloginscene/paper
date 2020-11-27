package me.scene.dinner.board.article.ui;

import me.scene.dinner.account.application.AccountService;
import me.scene.dinner.account.domain.account.Account;
import me.scene.dinner.board.article.application.ArticleNotFoundException;
import me.scene.dinner.board.article.domain.Article;
import me.scene.dinner.board.article.domain.Status;
import me.scene.dinner.board.magazine.domain.Magazine;
import me.scene.dinner.board.magazine.domain.Policy;
import me.scene.dinner.board.reply.domain.Reply;
import me.scene.dinner.board.topic.domain.Topic;
import me.scene.dinner.mail.infra.TestMailSender;
import me.scene.dinner.test.facade.FactoryFacade;
import me.scene.dinner.test.facade.RepositoryFacade;
import me.scene.dinner.test.proxy.ArticleServiceProxy;
import me.scene.dinner.test.proxy.MagazineServiceProxy;
import me.scene.dinner.test.proxy.TopicServiceProxy;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static me.scene.dinner.test.utils.authentication.Authenticators.login;
import static me.scene.dinner.test.utils.authentication.Authenticators.logout;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Article")
class ArticleControllerTest {

    @Autowired MockMvc mockMvc;

    @SpyBean ArticleServiceProxy articleService;
    @SpyBean TopicServiceProxy topicService;
    @SpyBean MagazineServiceProxy magazineService;
    @Autowired AccountService accountService;
    @MockBean TestMailSender mailSender;

    @Autowired FactoryFacade factoryFacade;
    @Autowired RepositoryFacade repositoryFacade;


    Account manager;
    Account user;

    Magazine magazine;
    Topic topic;

    @BeforeEach
    void setup() {
        manager = factoryFacade.createAccount("manager");
        magazine = factoryFacade.createMagazine(manager, "Test magazine", Policy.OPEN);
        topic = factoryFacade.createTopic(magazine, manager, "Test Topic");
        user = factoryFacade.createAccount("user");
        login(user);
    }

    @AfterEach
    void clear() {
        repositoryFacade.deleteAll();
    }


    @Nested
    class Create {

        @Nested
        class Page {
            @Test
            void returns_form() throws Exception {
                mockMvc.perform(
                        get("/article-form")
                                .param("topicId", topic.getId().toString())
                )
                        .andExpect(status().isOk())
                        .andExpect(view().name("page/board/article/form"))
                        .andExpect(model().attributeExists("articleForm"))
                ;
            }

            @Nested
            class When_unauthenticated {
                @Test
                void redirectsTo_login() throws Exception {
                    logout();
                    mockMvc.perform(
                            get("/article-form")
                    )
                            .andExpect(status().is3xxRedirection())
                            .andExpect(redirectedUrlPattern("**/login"))
                    ;
                }
            }
        }

        @Nested
        class Submit {
            @Nested
            class When_open {
                @Test
                void saves_And_redirectsTo_article() throws Exception {
                    mockMvc.perform(
                            post("/articles")
                                    .param("topicId", topic.getId().toString())
                                    .param("title", "Test Article")
                                    .param("content", "This is test article.")
                                    .param("status", "PUBLIC")
                                    .with(csrf())
                    )
                            .andExpect(status().is3xxRedirection())
                            .andExpect(redirectedUrlPattern("/articles/*"))
                    ;
                    Article article = articleService.load("Test Article");
                    assertThat(article.getContent()).isEqualTo("This is test article.");
                    assertThat(article.getTopic()).isEqualTo(topic);
                    assertThat(article.getWriter()).isEqualTo(user.getUsername());
                    magazine = magazineService.load(magazine.getTitle());
                    assertThat(magazine.getWriters()).contains(user.getUsername());
                }

                @Nested
                class With_invalid_params {
                    @Test
                    void returns_errors() throws Exception {
                        mockMvc.perform(
                                post("/articles")
                                        .with(csrf())
                                        .param("topicId", topic.getId().toString())
                        )
                                .andExpect(status().isOk())
                                .andExpect(view().name("page/board/article/form"))
                                .andExpect(model().hasErrors())
                                .andExpect(model().errorCount(3))
                        ;
                    }
                }
            }

            @Nested
            class When_managed {
                Magazine managed;

                @BeforeEach
                void setup() {
                    managed = factoryFacade.createMagazine(manager, "MANAGED Magazine", Policy.MANAGED);
                    topic = factoryFacade.createTopic(managed, manager, "MANAGED Topic");
                }

                @Test
                void handles_exception() throws Exception {
                    mockMvc.perform(
                            post("/articles")
                                    .param("topicId", topic.getId().toString())
                                    .param("title", "Test Article")
                                    .param("content", "This is test article.")
                                    .param("status", "PUBLIC")
                                    .with(csrf())
                    )
                            .andExpect(status().isOk())
                            .andExpect(view().name("error/access"))
                    ;
                }

                @Nested
                class When_member {
                    @Test
                    void saves_And_redirectsTo_article() throws Exception {
                        magazineService.addMember(managed, user);
                        mockMvc.perform(
                                post("/articles")
                                        .param("topicId", topic.getId().toString())
                                        .param("title", "Test Article")
                                        .param("content", "This is test article.")
                                        .param("status", "PUBLIC")
                                        .with(csrf())
                        )
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrlPattern("/articles/*"))
                        ;
                    }
                }
            }

            @Nested
            class When_exclusive {
                Magazine exclusive;

                @BeforeEach
                void setup() {
                    exclusive = factoryFacade.createMagazine(manager, "EXCLUSIVE Magazine", Policy.EXCLUSIVE);
                    topic = factoryFacade.createTopic(exclusive, manager, "EXCLUSIVE Topic");
                }

                @Test
                void handles_exception() throws Exception {
                    mockMvc.perform(
                            post("/articles")
                                    .param("topicId", topic.getId().toString())
                                    .param("title", "Test Article")
                                    .param("content", "This is test article.")
                                    .param("status", "PUBLIC")
                                    .with(csrf())
                    )
                            .andExpect(status().isOk())
                            .andExpect(view().name("error/access"));
                }

                @Nested
                class When_Manager {
                    @Test
                    void saves_And_redirectsTo_article() throws Exception {
                        logout();
                        login(manager);
                        mockMvc.perform(
                                post("/articles")
                                        .param("topicId", topic.getId().toString())
                                        .param("title", "Test Article")
                                        .param("content", "This is test article.")
                                        .param("status", "PUBLIC")
                                        .with(csrf())
                        )
                                .andExpect(status().is3xxRedirection())
                                .andExpect(redirectedUrlPattern("/articles/*"))
                        ;
                    }
                }
            }
        }

    }

    @Nested
    class Read {

        Article article;

        @BeforeEach
        void setup() {
            article = factoryFacade.createArticle(topic, manager, "Test Article", Status.PUBLIC);
        }

        // TODO DTO
        @Test @Disabled
        void shows_article() throws Exception {
            mockMvc.perform(
                    get("/articles/" + article.getId())
            )
                    .andExpect(status().isOk())
                    .andExpect(view().name("page/board/article/view"))
                    .andExpect(model().attributeExists("article"))
            ;
        }

        @Nested
        class When_nonExistent {
            @Test
            void handles_exception() throws Exception {
                mockMvc.perform(
                        get("/articles/" + (article.getId() + 1))
                )
                        .andExpect(status().isOk())
                        .andExpect(view().name("error/board_not_found"))
                ;
            }
        }

        @Nested
        class When_private {
            Article privateArticle;

            @BeforeEach
            void setup() {
                privateArticle = factoryFacade.createArticle(topic, manager, "PRIVATE Article", Status.PRIVATE);
            }

            @Test
            void handles_exception() throws Exception {
                mockMvc.perform(
                        get("/articles/" + privateArticle.getId())
                )
                        .andExpect(status().isOk())
                        .andExpect(view().name("error/access"))
                ;
            }

            @Nested
            class When_owner {
                // TODO DTO
                @Test @Disabled
                void shows_article() throws Exception {
                    logout();
                    login(manager);
                    mockMvc.perform(
                            get("/articles/" + privateArticle.getId())
                    )
                            .andExpect(status().isOk())
                            .andExpect(view().name("page/board/article/view"))
                    ;
                }
            }
        }

    }

    @Nested
    class Update {

        Article article;

        @BeforeEach
        void setup() {
            article = factoryFacade.createArticle(topic, user, "Test Article", Status.PUBLIC);
        }

        @Nested
        class Page {
            @Test
            void returns_form() throws Exception {
                mockMvc.perform(
                        get("/articles/" + article.getId() + "/form")
                )
                        .andExpect(status().isOk())
                        .andExpect(view().name("page/board/article/update"))
                        .andExpect(model().attributeExists("updateForm"))
                ;
            }

            @Nested
            class With_notOwner {
                @Test
                void handles_exception() throws Exception {
                    logout();
                    login(manager);
                    mockMvc.perform(
                            get("/articles/" + article.getId() + "/form")
                    )
                            .andExpect(status().isOk())
                            .andExpect(view().name("error/access"))
                    ;
                }
            }
        }

        @Nested
        class Submit {
            @Test
            void saves_And_redirectsTo_article() throws Exception {
                Long id = article.getId();
                mockMvc.perform(
                        put("/articles/" + id)
                                .with(csrf())
                                .param("id", id.toString())
                                .param("topicId", topic.getId().toString())
                                .param("title", "Updated")
                                .param("content", "Updated content.")
                                .param("status", "PRIVATE")
                )
                        .andExpect(status().is3xxRedirection())
                        .andExpect(redirectedUrl("/articles/" + id))
                ;

                Article article = articleService.find(id);
                assertThat(article.getTitle()).isEqualTo("Updated");
                assertThat(article.getContent()).isEqualTo("Updated content.");
                assertThat(article.getStatus().name()).isEqualTo("PRIVATE");
                magazine = magazineService.load(magazine.getTitle());
                assertThat(magazine.getWriters()).doesNotContain(user.getUsername());
            }

            @Nested
            class With_invalid_params {
                @Test
                void redirectsTo_form() throws Exception {
                    Long id = article.getId();
                    mockMvc.perform(
                            put("/articles/" + id)
                                    .with(csrf())
                                    .param("id", "")
                                    .param("topicId", "")
                                    .param("title", "")
                                    .param("content", "")
                                    .param("status", "")
                    )
                            .andExpect(status().is3xxRedirection())
                            .andExpect(redirectedUrl("/articles/" + id + "/form"))
                    ;
                }
            }

            @Nested
            class With_notOwner {
                @Test
                void handles_exception() throws Exception {
                    logout();
                    login(manager);
                    Long id = article.getId();
                    mockMvc.perform(
                            put("/articles/" + id)
                                    .with(csrf())
                                    .param("id", id.toString())
                                    .param("topicId", topic.getId().toString())
                                    .param("title", "Updated")
                                    .param("content", "Updated content.")
                                    .param("status", "PRIVATE")
                    )
                            .andExpect(status().isOk())
                            .andExpect(view().name("error/access"))
                    ;
                }
            }
        }

    }

    @Nested
    class Delete {

        Article article;

        @BeforeEach
        void setup() {
            article = factoryFacade.createArticle(topic, user, "Test Article", Status.PUBLIC);
        }

        @Test
        void deletes_And_redirectsTo_topic() throws Exception {
            Long id = article.getId();
            mockMvc.perform(
                    delete("/articles/" + id)
                            .with(csrf())
            )
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/topics/" + topic.getId()))
            ;
            assertThrows(ArticleNotFoundException.class, () -> articleService.find(id));
            topic = topicService.load(topic.getTitle());
            assertThat(topic.getArticles()).isEmpty();
            magazine = magazineService.load(magazine.getTitle());
            assertThat(magazine.getWriters()).doesNotContain(user.getUsername());
        }

        @Nested
        class With_child {
            @Test
            void removes_Recursively() throws Exception {
                Long id = article.getId();
                factoryFacade.createReply(article, manager, "Reply...");
                mockMvc.perform(
                        delete("/articles/" + id)
                                .with(csrf())
                )
                        .andExpect(status().is3xxRedirection())
                        .andExpect(redirectedUrl("/topics/" + topic.getId()))
                ;
                assertThrows(ArticleNotFoundException.class, () -> articleService.find(id));
                List<Reply> replies = repositoryFacade.findAllReplies();
                assertThat(replies).isEmpty();
            }
        }

        @Nested
        class With_notOwner {
            @Test
            void handles_exception() throws Exception {
                logout();
                login(manager);
                Long id = article.getId();
                mockMvc.perform(
                        delete("/articles/" + id)
                                .with(csrf())
                )
                        .andExpect(status().isOk())
                        .andExpect(view().name("error/access"))
                ;
                assertDoesNotThrow(() -> articleService.find(id));
            }
        }

    }

}
