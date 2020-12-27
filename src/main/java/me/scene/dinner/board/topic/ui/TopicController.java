package me.scene.dinner.board.topic.ui;

import lombok.RequiredArgsConstructor;
import me.scene.dinner.board.topic.application.command.TopicService;
import me.scene.dinner.board.topic.application.command.request.TopicCreateRequest;
import me.scene.dinner.board.topic.application.command.request.TopicUpdateRequest;
import me.scene.dinner.board.topic.application.query.TopicQueryService;
import me.scene.dinner.board.topic.application.query.dto.TopicView;
import me.scene.dinner.board.topic.ui.form.TopicForm;
import me.scene.dinner.board.topic.ui.form.TopicUpdateForm;
import me.scene.dinner.common.security.Principal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;


@Controller
@RequiredArgsConstructor
public class TopicController {

    private final TopicService service;
    private final TopicQueryService queryService;


    @GetMapping("/topics/{id}")
    public String showTopic(@PathVariable Long id, Model model) {
        TopicView topic = queryService.find(id);

        model.addAttribute("topic", topic);
        return "page/board/topic/view";
    }


    @GetMapping("/topic-form")
    public String shipTopicForm(@RequestParam Long magazineId, Model model) {
        TopicForm topicForm = new TopicForm(magazineId);

        model.addAttribute("topicForm", topicForm);
        return "page/board/topic/form";
    }

    @PostMapping("/topics")
    public String createTopic(@Principal String username,
                              @Valid TopicForm form, Errors errors) {

        if (errors.hasErrors()) return "page/board/topic/form";

        Long magazineId = form.getMagazineId();
        String title = form.getTitle();
        String shortExplanation = form.getShortExplanation();
        String longExplanation = form.getLongExplanation();

        TopicCreateRequest request = new TopicCreateRequest(username, magazineId, title, shortExplanation, longExplanation);
        Long id = service.save(request);

        return "redirect:" + ("/topics/" + id);
    }


    @GetMapping("/topics/{id}/form")
    public String updateForm(@PathVariable Long id, Model model) {
        TopicView topic = queryService.find(id);

        TopicUpdateForm updateForm = new TopicUpdateForm(
                topic.getId(),
                topic.getTitle(),
                topic.getShortExplanation(),
                topic.getLongExplanation()
        );

        model.addAttribute("updateForm", updateForm);
        return "page/board/topic/update";
    }

    @PutMapping("/topics/{id}")
    public String update(@PathVariable Long id,
                         @Principal String username,
                         @Valid TopicUpdateForm form, Errors errors) {

        if (errors.hasErrors()) return "page/board/topic/update";

        String title = form.getTitle();
        String shortExplanation = form.getShortExplanation();
        String longExplanation = form.getLongExplanation();

        TopicUpdateRequest request = new TopicUpdateRequest(username, id, title, shortExplanation, longExplanation);
        service.update(request);

        return "redirect:" + ("/topics/" + id);
    }


    @DeleteMapping("/topics/{id}")
    public String delete(@PathVariable Long id,
                         @Principal String username) {

        Long magazineId = service.delete(id, username);

        return "redirect:" + ("/magazines/" + magazineId);
    }

}
