package practical.post.model.constants;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiConstants {
    public static final String UNDEFINED = "undefined";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_WHITE = "\u001B[37m";
    public static final String BREAK_LINE = "\n";
    public static final String TIME_ZONE_PACKAGE_NAME = "java.time.zone";
    public static final String PASSWORD_ALLCHARACTERS =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789~`!@#$%^&*()-=+[{]}\\|;:'\",<.>/?";
    public static final String PASSWORD_LETTERS_UPPER_CASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String PASSWORD_LETTERS_LOWER_CASE = "abcdefghijklmnopqrstuvwxyz";
    public static final String PASSWORD_DIGITS = "0123456789";
    public static final String PASSWORDCHARACTERS = "~`!@#$%^&*()-=+[{]}\\|;:'\",<.>/?";
    public static final Integer REQUIRED_MIN_PASSWORD_LENGTH = 8;
    public static final Integer REQUIRED_MIN_LETTERS_NUMBER_EVERY_CASE_IN_PASSWORD = 1;
    public static final Integer REQUIRED_MIN_DIGITS_NUMBER_IN_PASSWORD = 1;
    public static final Integer REQUIRED_MIN_CHARACTERS_NUMBER_IN_PASSWORD = 1;
}
