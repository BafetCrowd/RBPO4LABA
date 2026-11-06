package рбпо.гришин.лаба4.сервис;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import рбпо.гришин.лаба4.безопасность.ПроверкаДоступа;
import рбпо.гришин.лаба4.модель.Пользователь;
import рбпо.гришин.лаба4.репозиторий.РепозиторийТеатра;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;

@Service
@Transactional
public class СервисПользователей {
    private final РепозиторийТеатра репозиторий;
    private final PasswordEncoder кодировщик;
    private final ПроверкаДоступа доступ;

    public СервисПользователей(РепозиторийТеатра репозиторий, PasswordEncoder кодировщик, ПроверкаДоступа доступ) {
        this.репозиторий = репозиторий;
        this.кодировщик = кодировщик;
        this.доступ = доступ;
    }

    public Пользователь зарегистрировать(String имя, String логин, String пароль) {
        репозиторий.заблокироватьИзменения();
        // При регистрации пользователь не может выбрать роль или состояние аккаунта.
        return добавитьПользователя(имя, логин, пароль, "USER", true);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public List<Пользователь> получитьВсех() {
        доступ.проверитьАдминистратора();
        return репозиторий.всеПользователи();
    }

    @PreAuthorize("isAuthenticated()")
    public Пользователь получитьТекущего(Authentication вход) {
        if (вход == null || !вход.isAuthenticated() || вход instanceof AnonymousAuthenticationToken) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Требуется вход или неверные учётные данные.");
        }
        Пользователь пользователь = репозиторий.найтиПользователяПоЛогину(вход.getName());
        if (пользователь == null || !пользователь.активен) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Требуется вход или неверные учётные данные.");
        }
        return пользователь;
    }

    @PreAuthorize("isAuthenticated()")
    public Пользователь получитьПользователя(long id, Authentication вход) {
        Пользователь текущий = получитьТекущего(вход);
        if (!"ADMIN".equals(текущий.роль) && текущий.id != id) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Недостаточно прав.");
        }
        return найтиПользователя(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Пользователь создать(String имя, String логин, String пароль, String роль, Boolean активен) {
        репозиторий.заблокироватьИзменения();
        доступ.проверитьАдминистратора();
        return добавитьПользователя(имя, логин, пароль,
                роль == null ? "USER" : роль, активен == null || активен);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Пользователь изменить(long id, String имя, String пароль, String роль, Boolean активен) {
        репозиторий.заблокироватьИзменения();
        доступ.проверитьАдминистратора();
        Пользователь прежний = найтиПользователя(id);
        Пользователь новый = new Пользователь();
        новый.id = id;
        новый.логин = прежний.логин;
        новый.имя = имя == null ? прежний.имя : имя;
        новый.роль = роль == null ? прежний.роль : роль;
        новый.активен = активен == null ? прежний.активен : активен;
        новый.хешПароля = прежний.хешПароля;
        проверитьИмя(новый.имя);
        проверитьРоль(новый.роль);
        if (пароль != null) {
            проверитьПароль(пароль);
            новый.хешПароля = кодировщик.encode(пароль);
        }
        if (!"ADMIN".equals(новый.роль) || !новый.активен) {
            защититьПоследнегоАдминистратора(прежний);
        }
        return репозиторий.сохранитьПользователя(новый);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void удалить(long id) {
        репозиторий.заблокироватьИзменения();
        доступ.проверитьАдминистратора();
        Пользователь пользователь = найтиПользователя(id);
        защититьПоследнегоАдминистратора(пользователь);
        // Внешний ключ запрещает удаление пользователя, у которого есть билеты.
        репозиторий.удалитьПользователя(id);
    }

    private Пользователь добавитьПользователя(String имя, String логин, String пароль,
                                             String роль, boolean активен) {
        проверитьИмя(имя);
        проверитьПароль(пароль);
        проверитьРоль(роль);
        if (логин == null || !логин.matches("[a-zA-Z0-9_]{3,40}")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Логин: от 3 до 40 латинских букв, цифр или знаков подчёркивания.");
        }
        логин = логин.toLowerCase(Locale.ROOT);
        if (репозиторий.найтиПользователяПоЛогину(логин) != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Этот логин уже занят.");
        }
        Пользователь пользователь = new Пользователь();
        пользователь.имя = имя;
        пользователь.логин = логин;
        пользователь.роль = роль;
        пользователь.активен = активен;
        пользователь.хешПароля = кодировщик.encode(пароль);
        return репозиторий.сохранитьПользователя(пользователь);
    }

    private Пользователь найтиПользователя(long id) {
        Пользователь пользователь = репозиторий.найтиПользователя(id);
        if (пользователь == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден.");
        }
        return пользователь;
    }

    private void защититьПоследнегоАдминистратора(Пользователь пользователь) {
        if ("ADMIN".equals(пользователь.роль) && пользователь.активен
                && репозиторий.числоАктивныхАдминистраторов() <= 1) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Нельзя удалить, заблокировать или понизить последнего активного администратора.");
        }
    }

    private void проверитьИмя(String имя) {
        if (имя == null || имя.isBlank() || имя.length() > 200) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Укажите имя длиной от 1 до 200 символов.");
        }
    }

    private void проверитьРоль(String роль) {
        if (!"ADMIN".equals(роль) && !"USER".equals(роль)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Роль: ADMIN или USER.");
        }
    }

    public static void проверитьПароль(String пароль) {
        if (пароль == null || пароль.getBytes(StandardCharsets.UTF_8).length < 8
                || пароль.getBytes(StandardCharsets.UTF_8).length > 72
                || !пароль.matches("(?s).*[A-Z].*") || !пароль.matches("(?s).*[a-z].*")
                || !пароль.matches("(?s).*[0-9].*")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Пароль: от 8 до 72 байт UTF-8, минимум одна латинская заглавная и строчная буква и цифра.");
        }
    }
}
