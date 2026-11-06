package рбпо.гришин.лаба4.контроллер;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import рбпо.гришин.лаба4.модель.Пользователь;
import рбпо.гришин.лаба4.сервис.СервисПользователей;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class КонтроллерПользователей {
    private final СервисПользователей сервис;

    public КонтроллерПользователей(СервисПользователей сервис) {
        this.сервис = сервис;
    }

    @GetMapping
    public List<Пользователь> получитьВсе() {
        return сервис.получитьВсех();
    }

    @GetMapping("/{id}")
    public Пользователь получитьПоНомеру(@PathVariable("id") long id, Authentication вход) {
        return сервис.получитьПользователя(id, вход);
    }

    @PostMapping
    public ResponseEntity<Пользователь> создать(@RequestBody Создание запрос) {
        Пользователь результат = сервис.создать(запрос.имя, запрос.логин, запрос.пароль,
                запрос.роль, запрос.активен);
        return ResponseEntity.status(HttpStatus.CREATED).body(результат);
    }

    @PatchMapping("/{id}")
    public Пользователь изменить(@PathVariable("id") long id, @RequestBody Изменение запрос) {
        return сервис.изменить(id, запрос.имя, запрос.пароль, запрос.роль, запрос.активен);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> удалить(@PathVariable("id") long id) {
        сервис.удалить(id);
        return ResponseEntity.noContent().build();
    }

    public static class Создание {
        public String имя;
        public String логин;
        public String пароль;
        public String роль;
        public Boolean активен;
    }

    public static class Изменение {
        public String имя;
        public String пароль;
        public String роль;
        public Boolean активен;
    }
}
