package me.scene.dinner.domain.board.article;

import me.scene.dinner.domain.account.Account;
import me.scene.dinner.domain.account.AccountFactory;
import me.scene.dinner.domain.account.AccountRepository;
import me.scene.dinner.domain.board.topic.Topic;
import me.scene.dinner.domain.board.topic.TopicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ArticleFactory {

    private final AccountFactory accountFactory;
    private final AccountRepository accountRepository;
    private final TopicRepository topicRepository;
    private final ArticleRepository articleRepository;

    @Autowired
    public ArticleFactory(AccountFactory accountFactory, AccountRepository accountRepository, TopicRepository topicRepository, ArticleRepository articleRepository) {
        this.accountFactory = accountFactory;
        this.accountRepository = accountRepository;
        this.topicRepository = topicRepository;
        this.articleRepository = articleRepository;
    }


    @Transactional
    public void create(String title) {
        accountFactory.createInRegular("scene");
        Account scene = accountRepository.findByUsername("scene").orElseThrow();

        Topic topic = new Topic();
        topicRepository.save(topic);

        ArticleForm articleForm = new ArticleForm();
        articleForm.setTitle(title);
        articleForm.setParentUrl("/magazine/topic/");
        articleForm.setUrl(title);

        Article article = new Article(scene, topic, articleForm);
        articleRepository.save(article);
    }

}
