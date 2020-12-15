package me.scene.dinner.test.proxy.service;

import lombok.extern.slf4j.Slf4j;
import me.scene.dinner.board.application.article.ArticleService;
import me.scene.dinner.board.application.topic.TopicService;
import me.scene.dinner.board.domain.article.Article;
import me.scene.dinner.tag.TaggedArticle;
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
        article.getTaggedArticles().stream().map(TaggedArticle::getTag).forEach(tag -> log.debug("load: " + tag));
        return article;
    }

}
