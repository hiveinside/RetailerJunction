package com.example;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class DAOGenerator {


    public static void main(String args[]) throws Exception {
        Schema schema = new Schema(10, "com.example.mridul.RetailerJunction.daogenerator.model");

        Entity event = schema.addEntity("Event");
        event.addIdProperty().autoincrement();
        event.addStringProperty("name");
        event.addStringProperty("value");
        event.addStringProperty("extraname");
        event.addBooleanProperty("issent");
        event.addDateProperty("date");
        event.addIntProperty("count");

        //Location entity
        Entity locationEvent = schema.addEntity("AnalyticsLocation");
        locationEvent.addIdProperty().autoincrement();
        locationEvent.addStringProperty("locationidentifier");
        locationEvent.addDoubleProperty("latitude");
        locationEvent.addDoubleProperty("longitude");
        locationEvent.addIntProperty("locationkey");
        locationEvent.addLongProperty("locationdate");
        locationEvent.addStringProperty("locationname");
        locationEvent.addStringProperty("locationextraidentifier");

        new DaoGenerator().generateAll(schema, "app/src/main/java");
    }
}
