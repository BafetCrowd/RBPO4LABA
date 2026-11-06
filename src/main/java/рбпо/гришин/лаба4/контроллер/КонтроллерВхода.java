package рбпо.гришин.лаба4.контроллер;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import рбпо.гришин.лаба4.модель.Пользователь;
import рбпо.гришин.лаба4.сервис.СервисПользователей;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class КонтроллерВхода {
    private final СервисПользователей сервис;

    public КонтроллерВхода(СервисПользователей сервис) {
        this.сервис = сервис;
    }

    @GetMapping("/csrf")
    public Map<String, String> получитьТокен(CsrfToken токен) {
        return Map.of("token", токен.getToken(), "headerName", токен.getHeaderName(),
                "parameterName", токен.getParameterName());
    }

    @PostMapping("/register")
    public ResponseEntity<Пользователь> зарегистрироваться(@RequestBody Регистрация запрос) {
        Пользователь пользователь = сервис.зарегистрировать(запрос.имя, запрос.логин, запрос.пароль);
        return ResponseEntity.status(HttpStatus.CREATED).body(пользователь);
    }

    @GetMapping("/me")
    public Ответ текущийПользователь(Authentication вход) {
        return new Ответ(сервис.получитьТекущего(вход));
    }

    public static class Ответ {
        public Long id;
        public String имя;
        public String логин;
        public String роль;
        public Boolean активен;

        public Ответ(Пользователь пользователь) {
            id = пользователь.id;
            имя = пользователь.имя;
            логин = пользователь.логин;
            роль = пользователь.роль;
            активен = пользователь.активен;
        }
    }

    public static class Регистрация {
        public String имя;
        public String логин;
        public String пароль;
    }
}
