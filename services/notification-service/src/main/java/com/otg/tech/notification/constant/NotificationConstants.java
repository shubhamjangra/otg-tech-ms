package com.otg.tech.notification.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class NotificationConstants {

    // Application Constants
    public static final String USER_ID = "userId";
    public static final String LANGUAGE = "language";
    public static final String CUSTOMER = "customer";
    public static final String EVENT = "event";
    public static final String MOBILE_NO = "mobileNo";
    public static final String EMAIL_KEY = "email";

    // Headers Constants
    public static final String API_URL = "apiUrl";
    public static final String API_USER = "apiuser";
    public static final String API_KEY = "apikey";

    // EMAIL Constants
    public static final String ATTACHMENT = "Attachment";
    public static final String FILE_NAME = "FileName";
    public static final String FILE_MIME_TYPE = "FileMimeType";
    public static final String FILE_DATA_CONTENT = "FileDataContent";
    public static final String TO = "TO";
    public static final String BCC = "BCC";
    public static final String SUBJECT = "Subject";
    public static final String TEXT = "Text";
    public static final String PRIORITY = "Priority";

    // SMS Constants
    public static final String MESSAGE = "Message";
    public static final String MOBILE_NUMBER = "MobileNumber";
    public static final String MESSAGE_TEXT = "MessageText";
    public static final String GROUP_ID = "GroupID";
    public static final String CONTENT_TYPE = "ContentType";
    public static final String NATIONAL_OR_INTERNATIONAL = "NationalorInternational";
    public static final String MESSAGE_TYPE = "MessageType";
    public static final String IS_OTP_MESSAGE = "IsOTPMessage";
    public static final String LANGUAGE_ID = "LanguageId";
    public static final String BANK = "BANK";
    public static final String CONTENT_TYPE_VALUE = "1";
    public static final String N_OR_I_VALUE = "1";
    public static final String MESSAGE_TYPE_VALUE = "S";
    public static final String IS_OTP_MESSAGE_VALUE = "1";
    public static final String LANGUAGE_ID_VALUE = "en";

    // EMAIL & SMS
    public static final String CHANNEL_KEY = "channel";
    public static final String FILE_NAME_KEY = "fileName";
    public static final String MIME_TYPE_KEY = "mimeType";
    public static final String DATA_CONTENT_KEY = "dataContent";
    public static final String REQUEST_ID = "RequestId";
    public static final String CHANNEL = "Channel";
    public static final String FAILED_CODE = "99";
    public static final String FAILED_DESC = "failed";

    // Provider Constants
    public static final String EMAIL = "EMAIL";
    public static final String SMS = "SMS";
    public static final String PUSH = "PUSH";
    public static final String OTG_EMAIL = "OTG_EMAIL";
    public static final String OTG_SMS = "OTG_SMS";
    public static final String OTG_PUSH = "OTG_PUSH";
}
