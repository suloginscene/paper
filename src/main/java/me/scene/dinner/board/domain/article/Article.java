package me.scene.dinner.board.domain.article;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import me.scene.dinner.board.domain.common.NotOwnerException;
import me.scene.dinner.board.domain.topic.Topic;
import me.scene.dinner.tag.TaggedArticle;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.LAZY;
import static me.scene.dinner.board.domain.article.RatingType.DISLIKE;
import static me.scene.dinner.board.domain.article.RatingType.LIKE;
import static me.scene.dinner.board.domain.article.RatingType.READ;

@Entity
@Getter @EqualsAndHashCode(of = "id")
public class Article {

    @Id @GeneratedValue
    private Long id;

    private String writer;


    private String title;

    @Lob
    private String content;

    private boolean publicized;

    private LocalDateTime createdAt;


    private int read;

    private int likes;

    private int rating;

    @OneToMany(mappedBy = "article")
    private final Set<TaggedArticle> taggedArticles = new HashSet<>();


    @ManyToOne(fetch = LAZY)
    private Topic topic;

    @OneToMany(cascade = ALL, orphanRemoval = true) @JoinColumn(name = "article_id")
    private final List<Reply> replies = new ArrayList<>();


    protected Article() {
    }

    public static Article create(Topic topic, String writer, String title, String content, boolean publicized) {
        topic.getMagazine().checkAuthorization(writer);
        Article article = new Article();
        article.writer = writer;
        article.title = title;
        article.content = content;
        article.createdAt = LocalDateTime.now();
        article.topic = topic;
        article.publicized = publicized;
        topic.add(article);
        article.toggleWriterRegistration();
        return article;
    }

    public void update(String current, String title, String content, boolean publicized) {
        confirmWriter(current);
        this.title = title;
        this.content = content;
        this.publicized = publicized;
        toggleWriterRegistration();
    }

    private void rate(RatingType ratingType) {
        int point = ratingType.point();
        rating += point;
        topic.rate(point);
    }

    public void read() {
        read++;
        rate(READ);
    }

    public void like() {
        likes++;
        rate(LIKE);
    }

    public void dislike() {
        if (likes < 1) return;
        likes--;
        rate(DISLIKE);
    }

    public void beforeDelete(String current) {
        confirmWriter(current);
        topic.getMagazine().removeWriter(writer);
        topic.remove(this);
    }

    private void toggleWriterRegistration() {
        if (publicized) topic.getMagazine().addWriter(writer);
        else topic.getMagazine().removeWriter(writer);
    }


    public void confirmWriter(String current) {
        if (current.equals(writer)) return;
        throw new NotOwnerException(current);
    }


    public void add(Reply reply) {
        replies.add(reply);
    }

    public void remove(Reply reply) {
        replies.remove(reply);
    }

    public Optional<Reply> findReplyById(Long replyId) {
        return replies.stream().filter(r -> r.getId().equals(replyId)).findAny();
    }

    public TopicSummary topicSummary() {
        return new TopicSummary(topic.getId(), topic.getTitle());
    }

    public List<ReplySummary> replySummaries() {
        return replies.stream()
                .map(r -> new ReplySummary(r.getId(), r.getWriter(), r.getContent(), r.getCreatedAt()))
                .sorted(Comparator.comparing(ReplySummary::getCreatedAt))
                .collect(Collectors.toList());
    }

}
