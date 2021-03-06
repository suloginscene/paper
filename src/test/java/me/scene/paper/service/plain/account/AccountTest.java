package me.scene.paper.service.plain.account;

import me.scene.paper.service.account.domain.account.model.Account;
import me.scene.paper.service.account.domain.account.model.Profile;
import me.scene.paper.service.account.domain.account.model.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Account")
class AccountTest {

    Account account;

    @BeforeEach
    void create() {
        account = new Account("username", "email", "encodedPassword");
    }


    @Nested class OnCreate {
        @Test
        void isUser() {
            Role role = account.getRole();
            assertThat(role).isEqualTo(Role.USER);
        }

        @Test
        void hasEmptyProfile() {
            Profile profile = account.getProfile();
            assertThat(profile).isNotNull();
        }
    }

}
