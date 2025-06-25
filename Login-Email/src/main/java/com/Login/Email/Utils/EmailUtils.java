package com.Login.Email.Utils;


public class EmailUtils {

    public static String getEmailMessage(String name, String host, String token) {
        return "Holla " + name + ",\n\nSu nueva cuenta ha sido creada. Por favor haga clic en el enlace a continuación para verificar su cuenta. \n\n" +
                getVerificationUrl(host, token) + "\n\nThe support Team";
    }

    public static String getVerificationUrl(String host, String token) {
        return host + "/auth/check?token=" + token;
    }
}
