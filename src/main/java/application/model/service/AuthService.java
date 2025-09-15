package application.model.service;

import application.model.entity.User;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import config.Config;

public class AuthService {

    public String createToken(User user) {
        Algorithm algorithm = Algorithm.HMAC256(Config.JWT_SECRET);
        try {
            return JWT.create()
                    .withClaim("userName", user.getUsername())
                    .withClaim("userId", user.getId())
                    .withClaim("firstName", user.getFirstName())
                    .withClaim("lastName", user.getLastName())
                    .sign(algorithm);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public DecodedJWT authMe(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(Config.JWT_SECRET);
            JWTVerifier verifier = JWT.require(algorithm)
                    .build();
            return verifier.verify(token);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
