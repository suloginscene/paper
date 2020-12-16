package me.scene.dinner.test.proxy.service;

import lombok.extern.slf4j.Slf4j;
import me.scene.dinner.board.article.application.ArticleService;
import me.scene.dinner.board.topic.application.TopicService;
import me.scene.dinner.board.article.domain.Article;
import me.scene.dinner.test.proxy.repository.ArticleRepositoryProxy;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Transactional
public class ArticleServiceProxy extends ArticleService {

    private final ArticleRepositoryProxy articleRepository;

    public ArticleServiceProxy(ArticleRepositoryProxy articleRepository, TopicService topicService, ApplicationEventPublisher applicationEventPublisher) {
        super(articleRepository, topicService, applicationEventPublisher);
        this.articleRepository = articleRepository;
    }

    public Article load(String title) {
        Article article = articleRepository.findByTitle(title).orElseThrow();
        log.debug("load: " + article.getContent());
        log.debug("load: " + article.getTopic());
        log.debug("load: " + article.getReplies());
        return article;
    }

}
