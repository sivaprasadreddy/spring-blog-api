package com.sivalabs.blog;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import com.sivalabs.blog.auth.JwtTokenHelper;
import com.sivalabs.blog.users.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.client.RestTestClient;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureMockMvc
@AutoConfigureRestTestClient
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("it")
public abstract class AbstractIT {

    @Autowired
    protected RestTestClient restTestClient;

    @Autowired
    protected MockMvcTester mvc;

    @Autowired
    private JwtTokenHelper jwtTokenHelper;

    public String createToken(UserDto userDto) {
        return jwtTokenHelper.generateToken(userDto).token();
    }
}
