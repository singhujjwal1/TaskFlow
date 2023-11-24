package io.aman.ppmtool.security;

import io.aman.ppmtool.domain.User;
import io.jsonwebtoken.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static io.aman.ppmtool.security.SecurityConstants.EXPIRATION_TIME;
import static io.aman.ppmtool.security.SecurityConstants.SECRET;


@Component
public class JwtTokenProvider {
    // generate the token

    public String generateToken(Authentication authentication) {
        User user = (User)authentication.getPrincipal();
        Date now = new Date(System.currentTimeMillis());
        Date expiryDate = new Date(now.getTime()+EXPIRATION_TIME);

        String userId = Long.toString(user.getId());

        Map<String, Object> claims = new HashMap<>();
        claims.put("id", (Long.toString(user.getId())));
        claims.put("username", user.getUsername());
        claims.put("fullName", user.getFullName());

        return Jwts.builder()
                .setSubject(userId)
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .compact();
    }

    //validate the token
    public boolean validateToken(String token) {
        try{
            Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token);
            return true;
        } catch (SignatureException ex) {
            System.out.println("Invalid JWT Signature");
        } catch (MalformedJwtException ex) {
            System.out.println("Invalid JWT Token");
        } catch (ExpiredJwtException ex) {
            System.out.println("Expried JWT token");
        } catch (UnsupportedJwtException ex) {
            System.out.println("Unspported JWT token");
        } catch (IllegalArgumentException ex) {
            System.out.println("JWT claims String is Empty");
        }
        return false;
    }

    //get user id from the token

    public long getUserIdFromJWT(String token) {
        Claims claims = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).getBody();
        String id = (String)claims.get("id");

        return Long.parseLong(id);
    }
}
