package com.notes;

public class TableRow {
    private String m_date;
    private String m_title;
    private String m_text;

    TableRow(String date, String title, String text)
    {
        m_date = date;
        m_title = title;
        m_text = text;
    }
    //setters and getters
    public String getDate() {return m_date;}
    public void setDate(String date) {m_date = date;}
    public String getTitle() {return m_title;}
    public void setTitle(String title) {m_title = title;}
    public String getText() {return m_text;}
    public void setText(String text) {m_text = text;}
}
