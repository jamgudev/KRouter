package com.jamgu.krouter.plugin

class Record {
    String templateName

    Set<String> aptClasses = []

    Record(String templateName) {
        this.templateName = templateName
    }
}