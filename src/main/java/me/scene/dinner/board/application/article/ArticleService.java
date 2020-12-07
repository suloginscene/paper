package me.scene.dinner.board.application.article;

import lombok.RequiredArgsConstructor;
//import me.scene.dinner.board.domain.article.ReplySummary;
//import me.scene.dinner.board.domain.article.TopicSummary;
import me.scene.dinner.board.application.topic.TopicService;
import me.scene.dinner.board.domain.article.Article;
import me.scene.dinner.board.domain.article.ArticleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service @Transactional(readOnly = true)
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final TopicService topicService;

    public Article find(Long id) {
        return articleRepository.findById(id).orElseThrow(() -> new ArticleNotFoundException(id));
    }

    @Transactional
    public Long save(Long topicId, String writer, String title, String content, boolean publicized) {
        Article article = Article.create(topicService.find(topicId), writer, title, content, publicized);
        return articleRepository.save(article).getId();
    }

    @Transactional
    public ArticleDto read(Long id, String current) {
        Article article = find(id);
        if (article.isPublicized()) article.read();
        else article.confirmWriter(current);
        return extractDto(article);
    }

    public ArticleDto findToUpdate(Long id, String current) {
        Article article = find(id);
        article.confirmWriter(current);
        return extractDto(article);
    }

    @Transactional
    public Article like(Long id) {
        Article article = find(id);
        article.like();
        return article;
    }

    @Transactional
    public void dislike(Long id) {
        Article article = find(id);
        article.dislike();
    }

    @Transactional
    public void update(Long id, String current, String title, String content, boolean publicized) {
        Article article = find(id);
        article.update(current, title, content, publicized);
    }

    @Transactional
    public Long delete(Long id, String current) {
        Article article = find(id);
        article.beforeDelete(current);
        articleRepository.delete(article);
        return article.getTopic().getId();
    }

    public List<ArticleDto> findPublicByWriter(String username) {
        List<Article> articles = articleRepository.findByWriterAndPublicizedOrderByRatingDesc(username, true);
        return articles.stream().map(this::extractDto).collect(Collectors.toList());
    }

    public List<ArticleDto> findPrivateByWriter(String username) {
        List<Article> articles = articleRepository.findByWriterAndPublicizedOrderByCreatedAtAsc(username, false);
        return articles.stream().map(this::extractDto).collect(Collectors.toList());
    }

    private ArticleDto extractDto(Article a) {
        return new ArticleDto(a.getId(), a.getWriter(), a.getTitle(), a.getContent(), a.isPublicized(),
                a.getCreatedAt(), a.getRead(), a.getLikes(), a.topicSummary(), a.replySummaries());
    }

}
