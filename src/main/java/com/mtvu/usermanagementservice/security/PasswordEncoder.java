package com.mtvu.usermanagementservice.security;
import io.quarkus.elytron.security.common.BcryptUtil;
import org.wildfly.security.password.Password;
import org.wildfly.security.password.PasswordFactory;
import org.wildfly.security.password.interfaces.BCryptPassword;
import org.wildfly.security.password.util.ModularCrypt;

import javax.enterprise.context.ApplicationScoped;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

@ApplicationScoped
public class PasswordEncoder {

    public String encode(String data) {
        return BcryptUtil.bcryptHash(data);
    }

    public boolean verify(String originalPwd, String encryptedPwd) {
        try {
            // convert encrypted password string to a password key
            Password rawPassword = ModularCrypt.decode(encryptedPwd);

            // create the password factory based on the bcrypt algorithm
            PasswordFactory factory = PasswordFactory.getInstance(BCryptPassword.ALGORITHM_BCRYPT);

            // create encrypted password based on stored string
            BCryptPassword restored = (BCryptPassword) factory.translate(rawPassword);

            // verify restored password against original
            return factory.verify(restored, originalPwd.toCharArray());
        } catch (InvalidKeySpecException | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }
}
