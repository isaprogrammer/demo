package com.example.demo.controller;

import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Query;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(value = "/influxdb")
@RestController
public class InfluxdbController {

    final String serverURL = "http://127.0.0.1:8086", username = "root", password = "Abc123456";
    final InfluxDB influxDB = InfluxDBFactory.connect(serverURL, username, password);

    @RequestMapping(value = "/mock", method = RequestMethod.POST)
    @ResponseBody
    public void mock() {
        String retentionPolicyName = "one_day_only";
        influxDB.setDatabase("NOAA_water_database");
        influxDB.query(new Query("CREATE RETENTION POLICY " + retentionPolicyName
                + " ON NOAA_water_database DURATION 1d REPLICATION 1 DEFAULT"));

        influxDB.query(new Query("CREATE DATABASE NOAA_water_database"));

    }

}
