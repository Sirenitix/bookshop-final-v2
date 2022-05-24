#!/bin/bash
printf build maven project
mvn clean -DskipTests install -Drat.skip=true
printf run project
java -jar target/MyBookShopApp-0.0.1.jar &