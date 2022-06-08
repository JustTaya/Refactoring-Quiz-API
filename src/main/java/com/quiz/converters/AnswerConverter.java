package com.quiz.converters;

import com.quiz.dto.AnswerDto;
import com.quiz.entities.Answer;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AnswerConverter {
    public AnswerDto toAnswerDto(Answer answer) {
        AnswerDto answerDto = new AnswerDto();

        answerDto.setQuestionId(answer.getQuestionId());
        answerDto.setText(answer.getText());
        answerDto.setNextAnswerId(answer.getNextAnswerId());
        answerDto.setChanged(false);
        answerDto.setDeleted(false);

        return answerDto;
    }

    public List<AnswerDto> toAnswerDtoList(List<Answer> answerList) {
        List<AnswerDto> answerDtoList = new ArrayList<>();

        for (Answer answer : answerList) {
            answerDtoList.add(toAnswerDto(answer));
        }

        return answerDtoList;
    }
}
