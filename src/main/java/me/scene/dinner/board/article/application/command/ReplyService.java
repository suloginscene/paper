package me.scene.dinner.board.article.application.command;

import lombok.RequiredArgsConstructor;
import me.scene.dinner.board.article.application.command.request.ReplyCreateRequest;
import me.scene.dinner.board.article.application.command.request.ReplyDeleteRequest;
import me.scene.dinner.board.article.domain.article.repository.ArticleRepository;
import me.scene.dinner.board.article.domain.article.model.Reply;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Service
@Transactional
@RequiredArgsConstructor
public class ReplyService {

    private final ArticleRepository repository;


    public void save(ReplyCreateRequest request) {
        Long articleId = request.getArticleId();
        String username = request.getUsername();
        String content = request.getContent();

        Reply reply = new Reply(username, content);

        List<Reply> replies = repository.find(articleId).getReplies();
        replies.add(reply);

        // TODO notification
    }

    public void delete(ReplyDeleteRequest request) {
        Long articleId = request.getArticleId();
        Long replyId = request.getReplyId();
        String username = request.getUsername();

        List<Reply> replies = repository.find(articleId).getReplies();

        Optional<Reply> optionalReply = replies.stream()
                .filter(reply -> reply.getId().equals(replyId))
                .findAny();

        optionalReply.ifPresent(reply -> {
            reply.getOwner().identify(username);
            replies.remove(reply);
        });
    }

}