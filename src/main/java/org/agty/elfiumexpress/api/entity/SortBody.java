package org.agty.elfiumexpress.api.entity;

public class SortBody {
    private Long id;
    private int align;

    public SortBody() {
    }

    public SortBody(Long id, int align) {
        this.id = id;
        this.align = align;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getAlign() {
        return align;
    }

    public void setAlign(int align) {
        this.align = align;
    }
}
