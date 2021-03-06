package me.scene.paper.service.board.topic.ui.form;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;


@Data
public class TopicForm {

    private final Long magazineId;

    @NotBlank(message = "제목을 적어주세요.")
    @Length(max = 20, message = "제목은 최대 20자까지 가능합니다.")
    private String title;

    @NotBlank(message = "짧은 소개를 적어주세요.")
    @Length(max = 30, message = "짧은 소개는 최대 30자까지 가능합니다.")
    private String shortExplanation;

    @Length(max = 255, message = "자세한 소개는 최대 255자까지 가능합니다.")
    private String longExplanation;

}
