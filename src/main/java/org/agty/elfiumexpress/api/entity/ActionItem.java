package org.agty.elfiumexpress.api.entity;

public class ActionItem {
    private String object;
    private Long src;
    private Long dst;

    public ActionItem() {
    }

    public ActionItem(String object, Long src, Long dst) {
        this.object = object;
        this.src = src;
        this.dst = dst;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public Long getSrc() {
        return src;
    }

    public void setSrc(Long src) {
        this.src = src;
    }

    public Long getDst() {
        return dst;
    }

    public void setDst(Long dst) {
        this.dst = dst;
    }
}
