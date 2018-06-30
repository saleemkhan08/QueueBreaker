package com.thnki.queuebreaker.home;

public abstract class FirestoreDocumentModel {
    protected String id;
    protected int order;

    public long getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
