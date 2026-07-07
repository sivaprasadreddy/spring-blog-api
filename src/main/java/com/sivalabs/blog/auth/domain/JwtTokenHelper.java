package com.sivalabs.blog.auth.domain;

import com.sivalabs.blog.auth.domain.models.JwtToken;
import com.sivalabs.blog.config.JwtProperties;
import com.sivalabs.blog.users.domain.models.UserDto;
import java.time.Instant;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenHelper {
    private final JwtEncoder encoder;
    private final JwtProperties jwtProperties;

    JwtTokenHelper(JwtEncoder encoder, JwtProperties properties) {
        this.encoder = encoder;
        this.jwtProperties = properties;
    }

    public JwtToken generateToken(UserDto userDto) {
        Instant now = Instant.now();
        Instant expiresAt = now.plusSeconds(jwtProperties.expiresInSeconds());
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(jwtProperties.issuer())
                .issuedAt(now)
                .expiresAt(expiresAt)
                .subject(userDto.email())
                .claim("user_id", userDto.id())
                .claim("roles", userDto.role().name())
                .build();
        var token = this.encoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
        return new JwtToken(token, expiresAt);
    }
}
