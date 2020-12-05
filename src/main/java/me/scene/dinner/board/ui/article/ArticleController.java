package me.scene.dinner.board.ui.article;

import lombok.RequiredArgsConstructor;
import me.scene.dinner.account.application.CurrentUser;
import me.scene.dinner.account.domain.account.Account;
import me.scene.dinner.board.application.article.dto.ArticleDto;
import me.scene.dinner.board.application.article.ArticleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;

    @GetMapping("/article-form")
    public String shipArticleForm(@RequestParam Long topicId, Model model) {
        ArticleForm articleForm = new ArticleForm();
        articleForm.setTopicId(topicId);
        model.addAttribute("articleForm", articleForm);
        return "page/board/article/form";
    }

    @PostMapping("/articles")
    public String createArticle(@CurrentUser Account current, @Valid ArticleForm form, Errors errors) {
        if (errors.hasErrors()) return "page/board/article/form";

        Long id = articleService.save(form.getTopicId(), current.getUsername(), form.getTitle(), form.getContent(), form.isPublicized());
        return "redirect:" + ("/articles/" + id);
    }

    @GetMapping("/articles/{articleId}")
    public String showArticle(@PathVariable Long articleId, @CurrentUser Account current, Model model) {
        String username = (current != null) ? current.getUsername() : "anonymousUser";
        ArticleDto article = articleService.read(articleId, username);
        model.addAttribute("article", article);
        return "page/board/article/view";
    }

    @GetMapping("/articles/{articleId}/form")
    public String updateForm(@PathVariable Long articleId, @CurrentUser Account current, Model model) {
        ArticleDto article = articleService.findToUpdate(articleId, current.getUsername());
        model.addAttribute("id", articleId);
        model.addAttribute("updateForm", updateForm(article));
        return "page/board/article/update";
    }

    private ArticleForm updateForm(ArticleDto a) {
        ArticleForm f = new ArticleForm();
        f.setTopicId(a.getTopic().getId());
        f.setTitle(a.getTitle());
        f.setContent(a.getContent());
        f.setStatus(a.getStatus());
        return f;
    }

    @PutMapping("/articles/{articleId}")
    public String update(@PathVariable Long articleId, @CurrentUser Account current, @Valid ArticleForm form, Errors errors) {
        if (errors.hasErrors()) return "redirect:" + ("/articles/" + articleId + "/form");

        articleService.update(articleId, current.getUsername(), form.getTitle(), form.getContent(), form.isPublicized());
        return "redirect:" + ("/articles/" + articleId);
    }

    @DeleteMapping("/articles/{articleId}")
    public String delete(@PathVariable Long articleId, @CurrentUser Account current) {
        Long topicId = articleService.delete(articleId, current.getUsername());
        return "redirect:" + ("/topics/" + topicId);
    }

    @GetMapping("/api/articles/{username}")
    public @ResponseBody List<ArticleDto> byUserPublic(@PathVariable String username) {
        return articleService.findPublicByWriter(username);
    }

    @GetMapping("/private-articles")
    public String byUserPrivate(@CurrentUser Account current, Model model) {
        model.addAttribute("articles", articleService.findPrivateByWriter(current.getUsername()));
        return "page/board/article/private";
    }

}
