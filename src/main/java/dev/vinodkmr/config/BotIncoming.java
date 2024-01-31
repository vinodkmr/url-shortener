package dev.vinodkmr.config;

import dev.vinodkmr.model.BotResponse;
import dev.vinodkmr.service.BotService;
import dev.vinodkmr.util.AppUtil;
import dev.vinodkmr.util.BotRequest;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
public class BotIncoming extends TelegramLongPollingBot {

    private final String botName;

    public BotIncoming(String botToken, String botName) {
        super(botToken);
        this.botName = botName;
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if(!update.hasMessage()) return;
        // We check if the update has a message and the message has text
        if (update.hasMessage() && update.getMessage().hasText()) {
            String text = update.getMessage().getText();
            if(AppUtil.checkForValidURL(text)){
                BotResponse botResponse = BotService.shrinkUrl(text);
                sendMessage(update, botResponse);
                return;
            }

            String command = text.replace("/","").toUpperCase();
            log.info("Command:{}",command);
            Optional<BotRequest> botRequest = BotRequest.getBotRequestByCommand(command);
            botRequest.ifPresentOrElse(
                    br -> sendMessage(update, BotService.getResByBotRequest(br)),
                    () -> sendMessage(update,new BotResponse("Invalid Request"))
            );
        }
    }

    private void sendMessage(Update update, BotResponse botResponse) {
        SendMessage message = new SendMessage(); // Create a SendMessage object with mandatory fields
        message.setChatId(update.getMessage().getChatId().toString());
        sendRes(message,botResponse);
    }

    private void sendRes(SendMessage message, BotResponse botResponse) {
        try {
            message.setText(botResponse.getResText());
            execute(message); // Call method to send the message
        } catch (TelegramApiException e) {
            log.error("Error Sending a message",e);
        }
    }
}