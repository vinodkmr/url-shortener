package dev.vinodkmr.model;

import lombok.Getter;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;

@Setter
@Getter
public class BotResponse {

    private String resText;
    private BotCommand botCommand;

    public BotResponse(String resText) {
        this.resText = resText;
    }

    public BotResponse(String resText, BotCommand botCommand) {
        this.resText = resText;
        this.botCommand = botCommand;
    }

    public BotResponse(BotCommand botCommand) {
        this.botCommand = botCommand;
    }
}
