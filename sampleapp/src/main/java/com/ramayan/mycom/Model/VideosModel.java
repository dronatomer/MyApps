package com.ramayan.mycom.Model;

import java.io.Serializable;
import java.util.List;

public class VideosModel implements Serializable {

    private int page;
    private int limit;
    private boolean explicit;
    private int total;
    private boolean has_more;
    List<Videos> list;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public boolean isExplicit() {
        return explicit;
    }

    public void setExplicit(boolean explicit) {
        this.explicit = explicit;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public boolean isHas_more() {
        return has_more;
    }

    public void setHas_more(boolean has_more) {
        this.has_more = has_more;
    }

    public List<Videos> getList() {
        return list;
    }

    public void setList(List<Videos> list) {
        this.list = list;
    }
}
