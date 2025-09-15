package config;

import io.github.cdimascio.dotenv.Dotenv;

public class Config {
    private static final Dotenv dotenv = Dotenv.load();
    public static final String SALT_ROUNDS = dotenv.get("SALT_ROUNDS");
    public static final String JWT_SECRET = dotenv.get("JWT_SECRET");
}
