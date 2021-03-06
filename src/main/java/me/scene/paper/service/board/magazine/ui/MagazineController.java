package me.scene.paper.service.board.magazine.ui;

import lombok.RequiredArgsConstructor;
import me.scene.paper.service.board.magazine.application.command.MagazineService;
import me.scene.paper.service.board.magazine.application.command.request.MagazineCreateRequest;
import me.scene.paper.service.board.magazine.application.command.request.MagazineUpdateRequest;
import me.scene.paper.service.board.magazine.application.query.MagazineQueryService;
import me.scene.paper.service.board.magazine.application.query.dto.MagazineExtendedLink;
import me.scene.paper.service.board.magazine.application.query.dto.MagazineToUpdate;
import me.scene.paper.service.board.magazine.application.query.dto.MagazineView;
import me.scene.paper.service.board.magazine.ui.form.MagazineForm;
import me.scene.paper.service.board.magazine.ui.form.MagazineUpdateForm;
import me.scene.paper.common.framework.security.Principal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

import javax.validation.Valid;
import java.util.List;


@Controller
@RequiredArgsConstructor
public class MagazineController {

    private final MagazineService service;
    private final MagazineQueryService query;


    @GetMapping("/magazine-form")
    public String shipMagazineForm(Model model) {
        MagazineForm magazineForm = new MagazineForm();

        model.addAttribute("magazineForm", magazineForm);
        return "page/board/magazine/form";
    }

    @PostMapping("/magazines")
    public String createMagazine(@Principal String username,
                                 @Valid MagazineForm form, Errors errors) {

        if (errors.hasErrors()) return "page/board/magazine/form";

        String title = form.getTitle();
        String shortExplanation = form.getShortExplanation();
        String longExplanation = form.getLongExplanation();
        String policy = form.getPolicy();

        MagazineCreateRequest request = new MagazineCreateRequest(username, title, shortExplanation, longExplanation, policy);
        Long id = service.save(request);

        return "redirect:" + ("/magazines/" + id);
    }


    @GetMapping("/magazines/{id}")
    public String showMagazine(@PathVariable Long id, Model model) {
        MagazineView magazine = query.view(id);

        model.addAttribute("magazine", magazine);
        return "page/board/magazine/view";
    }

    @GetMapping("/magazines")
    public String showList(Model model) {
        List<MagazineExtendedLink> magazines = query.allLinks();

        model.addAttribute("magazines", magazines);
        return "page/board/magazine/list";
    }


    @GetMapping("/magazines/{id}/form")
    public String updateForm(@PathVariable Long id,
                             @Principal String username, Model model) {
        MagazineToUpdate magazine = query.toUpdate(id, username);

        MagazineUpdateForm updateForm = new MagazineUpdateForm(
                magazine.getId(),
                magazine.getPolicy(),
                magazine.getTitle(),
                magazine.getShortExplanation(),
                magazine.getLongExplanation()
        );

        model.addAttribute("updateForm", updateForm);
        return "page/board/magazine/update";
    }

    @PutMapping("/magazines/{id}")
    public String update(@PathVariable Long id,
                         @Principal String username,
                         @Valid MagazineUpdateForm form, Errors errors) {

        if (errors.hasErrors()) return "page/board/magazine/update";

        String title = form.getTitle();
        String shortExplanation = form.getShortExplanation();
        String longExplanation = form.getLongExplanation();

        MagazineUpdateRequest request = new MagazineUpdateRequest(username, title, shortExplanation, longExplanation);
        service.update(id, request);

        return "redirect:" + ("/magazines/" + id);
    }


    @DeleteMapping("/magazines/{id}")
    public String delete(@PathVariable Long id,
                         @Principal String username) {

        service.delete(id, username);
        return "redirect:" + ("/");
    }

}
