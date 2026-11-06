package рбпо.гришин.лаба4.безопасность;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import рбпо.гришин.лаба4.модель.Пользователь;
import рбпо.гришин.лаба4.репозиторий.РепозиторийТеатра;

@Component
public class ПроверкаДоступа {
    private final РепозиторийТеатра репозиторий;

    public ПроверкаДоступа(РепозиторийТеатра репозиторий) {
        this.репозиторий = репозиторий;
    }

    public Пользователь текущийПользователь() {
        Authentication вход = SecurityContextHolder.getContext().getAuthentication();
        if (вход == null || !вход.isAuthenticated()) {
            throw new AccessDeniedException("Требуется вход.");
        }
        Пользователь пользователь = репозиторий.найтиПользователяПоЛогину(вход.getName());
        if (пользователь == null || !Boolean.TRUE.equals(пользователь.активен)) {
            throw new AccessDeniedException("Недостаточно прав.");
        }
        return пользователь;
    }

    public void проверитьАдминистратора() {
        if (!"ADMIN".equals(текущийПользователь().роль)) {
            throw new AccessDeniedException("Недостаточно прав.");
        }
    }

    public void проверитьВладельца(Long idПользователя) {
        Пользователь текущий = текущийПользователь();
        if (!"ADMIN".equals(текущий.роль) && !текущий.id.equals(idПользователя)) {
            throw new AccessDeniedException("Доступны только свои билеты.");
        }
    }
}
