package рбпо.гришин.лаба4.репозиторий;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import рбпо.гришин.лаба4.модель.Билет;
import рбпо.гришин.лаба4.модель.Зал;
import рбпо.гришин.лаба4.модель.Показ;
import рбпо.гришин.лаба4.модель.Пользователь;
import рбпо.гришин.лаба4.модель.Спектакль;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class РепозиторийТеатра {
    private final JdbcTemplate база;

    public РепозиторийТеатра(JdbcTemplate база) {
        this.база = база;
    }

    public void заблокироватьИзменения() {
        база.queryForObject("SELECT id FROM блокировка WHERE id = 1 FOR UPDATE", Integer.class);
    }

    public List<Спектакль> всеСпектакли() {
        return база.query("SELECT * FROM спектакли ORDER BY id", this::прочитатьСпектакль);
    }

    public Спектакль найтиСпектакль(long id) {
        List<Спектакль> записи = база.query("SELECT * FROM спектакли WHERE id = ?", this::прочитатьСпектакль, id);
        return записи.isEmpty() ? null : записи.get(0);
    }

    public Спектакль сохранитьСпектакль(Спектакль спектакль) {
        if (спектакль.id == null) {
            спектакль.id = добавить("INSERT INTO спектакли (название, длительность_минут, возрастное_ограничение) VALUES (?, ?, ?)",
                    спектакль.название, спектакль.длительностьМинут, спектакль.возрастноеОграничение);
        } else {
            база.update("UPDATE спектакли SET название = ?, длительность_минут = ?, возрастное_ограничение = ? WHERE id = ?",
                    спектакль.название, спектакль.длительностьМинут, спектакль.возрастноеОграничение, спектакль.id);
        }
        return спектакль;
    }

    public void удалитьСпектакль(long id) {
        база.update("DELETE FROM спектакли WHERE id = ?", id);
    }

    public List<Зал> всеЗалы() {
        return база.query("SELECT * FROM залы ORDER BY id", this::прочитатьЗал);
    }

    public Зал найтиЗал(long id) {
        List<Зал> записи = база.query("SELECT * FROM залы WHERE id = ?", this::прочитатьЗал, id);
        return записи.isEmpty() ? null : записи.get(0);
    }

    public Зал сохранитьЗал(Зал зал) {
        if (зал.id == null) {
            зал.id = добавить("INSERT INTO залы (название, число_мест) VALUES (?, ?)",
                    зал.название, зал.числоМест);
        } else {
            база.update("UPDATE залы SET название = ?, число_мест = ? WHERE id = ?",
                    зал.название, зал.числоМест, зал.id);
        }
        return зал;
    }

    public void удалитьЗал(long id) {
        база.update("DELETE FROM залы WHERE id = ?", id);
    }

    public List<Показ> всеПоказы() {
        return база.query("SELECT * FROM показы ORDER BY id", this::прочитатьПоказ);
    }

    public Показ найтиПоказ(long id) {
        List<Показ> записи = база.query("SELECT * FROM показы WHERE id = ?", this::прочитатьПоказ, id);
        return записи.isEmpty() ? null : записи.get(0);
    }

    public Показ сохранитьПоказ(Показ показ) {
        if (показ.id == null) {
            показ.id = добавить("INSERT INTO показы (спектакль_id, зал_id, начало, базовая_цена, статус) VALUES (?, ?, ?, ?, ?)",
                    показ.idСпектакля, показ.idЗала, показ.начало, показ.базоваяЦена, показ.статус);
        } else {
            база.update("UPDATE показы SET спектакль_id = ?, зал_id = ?, начало = ?, базовая_цена = ?, статус = ? WHERE id = ?",
                    показ.idСпектакля, показ.idЗала, показ.начало, показ.базоваяЦена, показ.статус, показ.id);
        }
        return показ;
    }

    public void удалитьПоказ(long id) {
        база.update("DELETE FROM показы WHERE id = ?", id);
    }

    public List<Билет> всеБилеты() {
        return база.query("SELECT * FROM билеты ORDER BY id", this::прочитатьБилет);
    }

    public Билет найтиБилет(long id) {
        List<Билет> записи = база.query("SELECT * FROM билеты WHERE id = ?", this::прочитатьБилет, id);
        return записи.isEmpty() ? null : записи.get(0);
    }

    public Билет сохранитьБилет(Билет билет) {
        if (билет.id == null) {
            билет.id = добавить("INSERT INTO билеты (показ_id, номер_места, пользователь_id, статус, цена, сумма_возврата) VALUES (?, ?, ?, ?, ?, ?)",
                    билет.idПоказа, билет.номерМеста, билет.idПользователя, билет.статус, билет.цена, билет.суммаВозврата);
        } else {
            база.update("UPDATE билеты SET показ_id = ?, номер_места = ?, пользователь_id = ?, статус = ?, цена = ?, сумма_возврата = ? WHERE id = ?",
                    билет.idПоказа, билет.номерМеста, билет.idПользователя, билет.статус, билет.цена, билет.суммаВозврата, билет.id);
        }
        return билет;
    }

    public void удалитьБилет(long id) {
        база.update("DELETE FROM билеты WHERE id = ?", id);
    }

    public List<Пользователь> всеПользователи() {
        return база.query("SELECT * FROM пользователи ORDER BY id", this::прочитатьПользователя);
    }

    public Пользователь найтиПользователя(long id) {
        List<Пользователь> записи = база.query("SELECT * FROM пользователи WHERE id = ?", this::прочитатьПользователя, id);
        return записи.isEmpty() ? null : записи.get(0);
    }

    public Пользователь найтиПользователяПоЛогину(String логин) {
        List<Пользователь> записи = база.query("SELECT * FROM пользователи WHERE логин = ?",
                this::прочитатьПользователя, логин);
        return записи.isEmpty() ? null : записи.get(0);
    }

    public long числоАктивныхАдминистраторов() {
        return база.queryForObject("SELECT COUNT(*) FROM пользователи WHERE роль = 'ADMIN' AND активен = TRUE", Long.class);
    }

    public List<Билет> билетыПользователя(long idПользователя) {
        return база.query("SELECT * FROM билеты WHERE пользователь_id = ? ORDER BY id",
                this::прочитатьБилет, idПользователя);
    }

    public Пользователь сохранитьПользователя(Пользователь пользователь) {
        if (пользователь.id == null) {
            пользователь.id = добавить("INSERT INTO пользователи (имя, логин, хеш_пароля, роль, активен) VALUES (?, ?, ?, ?, ?)",
                    пользователь.имя, пользователь.логин, пользователь.хешПароля, пользователь.роль, пользователь.активен);
        } else {
            база.update("UPDATE пользователи SET имя = ?, хеш_пароля = ?, роль = ?, активен = ? WHERE id = ?",
                    пользователь.имя, пользователь.хешПароля, пользователь.роль, пользователь.активен, пользователь.id);
        }
        return пользователь;
    }

    public void удалитьПользователя(long id) {
        база.update("DELETE FROM пользователи WHERE id = ?", id);
    }

    public List<Билет> билетыПоказа(long idПоказа) {
        return база.query("SELECT * FROM билеты WHERE показ_id = ? ORDER BY id", this::прочитатьБилет, idПоказа);
    }

    public boolean естьБилетыПоказа(long idПоказа) {
        return база.queryForObject("SELECT COUNT(*) FROM билеты WHERE показ_id = ?",
                Long.class, idПоказа) > 0;
    }

    public boolean естьМестаЗаПределамиЗала(long idЗала, int числоМест) {
        return база.queryForObject("SELECT COUNT(*) FROM билеты JOIN показы ON билеты.показ_id = показы.id "
                        + "WHERE показы.зал_id = ? AND билеты.номер_места > ?",
                Long.class, idЗала, числоМест) > 0;
    }

    public boolean местоЗанято(long idПоказа, int номерМеста, Long idБилета) {
        // При изменении брони её собственное место не считается занятым другим билетом.
        long исключённыйId = idБилета == null ? 0 : idБилета;
        return база.queryForObject("SELECT COUNT(*) FROM билеты "
                        + "WHERE показ_id = ? AND активное_место = ? AND id <> ?",
                Long.class, idПоказа, номерМеста, исключённыйId) > 0;
    }

    // Общая часть INSERT: передать значения и получить созданный базой id.
    private long добавить(String sql, Object... значения) {
        GeneratedKeyHolder ключ = new GeneratedKeyHolder();
        база.update(соединение -> {
            PreparedStatement команда = соединение.prepareStatement(sql, new String[]{"ID"});
            for (int i = 0; i < значения.length; i++) {
                команда.setObject(i + 1, значения[i]);
            }
            return команда;
        }, ключ);
        return ключ.getKey().longValue();
    }

    private Спектакль прочитатьСпектакль(ResultSet строка, int номерСтроки) throws SQLException {
        Спектакль спектакль = new Спектакль();
        спектакль.id = строка.getLong("id");
        спектакль.название = строка.getString("название");
        спектакль.длительностьМинут = строка.getInt("длительность_минут");
        спектакль.возрастноеОграничение = строка.getInt("возрастное_ограничение");
        return спектакль;
    }

    private Зал прочитатьЗал(ResultSet строка, int номерСтроки) throws SQLException {
        Зал зал = new Зал();
        зал.id = строка.getLong("id");
        зал.название = строка.getString("название");
        зал.числоМест = строка.getInt("число_мест");
        return зал;
    }

    private Показ прочитатьПоказ(ResultSet строка, int номерСтроки) throws SQLException {
        Показ показ = new Показ();
        показ.id = строка.getLong("id");
        показ.idСпектакля = строка.getLong("спектакль_id");
        показ.idЗала = строка.getLong("зал_id");
        показ.начало = строка.getObject("начало", LocalDateTime.class);
        показ.базоваяЦена = строка.getBigDecimal("базовая_цена");
        показ.статус = строка.getString("статус");
        return показ;
    }

    private Билет прочитатьБилет(ResultSet строка, int номерСтроки) throws SQLException {
        Билет билет = new Билет();
        билет.id = строка.getLong("id");
        билет.idПоказа = строка.getLong("показ_id");
        билет.номерМеста = строка.getInt("номер_места");
        билет.idПользователя = строка.getLong("пользователь_id");
        билет.статус = строка.getString("статус");
        билет.цена = строка.getBigDecimal("цена");
        билет.суммаВозврата = строка.getBigDecimal("сумма_возврата");
        return билет;
    }

    private Пользователь прочитатьПользователя(ResultSet строка, int номерСтроки) throws SQLException {
        Пользователь пользователь = new Пользователь();
        пользователь.id = строка.getLong("id");
        пользователь.имя = строка.getString("имя");
        пользователь.логин = строка.getString("логин");
        пользователь.роль = строка.getString("роль");
        пользователь.активен = строка.getBoolean("активен");
        пользователь.хешПароля = строка.getString("хеш_пароля");
        return пользователь;
    }

}
