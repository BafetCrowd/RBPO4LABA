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
import рбпо.гришин.лаба4.модель.Билет;
import рбпо.гришин.лаба4.сервис.СервисТеатра;

import java.util.List;

@RestController
@PreAuthorize("isAuthenticated()")
@RequestMapping("/api/tickets")
public class КонтроллерБилетов {
    private final СервисТеатра сервис;

    public КонтроллерБилетов(СервисТеатра сервис) {
        this.сервис = сервис;
    }

    @GetMapping
    public List<Билет> получитьВсе() {
        return сервис.получитьБилеты();
    }

    @GetMapping("/{id}")
    public Билет получитьПоНомеру(@PathVariable("id") long id) {
        return сервис.получитьБилет(id);
    }

    @PostMapping
    public ResponseEntity<Билет> создать(@RequestBody Запрос запрос) {
        Билет результат = сервис.создатьБилет(преобразовать(запрос));
        return ResponseEntity.status(HttpStatus.CREATED).body(результат);
    }

    @PatchMapping("/{id}")
    public Билет изменить(@PathVariable("id") long id, @RequestBody Запрос запрос) {
        return сервис.изменитьБилет(id, преобразовать(запрос));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> удалить(@PathVariable("id") long id) {
        сервис.удалитьБилет(id);
        return ResponseEntity.noContent().build();
    }

    private Билет преобразовать(Запрос запрос) {
        Билет данные = new Билет();
        данные.idПоказа = запрос.idПоказа;
        данные.номерМеста = запрос.номерМеста;
        данные.idПользователя = запрос.idПользователя;
        данные.статус = запрос.статус;
        return данные;
    }

    public static class Запрос {
        public Long idПоказа;
        public Integer номерМеста;
        public Long idПользователя;
        public String статус;
    }
}
