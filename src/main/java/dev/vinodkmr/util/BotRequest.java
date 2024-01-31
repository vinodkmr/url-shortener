package dev.vinodkmr.util;

import java.util.Optional;

public enum BotRequest {
    SHRINK,
    ABOUT;

    public static Optional<BotRequest> getBotRequestByCommand(String command){
        for (BotRequest botRequest: BotRequest.values()){
            if(command.equals(botRequest.toString())){
                return Optional.of(botRequest);
            }
        }
        return Optional.empty();

    }
}
