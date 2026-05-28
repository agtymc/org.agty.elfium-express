package org.agty.elfiumexpress.modules.express.common;

import org.agty.elfiumexpress.modules.express.dto.ExpressPanelDto;
import org.agty.elfiumexpress.modules.express.view.FileView;
import org.agty.elfiumexpress.storage.utils.ContentUtils;

import java.time.LocalDateTime;

public class ExpressView {
    private Long idType;
    private Long idPanel;
    private Long idGroup;
    private LocalDateTime date;
    private String title;
    private String content;
    private String about;
    private String status;
    private String icon;

    public static ExpressView convertFromPanel(ExpressPanelDto expressPanel) {
        ExpressView expressView = new ExpressView();

        expressView.setIdPanel(expressPanel.getIdExpress());
        expressView.setIdGroup(expressPanel.getIdGroup());
        expressView.setTitle(expressPanel.getTitle());
        expressView.setContent(expressView.bodyConvert(expressPanel));
        expressView.setDate(expressPanel.getDate());

        return expressView;
    }

    public String bodyConvert(ExpressPanelDto panel) {
        if (panel.getIdType() == 2) {
            return ContentUtils.nl2br(panel.getBody());
        }

        if (panel.getIdType() == 3) {
            return new FileView(panel).getContent();
        }

        return null;
    }

    public Long getIdType() {
        return idType;
    }

    public void setIdType(Long idType) {
        this.idType = idType;
    }

    public Long getIdPanel() {
        return idPanel;
    }

    public void setIdPanel(Long idPanel) {
        this.idPanel = idPanel;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Long getIdGroup() {
        return idGroup;
    }

    public void setIdGroup(Long idGroup) {
        this.idGroup = idGroup;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }
}
