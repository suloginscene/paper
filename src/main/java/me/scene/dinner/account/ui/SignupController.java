package me.scene.dinner.account.ui;

import me.scene.dinner.account.application.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;

@Controller
public class SignupController {

    private final AccountService accountService;
    private final AccountFormValidator accountFormValidator;

    @Autowired
    public SignupController(AccountService accountService, AccountFormValidator accountFormValidator) {
        this.accountService = accountService;
        this.accountFormValidator = accountFormValidator;
    }

    @InitBinder("accountForm")
    public void initBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(accountFormValidator);
    }


    @GetMapping("/signup")
    public String signupPage(Model model) {
        AccountForm accountForm = new AccountForm();
        model.addAttribute("accountForm", accountForm);
        return "page/account/signup";
    }

    @PostMapping("/signup")
    public String signupSubmit(@Valid AccountForm form, Errors errors) {
        if (errors.hasErrors()) return "page/account/signup";

        String email = form.getEmail();
        accountService.saveTemp(form.getUsername(), email, form.getPassword());
        return "redirect:" + ("/sent?email=" + email);
    }

    @GetMapping("/verify")
    public String verifyEmail(@RequestParam String email, @RequestParam String token, Model model) {
        accountService.transferToRegular(email, token);
        model.addAttribute("email", email);
        return "page/account/welcome";
    }

}
