package com.example;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class DAOGenerator {


    public static void main(String args[]) throws Exception {
        Schema schema = new Schema(14, "com.example.mridul.RetailerJunction.daogenerator.model");

        /*
         * Table to store current available list from cloud
         */
        Entity appDetails = schema.addEntity("CloudAppDetails");
        appDetails.addLongProperty("campaignId").primaryKey(); // must be long.. dao limitation
        appDetails.addStringProperty("name");
        appDetails.addStringProperty("version");
        appDetails.addIntProperty("size");
        appDetails.addStringProperty("downloadurl");
        appDetails.addStringProperty("packagename");
        appDetails.addStringProperty("checksum");
        appDetails.addIntProperty("state");
        appDetails.addIntProperty("minsdk");

        appDetails.addLongProperty("listts"); // ts of entry/update into DB
        appDetails.addBooleanProperty("downloaded"); // downloaded?
        appDetails.addLongProperty("apkts"); // ts of apk download

        /*
         * Table to store current download status
         */

        /*
         * Table to store install records - to be sent to cloud
         */
        Entity installRecord = schema.addEntity("InstallRecords");
        installRecord.addIdProperty().autoincrement().primaryKey();
        installRecord.addStringProperty("json_data").notNull();
        installRecord.addLongProperty("timestamp").notNull();
        installRecord.addIntProperty("isUploaded").notNull();

        new DaoGenerator().generateAll(schema, "app/src/main/java");
    }
}
