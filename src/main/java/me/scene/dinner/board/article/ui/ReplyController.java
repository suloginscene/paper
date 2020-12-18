package me.scene.dinner.board.article.ui;

import lombok.RequiredArgsConstructor;
import me.scene.dinner.account.domain.account.Account;
import me.scene.dinner.board.article.command.application.ReplyService;
import me.scene.dinner.common.security.Current;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class ReplyController {

    private final ReplyService replyService;

    @PostMapping("/replies")
    public String create(@Current Account current, @RequestParam Long articleId, @RequestParam String content) {
        replyService.save(current.getUsername(), articleId, content);
        return "redirect:" + ("/articles/" + articleId);
    }

    @DeleteMapping("/replies")
    public String delete(@Current Account current, @RequestParam Long articleId, @RequestParam Long replyId) {
        replyService.delete(current.getUsername(), articleId, replyId);
        return "redirect:" + ("/articles/" + articleId);
    }

}