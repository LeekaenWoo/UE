package com.ry.editor.srv.entity;

import com.tt.pwp.framework.data.model.DefaultDTO;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.*;

/**
 * Created by KING on 2018/3/13.
 */
@Entity
@Table(name = "TP_PLUGIN_TREE", schema = "EDITOR")
public class TpPluginTree extends DefaultDTO {
    //private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "ID")
    private Integer id;
    @Basic
    @Column(name = "PID")
    private Integer pid;
    @Basic
    @Column(name = "NAME")
    private String name;
    @Basic
    @Column(name = "DESCNAME")
    private String descname;
    @Basic
    @Column(name = "STATUS")
    private String status;

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    public TpPluginTree() {
    }

    public TpPluginTree(Integer id, Integer pid, String name, String descname, String status) {
        this.id = id;
        this.pid = pid;
        this.name = name;
        this.descname = descname;
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TpPluginTree that = (TpPluginTree) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (pid != null ? !pid.equals(that.pid) : that.pid != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (descname != null ? !descname.equals(that.descname) : that.descname != null) return false;
        return status != null ? status.equals(that.status) : that.status == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (pid != null ? pid.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (descname != null ? descname.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        return result;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPid() {
        return pid;
    }

    public void setPid(Integer pid) {
        this.pid = pid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescname() {
        return descname;
    }

    public void setDescname(String descname) {
        this.descname = descname;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
