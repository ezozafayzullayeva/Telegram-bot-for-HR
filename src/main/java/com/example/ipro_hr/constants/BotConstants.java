package com.example.ipro_hr.constants;

public interface BotConstants {
    String BOT_TOKEN = "7118624771:AAE1UxOQMEvZCxjvNNO-f4fkFqhV7Qrt73s";
    String BOT_USERNAME = "t.me/iPro_hr_bot";
    String URL = "https://api.telegram.org/bot";
    String FOR_MESSAGE = URL + BOT_TOKEN + "/sendMessage";
    String EDIT_MESSAGE = URL + BOT_TOKEN + "/editMessageText";
    String FORWARD = URL + BOT_TOKEN + "/forwardMessage";
    String FOR_DELETE_MESSAGE = URL + BOT_TOKEN + "/deleteMessage";
    String FOR_DOCUMENT = URL + BOT_TOKEN + "/sendDocument";
}
