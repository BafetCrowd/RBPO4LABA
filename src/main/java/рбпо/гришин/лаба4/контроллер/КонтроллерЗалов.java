package рбпо.гришин.лаба4.контроллер;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import рбпо.гришин.лаба4.модель.Зал;
import рбпо.гришин.лаба4.сервис.СервисТеатра;

import java.util.List;

@RestController
@PreAuthorize("isAuthenticated()")
@RequestMapping("/api/theatre-halls")
public class КонтроллерЗалов {
    private final СервисТеатра сервис;

    public КонтроллерЗалов(СервисТеатра сервис) {
        this.сервис = сервис;
    }

    @GetMapping
    public List<Зал> получитьВсе() {
        return сервис.получитьЗалы();
    }

    @GetMapping("/{id}")
    public Зал получитьПоНомеру(@PathVariable("id") long id) {
        return сервис.получитьЗал(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Зал> создать(@RequestBody Запрос запрос) {
        Зал результат = сервис.создатьЗал(преобразовать(запрос));
        return ResponseEntity.status(HttpStatus.CREATED).body(результат);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}")
    public Зал изменить(@PathVariable("id") long id, @RequestBody Запрос запрос) {
        return сервис.изменитьЗал(id, преобразовать(запрос));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> удалить(@PathVariable("id") long id) {
        сервис.удалитьЗал(id);
        return ResponseEntity.noContent().build();
    }

    private Зал преобразовать(Запрос запрос) {
        Зал данные = new Зал();
        данные.название = запрос.название;
        данные.числоМест = запрос.числоМест;
        return данные;
    }

    public static class Запрос {
        public String название;
        public Integer числоМест;
    }
}
