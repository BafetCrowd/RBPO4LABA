package рбпо.гришин.лаба4.контроллер;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import рбпо.гришин.лаба4.модель.Билет;
import рбпо.гришин.лаба4.модель.Показ;
import рбпо.гришин.лаба4.сервис.СервисТеатра;

import java.util.List;

@RestController
@PreAuthorize("isAuthenticated()")
@RequestMapping("/api")
public class КонтроллерОпераций {
    private final СервисТеатра сервис;

    public КонтроллерОпераций(СервисТеатра сервис) {
        this.сервис = сервис;
    }

    @PostMapping("/performances/{id}/group-booking")
    public ResponseEntity<List<Билет>> забронироватьГруппу(@PathVariable("id") long id,
                                                         @RequestBody ГрупповойЗаказ запрос) {
        List<Билет> билеты = сервис.забронироватьГруппу(id, запрос.idПользователя,
                запрос.первоеМесто, запрос.количество);
        return ResponseEntity.status(HttpStatus.CREATED).body(билеты);
    }

    @PostMapping("/tickets/{id}/purchase")
    public Билет купить(@PathVariable("id") long id) {
        return сервис.купитьБилет(id);
    }

    @PostMapping("/tickets/{id}/return")
    public Билет вернуть(@PathVariable("id") long id) {
        return сервис.вернутьБилет(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/performances/{id}/cancel")
    public Показ отменить(@PathVariable("id") long id) {
        return сервис.отменитьПоказ(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/performances/{id}/complete")
    public Показ завершить(@PathVariable("id") long id) {
        return сервис.завершитьПоказ(id);
    }

    public static class ГрупповойЗаказ {
        public Long idПользователя;
        public Integer первоеМесто;
        public Integer количество;
    }
}
