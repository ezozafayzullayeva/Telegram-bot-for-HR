
package com.example.ipro_hr.service;

import com.example.ipro_hr.constants.BotConstants;
import com.example.ipro_hr.payload.BotState;
import com.example.ipro_hr.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class WebhookService {

    private final UserRepository userRepository;
    private final RestTemplate restTemplate;
    private final UserService userService;

    private final Map<Long, BotState> userStates = new HashMap<>();

    public void setUserState(Long chatId, BotState state) {
        userStates.put(chatId, state);
    }

    public BotState getUserState(Long chatId) {
        return userStates.getOrDefault(chatId, BotState.START);
    }

    public static String getChatId(Update update) {
        return UserService.getChatId(update);
    }

    public static Long longChatId(Update update)  {
        return UserService.longChatId(update);
    }

    public void whenStart(Update update) {
        SendMessage sendMessage = new SendMessage(getChatId(update),
                "Assalomu alaykum hurmatli " + update.getMessage().getFrom().getFirstName() +
                        " bizning botimizga xush kelibsiz \n" +
                        "Bizning botimiz orqali siz o'z malumotlaringizni HR ga yuborishingiz mumkin");
        sendMessage.setReplyMarkup(forStart());
        restTemplate.postForObject(BotConstants.FOR_MESSAGE, sendMessage, Object.class);
        setUserState(longChatId(update), BotState.REGISTRATION);
    }

    private InlineKeyboardMarkup forStart() {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        InlineKeyboardButton button1 = new InlineKeyboardButton();
        button1.setText("Ro'yxatdan o'tish");
//        System.out.println("ruyxatdan utish boshlandi");
        button1.setCallbackData("Royxatdan_otish");

//        System.out.println("ruyxatdan utish boshlandi");
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(button1);
        rowsInline.add(row1);
        markupInline.setKeyboard(rowsInline);
        return markupInline;
    }

    public void getUpdate(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String message = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();

            if (message.equals("/start")) whenStart(update);
            else if (userService.getUserState(chatId).equals(BotState.FIRST_NAME)) {
                userService.userFirstName(update);
            }
            else if (userService.getUserState(chatId).equals(BotState.LAST_NAME)) {
                userService.userLastName(update);
            }
            else if (userService.getUserState(chatId).equals(BotState.PROFESSION)) {
                userService.userProfession(update);
            }
            else if (userService.getUserState(chatId).equals(BotState.LOCATION)) {
                userService.location(update);
            } else if (userService.getUserState(chatId).equals(BotState.LEVEL)) {
                userService.userLevel(update);
            } else if (userService.getUserState(chatId).equals(BotState.PORTFOLIO)) {
                userService.userPortfolio(update);
            } else if (userService.getUserState(chatId).equals(BotState.SALARY)) {
                userService.userSalary(update);
            } else if (userService.getUserState(chatId).equals(BotState.EMPLOYMENT_TYPE)) {
                userService.employment(update);
            }  else if (userService.getUserState(chatId).equals(BotState.USER_PHONE_NUMBER)) {
                userService.userPhoneNumber(update);
            } else if (userService.getUserState(chatId).equals(BotState.INCORRECT_PHONE)) {
                userService.incorrectPhone(update);
            }else if (userService.getUserState(chatId).equals(BotState.REGISTRATION_FINISHED)) {
                userService.registerDone(update);
//                userService.sendUserDataToChannel(update, user);
            }
        }else if (update.hasCallbackQuery()) {
            String data = update.getCallbackQuery().getData();
            Long chatId = update.getCallbackQuery().getMessage().getChatId();
            if (getUserState(chatId).equals(BotState.REGISTRATION) && data.equals("Royxatdan_otish")) {
                userService.saveUser(update);

            } else if (getUserState(chatId).equals(BotState.REGISTRATION) && data.equals("userReRegister")) {
                userService.userReRegister(update);

            }
        }
    }
}
