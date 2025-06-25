package com.Login.Email.reports;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

public class ExtentFactory {

    public static ExtentReports getInstance() {
        ExtentSparkReporter sparkReporter = new ExtentSparkReporter("target/REPORTES-BACKEND.html");
        ExtentReports extentReports = new ExtentReports();
        extentReports.attachReporter(sparkReporter);

        // Información adicional del sistema
        extentReports.setSystemInfo("OS", System.getProperty("os.name"));
        extentReports.setSystemInfo("Ambiente", "QA");
        extentReports.setSystemInfo("Navegador", "Chrome");

        return extentReports;
    }

}
