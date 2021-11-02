package banking.application.routes;

import banking.application.Application;
import com.auth0.jwk.JwkException;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

@SpringBootApplication
@RequestMapping("/")
public class health extends Application {

    @GetMapping("health")
    public ResponseEntity<String> getHealth(@RequestHeader(name = "Authorization", required = false) String token) throws IOException, JwkException {
        return ResponseEntity.ok("Healthy");
    }
}
