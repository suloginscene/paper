package me.scene.dinner.domain.board.magazine;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class MagazineController {

    public static final String FORM = "/magazine-form";
    public static final String URL = "/magazines";

    @GetMapping(FORM)
    public String shipMagazineForm() {
        return "page/board/magazine/form";
    }

    @GetMapping(URL + "/{title}")
    public String showMagazine(@PathVariable String title, Model model) {
        model.addAttribute("title", title);
        return "page/board/magazine/view";
    }

}
