package models;

import com.avaje.ebean.Model;

import javax.persistence.*;

@Entity
public class MessageEntity extends Model {

    @Id
    private Long id;
    @Column
    private String user;
    @Column
    private String message;

    public MessageEntity(String user, String message) {
        this.user = user;
        this.message = message;
    }

    public Long getId() {
        return id;
    }

    public String getUser() {
        return user;
    }

    public String getMessage() {
        return message;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static final Finder<Long, MessageEntity> FIND = new Finder<>(MessageEntity.class);
}
