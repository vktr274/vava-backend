package sk.vava.zalospevaci.artifacts;

import sk.vava.zalospevaci.exceptions.NotAuthorizedException;
import sk.vava.zalospevaci.models.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class TokenManager {
    private static class UserData {
        public String role;
        public String login;
        public long id;

        public UserData(String role, String login, long id) {
            this.id = id;
            this.role = role;
            this.login = login;
        }

    }

    static Map<String, UserData> tokens = new HashMap<>();

    static public String createToken(User user) {
        Random random = new Random();
        StringBuilder key = new StringBuilder();
        for (int i = 0; i<20; i++) {
            key.append((char) (random.nextInt(122-97) + 97));
        }
        UserData data = new UserData(user.getRole(), user.getUsername(), user.getId());
        tokens.put(key.toString(), data);
        return key.toString();
    }

    static public void validToken(String token, String requiredRole) throws NotAuthorizedException {
        String role = tokens.get(token).role;
        if (UserRole.valueOf(role).getValue() < UserRole.valueOf(requiredRole).getValue()) {
            throw new NotAuthorizedException("Wrong role");
        }
    }

    static public String getLoginByToken(String token) throws NullPointerException {
        return tokens.get(token).login;
    }

    static public Long getIdByToken(String token) throws NullPointerException {
        return tokens.get(token).id;
    }
}
