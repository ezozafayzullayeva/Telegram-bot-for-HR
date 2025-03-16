package com.example.ipro_hr.service;


import com.example.ipro_hr.constants.BotConstants;
import com.example.ipro_hr.payload.BotState;
import com.example.ipro_hr.repository.UserRepository;
import com.example.ipro_hr.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    private final RestTemplate restTemplate;
    private final Map<Long, BotState> userStates = new HashMap<>();
    private final String channelId="-1002024429715";


    public void setUserState(Long chatId, BotState state) {
        userStates.put(chatId, state);
    }

    public BotState getUserState(Long chatId) {
        return userStates.getOrDefault(chatId, BotState.START);
    }

    public static String getChatId(Update update) {
        if (update.hasMessage()) {
            return update.getMessage().getChatId().toString();
        } else if (update.hasCallbackQuery()) {
            return update.getCallbackQuery().getFrom().getId().toString();
        }
        return "";
    }

    public static Long longChatId(Update update) {
        if (update.hasMessage()) {
            return update.getMessage().getChatId();
        } else if (update.hasCallbackQuery()) {
            return update.getCallbackQuery().getMessage().getChatId();
        }
        return null;
    }

    public void saveUser(Update update) {
        String chatId = getChatId(update);
        if (userRepository.existsByChatId(chatId)) {
            User user = userRepository.findByChatId(chatId);
            if (user.getFirstName() != null && user.getLastName() != null && user.getProfession()!=null
                    && user.getLevel()!=null && user.getPortfolioLink() != null &&
                    user.getPhoneNumber() != null && user.getSalary() != null
                    && user.getEmploymentType() != null && user.getLocation() != null) {
                setUserState(longChatId(update), BotState.ALREADY_REGISTRATED);
                me(update);
            } else {
                setUserState(longChatId(update), BotState.FIRST_NAME);
                userFirstName(update);
            }
        } else {
            User user = User.builder().chatId(chatId).build();
            userRepository.save(user);
            setUserState(longChatId(update), BotState.FIRST_NAME);
            userFirstName(update);
        }
    }
    private void me(Update update) {
        String chatId = getChatId(update);
        User user = userRepository.findByChatId(chatId);
        String text = "*Sizning malumotlaringiz \n*" +
                "Ismingiz: " + user.getFirstName() +"\n"+
                "Familiyangiz: " + user.getLastName() +"\n"+
                "Kasbingiz:  *" + user.getProfession()  +"\n"+
                "Darajangiz: " + user.getLevel() +"\n"+
                "Maoshingiz : " + user.getSalary()+"*" +
                "Portfolio linki: " + user.getPortfolioLink() +"\n"+
                "Ish turi: " + user.getEmploymentType()+"\n"+
                "Manzilingiz: " + user.getLocation() +"\n"+
                "Telefon raqam: " + user.getPhoneNumber()+"*";
//                "Rezume : " + user.getResume()+"*";

        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setChatId(chatId);
        editMessageText.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
        editMessageText.setReplyMarkup(forMe());
        editMessageText.enableMarkdown(true);
        editMessageText.setText(text);

        restTemplate.postForObject(BotConstants.EDIT_MESSAGE, editMessageText, Object.class);
        setUserState(longChatId(update), BotState.ALREADY_REGISTRATED);
    }



    private InlineKeyboardMarkup forMe() {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        InlineKeyboardButton button1 = new InlineKeyboardButton();
//        InlineKeyboardButton button2 = new InlineKeyboardButton();

        button1.setText("♻\uFE0FQayta ro'yxatdan o'tish");
//        button2.setText("To'lov qilish");

        button1.setCallbackData("userReRegister");
//        button2.setCallbackData("Tulov");

        List<InlineKeyboardButton> row1 = new ArrayList<>();
//        List<InlineKeyboardButton> row2 = new ArrayList<>();

        row1.add(button1);
//        row2.add(button2);

        rowsInline.add(row1);
//        rowsInline.add(row2);

        markupInline.setKeyboard(rowsInline);

        return markupInline;
    }

    public void userReRegister(Update update) {
        setUserState(longChatId(update), BotState.FIRST_NAME);
        userFirstName(update);
    }

    public void userFirstName(Update update) {
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setText("*Ro'yxatdan o'tishni boshladingiz *\n" +
                "Ismingizni kiriting ");
        editMessageText.setChatId(getChatId(update));
        editMessageText.enableMarkdown(true);
        editMessageText.setMessageId(update.getCallbackQuery().getMessage().getMessageId());
        restTemplate.postForObject(BotConstants.EDIT_MESSAGE, editMessageText, Object.class);
        setUserState(longChatId(update), BotState.LAST_NAME);
    }

    public void userLastName(Update update) {
        User user = userRepository.findByChatId(getChatId(update));
        String firstName=update.getMessage().getText();
        user.setFirstName(firstName);
        userRepository.save(user);
        SendMessage sendMessage = new SendMessage(getChatId(update), "\uD83D\uDCBBFamiliyangizni kiriting : ");
        sendMessage.setReplyMarkup(new ReplyKeyboardRemove(true));
        restTemplate.postForObject(BotConstants.FOR_MESSAGE, sendMessage, Object.class);
        setUserState(longChatId(update), BotState.PROFESSION);
    }

    public void userProfession(Update update) {
        User user = userRepository.findByChatId(getChatId(update));
        String lastName=update.getMessage().getText();
        user.setLastName(lastName);
        userRepository.save(user);
        SendMessage sendMessage = new SendMessage(getChatId(update), "\uD83D\uDCBBKasbingizni kiriting : ");
        sendMessage.setReplyMarkup(new ReplyKeyboardRemove(true));
        restTemplate.postForObject(BotConstants.FOR_MESSAGE, sendMessage, Object.class);
        setUserState(longChatId(update), BotState.LEVEL);
    }

//    public void sendResume(Update update) {
//        User user = userRepository.findByChatId(getChatId(update));
//        String profession=update.getMessage().getText();
//        user.setProfession(profession);
//        userRepository.save(user);
//        SendMessage sendMessage = new SendMessage(getChatId(update),
//                "\uD83D\uDCF11-Rezumengizni yuboring: ");
//        // sendMessage.setReplyMarkup(generateMarkup());
//        restTemplate.postForObject(BotConstants.FOR_MESSAGE, sendMessage, Object.class);
//        setUserState(longChatId(update), BotState.LOCATION);
//    }
    public void userLevel(Update update) {
        User user = userRepository.findByChatId(getChatId(update));
//        Location location= Location.valueOf(update.getMessage().getText()); //enum classdan foydalanganda
        String profession= update.getMessage().getText();
        user.setProfession(profession);
        userRepository.save(user);
        SendMessage sendMessage = new SendMessage(getChatId(update), "Darajangizni kiriting" + "\n"+
            "Masalan: Junior ");
//                "Junior+ " + "\n"+
//                "Middle " + "\n"+
//                "Middle+ " + "\n"+
//                "Senior : ");
        sendMessage.setReplyMarkup(new ReplyKeyboardRemove(true));
        restTemplate.postForObject(BotConstants.FOR_MESSAGE, sendMessage, Object.class);
        setUserState(longChatId(update), BotState.LOCATION);
    }
    public void location(Update update) {
        User user = userRepository.findByChatId(getChatId(update));
        String level=update.getMessage().getText();
        user.setLevel(level);
        userRepository.save(user);
        SendMessage sendMessage = new SendMessage(getChatId(update), "\uD83C\uDF0DManzilingizni kiriting:" + "\n"+
                "Masalan: Toshkent shahri ");
//                "1. Toshkent shahri" + "\n"+
//                "2. Samarqand" + "\n"+
//                "3. Qashqadaryo" + "\n"+
//                "4. Surxondaryo" + "\n"+
//                "5. Sirdaryo" + "\n"+
//                "6. Jizzax" + "\n"+
//                "7. Navoiy" + "\n"+
//                "8. Buxoro" + "\n"+
//                "9. Andijon" + "\n"+
//                "10. Farg'ona" + "\n"+
//                "11. Namangan" + "\n"+
//                "12. Xiva" + "\n"+
//                "13. Qoraqalpog'iston Respublikasi: ");
        sendMessage.setReplyMarkup(new ReplyKeyboardRemove(true));
        restTemplate.postForObject(BotConstants.FOR_MESSAGE, sendMessage, Object.class);
        setUserState(longChatId(update), BotState.PORTFOLIO);
    }



    public void userPortfolio(Update update) {
        User user = userRepository.findByChatId(getChatId(update));
//        Level level= Level.valueOf(update.getMessage().getText());
        String location=update.getMessage().getText();
        user.setLocation(location);
        userRepository.save(user);
        SendMessage sendMessage = new SendMessage(getChatId(update), "\uD83D\uDDC2Portfoliongiz silkasini yuboring!");
        sendMessage.setReplyMarkup(new ReplyKeyboardRemove(true));
        restTemplate.postForObject(BotConstants.FOR_MESSAGE, sendMessage, Object.class);
        setUserState(longChatId(update), BotState.SALARY);
    }

    public void userSalary(Update update) {
        User user = userRepository.findByChatId(getChatId(update));
        String portfolioLink = update.getMessage().getText();
        user.setPortfolioLink(portfolioLink);
        userRepository.save(user);
        SendMessage sendMessage = new SendMessage(getChatId(update), "\uD83D\uDCB5Qancha maosh olishni xohlaysiz?");
//                "1-5 mln, " + "\n"+
//                "5-10 mln, " +"\n"+
//                "10-15 mln, " +"\n"+
//                "15-20 mln, " +"\n"+
//                "20 mln+: ");
        sendMessage.setReplyMarkup(new ReplyKeyboardRemove(true));
        restTemplate.postForObject(BotConstants.FOR_MESSAGE, sendMessage, Object.class);
        setUserState(longChatId(update), BotState.EMPLOYMENT_TYPE);
    }

    public void employment(Update update) {
        User user = userRepository.findByChatId(getChatId(update));
        String salary = update.getMessage().getText();
        user.setSalary(salary);
        userRepository.save(user);
        SendMessage sendMessage = new SendMessage(getChatId(update), "\uD83D\uDD51Qanday tartibda ishlamoqchisiz?" + "\n"+
                "Full time, " + "\n"+
                "Part time, " + "\n"+
                "Online ");
        sendMessage.setReplyMarkup(new ReplyKeyboardRemove(true));
        restTemplate.postForObject(BotConstants.FOR_MESSAGE, sendMessage, Object.class);
        setUserState(longChatId(update), BotState.USER_PHONE_NUMBER);
    }

    private ReplyKeyboard replyKeyboard() {
        return new ReplyKeyboardMarkup();
    }
    public void userPhoneNumber(Update update) {
        User user = userRepository.findByChatId(getChatId(update));
        String employment=update.getMessage().getText();
        user.setEmploymentType(employment);
        userRepository.save(user);

//        // Reply keyboard yaratamiz va kontakt yuborish tugmasini qo'shamiz
//        ReplyKeyboardMarkup replyKeyboard = new ReplyKeyboardMarkup();
//        List<KeyboardRow> keyboard = new ArrayList<>();
//
//        KeyboardRow row = new KeyboardRow();
//        KeyboardButton contactButton = new KeyboardButton("Telefonimni yuborish");
//        contactButton.setRequestContact(true); // Bu tugma bosilganda telefon raqami avtomatik yuboriladi
//        row.add(contactButton);
//        keyboard.add(row);
//        replyKeyboard.setKeyboard(keyboard);
//        replyKeyboard.setResizeKeyboard(true);
//        replyKeyboard.setOneTimeKeyboard(true);

        SendMessage sendMessage = new SendMessage(getChatId(update),
                "\uD83D\uDCF11-Telefon raqamingizni kiriting:\n" +
                        "Namuna: +998XXYYYYYYY \n" +
                        "Iltimos o'zingizning raqamingizni to'g'ri tartibda kiriting, \n chunki siz yuborgan telefon raqam orqali HR siz bn bog'lanadi!");
//         sendMessage.setReplyMarkup(generateMarkup());
//         sendMessage.setReplyMarkup(replyKeyboard);
        restTemplate.postForObject(BotConstants.FOR_MESSAGE, sendMessage, Object.class);
        setUserState(longChatId(update), BotState.REGISTRATION_FINISHED);
//        user.setPhoneNumber(update.getMessage().getContact().getPhoneNumber());
//        userRepository.save(user);
    }


    public void incorrectPhone(Update update) {
        registerDone(update);
    }

    public void registerDone(Update update) {
        User user = userRepository.findByChatId(getChatId(update));
        if (update.getMessage().hasText()) {
            if (isValidPhoneNumber(update.getMessage().getText())) {
                String phoneNumber = update.getMessage().getText();
                user.setPhoneNumber(phoneNumber);
                executeRegisterDone(update, user);
            } else {
                SendMessage sendMessage = new SendMessage(getChatId(update),
                        "Telefon raqam formati xato. Qayta kiriting:");
                // sendMessage.setReplyMarkup(generateMarkup());
                restTemplate.postForObject(BotConstants.FOR_MESSAGE, sendMessage, Object.class);
                setUserState(longChatId(update), BotState.INCORRECT_PHONE);
            }
        } else if (update.getMessage().hasText()) {
            String phoneNumber = update.getMessage().getText();
            phoneNumber = phoneNumber.startsWith("+") ? phoneNumber : "+" + phoneNumber;
            user.setPhoneNumber(update.getMessage().getText());
            executeRegisterDone(update, user);
        }
    }

    public void executeRegisterDone(Update update,User user) {
        userRepository.findByChatId(getChatId(update));

        String phoneNumber=update.getMessage().getText();
        user.setPhoneNumber(phoneNumber);
        userRepository.save(user);

        SendMessage sendMessage1 = new SendMessage(getChatId(update),
                "Xurmatli foydalanuvchi, siz muvaffaqiyatli ro'yxatdan o'tdingiz✅");
        SendMessage sendMessage2 = new SendMessage(getChatId(update),
                "\uD83D\uDC47\uD83D\uDC47\uD83D\uDC47Sizning ma'lumotlaringiz\uD83D\uDC47\uD83D\uDC47\uD83D\uDC47 \n\n" +
                        "\uD83D\uDC64Ism:  " + user.getFirstName() + "\n" +
                        "\uD83D\uDC64Familiya:  " + user.getLastName() + "\n" +
                        "\uD83D\uDCBBKasb:  " + user.getProfession() + "\n" +
                        "\uD83D\uDCC8Daraja :  " + user.getLevel() + "\n" +
                        "\uD83D\uDCF1Telefon raqam:   " + user.getPhoneNumber() + "\n" +
                        "\uD83C\uDFDEManzil:  " + user.getLocation() + "\n" +
                        "\uD83D\uDDC2Portfolio linki:  " + user.getPortfolioLink() + "\n" +
                        "\uD83D\uDCB5Maosh:  " + user.getSalary() + "\n" +
                        "\uD83D\uDD51Ish shakli:  " + user.getEmploymentType() );
//                        "\uD83D\uDCF1Rezumengizni yuboring: " + user.getResume());
        sendMessage1.enableMarkdown(false);
        sendMessage1.setReplyMarkup(new ReplyKeyboardRemove(true));


        sendMessage2.enableMarkdown(false);

        setUserState(longChatId(update), BotState.REGISTRATION_FINISHED);
        try {
            restTemplate.postForObject(BotConstants.FOR_MESSAGE, sendMessage1, Object.class);
            restTemplate.postForObject(BotConstants.FOR_MESSAGE, sendMessage2, Object.class);
//            setUserState(longChatId(update), BotState.REGISTRATION_FINISHED);
            setUserState(longChatId(update), BotState.SEND_DATA_TO_CHANNEL);
        } catch (Exception e) {
            e.printStackTrace();
        }
       sendUserDataToChannel(update, user);

    }
    private void sendUserDataToChannel(Update update, User user) {
        userRepository.findByChatId(getChatId(update));
        String phoneNumber=update.getMessage().getText();

        user.setPhoneNumber(phoneNumber);
        userRepository.save(user);
        StringBuilder sb = new StringBuilder();
        sb.append("Yangi ro'yxatdan o'tgan foydalanuvchi:\n")
                .append("\uD83D\uDC64Xodim: ").append(user.getFirstName()).append(" ").append(user.getLastName()).append("\n")
                .append("\uD83D\uDCF1Telegram: t.me/").append(update.getMessage().getFrom().getUserName()).append("\n")
                .append("\uD83D\uDCBBKasbi: ").append(user.getProfession()).append("\n")
                .append("\uD83C\uDFDEManzili: ").append(user.getLocation()).append("\n")
                .append("\uD83D\uDCC8Darajasi: ").append(user.getLevel()).append("\n")
                .append("\uD83D\uDDC2Portfolio havolasi: ").append(user.getPortfolioLink()).append("\n")
                .append("\uD83D\uDCB5Maosh: ").append(user.getSalary()).append("\n")
                .append("\uD83D\uDD51Ishlash vaqti: ").append(user.getEmploymentType()).append("\n")
//                .append("\uD83D\uDCF1Rezume: ").append(user.getResume()).append("\n")
                .append("\uD83D\uDCF1Telefon raqami: ").append(user.getPhoneNumber());
        SendMessage message = new SendMessage();
        message.setChatId(channelId);
        message.setText(sb.toString());
        try {
            restTemplate.postForObject(BotConstants.FOR_MESSAGE, message, String.class);
            setUserState(longChatId(update), BotState.REGISTRATION_FINISHED);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean isValidPhoneNumber(String phoneNumber) {
        String regex = "\\+998[1-9]\\d{8}";

        Pattern pattern = Pattern.compile(regex);

        Matcher matcher = pattern.matcher(phoneNumber);

        return matcher.matches();
    }

}
