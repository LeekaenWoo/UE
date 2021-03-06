package com.ry.editor.srv.entity;

import com.tt.pwp.framework.data.model.DefaultDTO;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by KING on 2018/3/13.
 */
@Entity
@Table(name = "TP_TEMPLATE_TYPE", schema = "EDITOR")
public class TpTemplateType extends DefaultDTO {
    //private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "ID")
    private Integer id;
    @Basic
    @Column(name = "TYPE_CODE")
    private String type_code;
    @Basic
    @Column(name = "TYPE_NAME")
    private String type_name;
    @Basic
    @Column(name = "CREATE_USER")
    private String create_user;
    @Basic
    @Column(name = "CREATE_TIME")
    private Date create_time;
    @Basic
    @Column(name = "LASTUPDATE_TIME")
    private Date lastupdate_time;
    @Basic
    @Column(name = "LASTUPDATE_USER")
    private String lastupdate_user;
    @Basic
    @Column(name = "STATUS")
    private String status;

    public TpTemplateType() {
    }

    public TpTemplateType(Integer id, String type_code, String type_name, String create_user, Date create_time, Date lastupdate_time, String lastupdate_user, String status) {
        this.id = id;
        this.type_code = type_code;
        this.type_name = type_name;
        this.create_user = create_user;
        this.create_time = create_time;
        this.lastupdate_time = lastupdate_time;
        this.lastupdate_user = lastupdate_user;
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TpTemplateType that = (TpTemplateType) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (type_code != null ? !type_code.equals(that.type_code) : that.type_code != null) return false;
        if (type_name != null ? !type_name.equals(that.type_name) : that.type_name != null) return false;
        if (create_user != null ? !create_user.equals(that.create_user) : that.create_user != null) return false;
        if (create_time != null ? !create_time.equals(that.create_time) : that.create_time != null) return false;
        if (lastupdate_time != null ? !lastupdate_time.equals(that.lastupdate_time) : that.lastupdate_time != null)
            return false;
        if (lastupdate_user != null ? !lastupdate_user.equals(that.lastupdate_user) : that.lastupdate_user != null)
            return false;
        return status != null ? status.equals(that.status) : that.status == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (type_code != null ? type_code.hashCode() : 0);
        result = 31 * result + (type_name != null ? type_name.hashCode() : 0);
        result = 31 * result + (create_user != null ? create_user.hashCode() : 0);
        result = 31 * result + (create_time != null ? create_time.hashCode() : 0);
        result = 31 * result + (lastupdate_time != null ? lastupdate_time.hashCode() : 0);
        result = 31 * result + (lastupdate_user != null ? lastupdate_user.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        return result;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getType_code() {
        return type_code;
    }

    public void setType_code(String type_code) {
        this.type_code = type_code;
    }

    public String getType_name() {
        return type_name;
    }

    public void setType_name(String type_name) {
        this.type_name = type_name;
    }

    public String getCreate_user() {
        return create_user;
    }

    public void setCreate_user(String create_user) {
        this.create_user = create_user;
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }

    public Date getLastupdate_time() {
        return lastupdate_time;
    }

    public void setLastupdate_time(Date lastupdate_time) {
        this.lastupdate_time = lastupdate_time;
    }

    public String getLastupdate_user() {
        return lastupdate_user;
    }

    public void setLastupdate_user(String lastupdate_user) {
        this.lastupdate_user = lastupdate_user;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
