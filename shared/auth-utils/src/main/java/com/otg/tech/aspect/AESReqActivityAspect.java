package com.otg.tech.aspect;

import com.otg.tech.annotation.AESActivity;
import com.otg.tech.annotation.AESOptionalActivity;
import com.otg.tech.auth.claims.PrincipalHelper;
import com.otg.tech.constant.CommonConstant;
import com.otg.tech.exception.BusinessException;
import com.otg.tech.util.commons.Utils;
import com.otg.tech.util.security.AES;
import com.otg.tech.util.security.RSA;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.otg.tech.enums.ErrorExceptionCodes.AES0401;
import static com.otg.tech.enums.ErrorExceptionCodes.AES0500;

@RestControllerAdvice
@Slf4j
@RequiredArgsConstructor
@SuppressWarnings("all")
public class AESReqActivityAspect extends RequestBodyAdviceAdapter {

    private final AES aes;
    private final RSA rsa;
    private final PrincipalHelper principalHelper;

    @Value("${aes.encryption.mandatory:false}")
    private boolean encryptionMandatory;
    @Value("${aes.optional.method.encryption:false}")
    private boolean optionalMethodEncryption;
    @Value("${aes.encryption.salt:User}")
    private String defaultSalt;
    @Value("${aes.key:55ca1032cb0229b004d11b3e43f390954c641fbe9733002eb92f639522e33acb}")
    private String aesKey;
    @Value("${rsa.private.key:MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAKAUZV+tjiNBKhlBZbKBnzeugpdYPhh5PbHanjV0aQ+LF7vetPYhbTiCVqA3a+Chmge44+prlqd3qQCYra6OYIe7oPVq4mETa1c/7IuSlKJgxC5wMqYKxYydb1eULkrs5IvvtNddx+9O/JlyM5sTPosgFHOzr4WqkVtQ71IkR+HrAgMBAAECgYAkQLo8kteP0GAyXAcmCAkA2Tql/8wASuTX9ITD4lsws/VqDKO64hMUKyBnJGX/91kkypCDNF5oCsdxZSJgV8owViYWZPnbvEcNqLtqgs7nj1UHuX9S5yYIPGN/mHL6OJJ7sosOd6rqdpg6JRRkAKUV+tmN/7Gh0+GFXM+ug6mgwQJBAO9/+CWpCAVoGxCA+YsTMb82fTOmGYMkZOAfQsvIV2v6DC8eJrSa+c0yCOTa3tirlCkhBfB08f8U2iEPS+Gu3bECQQCrG7O0gYmFL2RX1O+37ovyyHTbst4s4xbLW4jLzbSoimL235lCdIC+fllEEP96wPAiqo6dzmdH8KsGmVozsVRbAkB0ME8AZjp/9Pt8TDXD5LHzo8mlruUdnCBcIo5TMoRG2+3hRe1dHPonNCjgbdZCoyqjsWOiPfnQ2Brigvs7J4xhAkBGRiZUKC92x7QKbqXVgN9xYuq7oIanIM0nz/wq190uq0dh5Qtow7hshC/dSK3kmIEHe8z++tpoLWvQVgM538apAkBoSNfaTkDZhFavuiVl6L8cWCoDcJBItip8wKQhXwHp0O3HLg10OEd14M58ooNfpgt+8D8/8/2OOFaR0HzA+2Dm}")
    private String privateKey;
    @Value("${aes.encryption.noauth:test}")
    private String noAuth;

    @Override
    public boolean supports(MethodParameter methodParameter, Type targetType,
                            Class<? extends HttpMessageConverter<?>> converterType) {
        return hasAnnotation(methodParameter, AESActivity.class, encryptionMandatory)
                || hasAnnotation(methodParameter, AESOptionalActivity.class, optionalMethodEncryption);
    }

    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage inputMessage, MethodParameter parameter, Type targetType,
                                           Class<? extends HttpMessageConverter<?>> converterType) throws IOException {

        try (InputStream inputStream = inputMessage.getBody()) {
            List<String> noAuthList = Optional.ofNullable(inputMessage.getHeaders().get(CommonConstant.NO_AES_KEY))
                    .orElse(Collections.emptyList());
            if (!noAuthList.isEmpty() && noAuthList.contains(noAuth)) {
                return new DecryptHttpInputMessage(inputMessage.getHeaders(),
                        new ByteArrayInputStream(StreamUtils.copyToByteArray(inputStream)));
            }

            String randomAESKey = inputMessage.getHeaders().getFirst(CommonConstant.AES_KEY);
            String userId = (randomAESKey == null) ? getUserId() : defaultSalt;

            byte[] body = StreamUtils.copyToByteArray(inputStream);
            Map<String, String> reqJson = Utils.toJavaPojo(new String(body), Map.class);
            byte[] decryptedBody = getDecryptedBody(randomAESKey, userId, reqJson);

            return new DecryptHttpInputMessage(inputMessage.getHeaders(), new ByteArrayInputStream(decryptedBody));
        }
    }

    private String getUserId() {
        final var auIdentity = principalHelper.getIdentity();
        if ((encryptionMandatory || optionalMethodEncryption) && auIdentity == null) {
            throw new BusinessException(AES0401.getHttpStatus(), AES0401.getCode(), AES0401.getMessage());
        }
        return (auIdentity != null) ? auIdentity.getUserId() : defaultSalt;
    }

    private byte[] getDecryptedBody(String randomAESKey, String userId, Map<String, String> reqJson) {
        try {
            if (randomAESKey == null) {
                return aes.decrypt(aesKey, reqJson.get("data"), userId);
            } else {
                String decryptAES = rsa.decrypt(randomAESKey, privateKey);
                byte[] decryptedBody = aes.decryptWithoutSalt(reqJson.get("data"), decryptAES);
                if (decryptedBody == null) {
                    throw new BusinessException(AES0500.getHttpStatus(), AES0500.getCode(), AES0500.getMessage());
                }
                return decryptedBody;
            }
        } catch (Exception e) {
            throw new BusinessException(AES0500.getHttpStatus(), AES0500.getCode(), AES0500.getMessage());
        }
    }

    private boolean hasAnnotation(MethodParameter methodParameter, Class<? extends Annotation> annotation,
                                  boolean encryptionFlag) {
        return (methodParameter.hasMethodAnnotation(annotation)
                || methodParameter.getContainingClass().isAnnotationPresent(annotation)) && encryptionFlag;
    }
}
