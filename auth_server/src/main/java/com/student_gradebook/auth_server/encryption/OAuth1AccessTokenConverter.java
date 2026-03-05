package com.student_gradebook.auth_server.encryption;

import com.github.scribejava.core.model.OAuth1AccessToken;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.RequiredArgsConstructor;

@Converter
@RequiredArgsConstructor
public class OAuth1AccessTokenConverter implements AttributeConverter<OAuth1AccessToken, String> {

    private final EncryptionUtil encryptionUtil;

    @Override
    public String convertToDatabaseColumn(OAuth1AccessToken token) {
        if (token == null) {
            return null;
        }

        String plainText = token.getToken() + "|" + token.getTokenSecret();
        return encryptionUtil.encrypt(plainText);
    }

    @Override
    public OAuth1AccessToken convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return null;
        }
        String decrypted = encryptionUtil.decrypt(dbData);
        String[] parts = decrypted.split("\\|", 2);

        if (parts.length != 2) {
            throw new IllegalStateException("Invalid token format in database");
        }

        return new OAuth1AccessToken(parts[0], parts[1]);
    }
}