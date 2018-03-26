package me.framework.test;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 */
public class DemoModel implements Serializable {

    private long id;

    private String name;

    private BigDecimal discount;

    private Date beginTime;

    private Date endTime;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public Date getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "DemoModel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", discount=" + discount +
                ", beginTime=" + beginTime +
                ", endTime=" + endTime +
                '}';
    }
}
