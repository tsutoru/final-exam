package exam.file.code.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

  private static final String SECRET_KEY = "VGhpc0lzQVNlY3JldEtleUZvckpXVC1BdXRoZW50aWNhdGlvbg==";

  private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 24;

  public String generateToken(UserDetails userDetails) {

    return Jwts.builder()
        .subject(userDetails.getUsername())
        .issuedAt(new Date())
        .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
        .signWith(getSignInKey())
        .compact();
  }

  public String extractUsername(String token) {

    return extractAllClaims(token).getSubject();
  }

  public boolean isTokenValid(String token, UserDetails userDetails) {

    final String username = extractUsername(token);

    return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
  }

  private boolean isTokenExpired(String token) {

    return extractAllClaims(token).getExpiration().before(new Date());
  }

  private Claims extractAllClaims(String token) {

    return Jwts.parser().verifyWith(getSignInKey()).build().parseSignedClaims(token).getPayload();
  }

  private SecretKey getSignInKey() {

    byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);

    return Keys.hmacShaKeyFor(keyBytes);
  }
}
