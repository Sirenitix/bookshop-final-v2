@echo build maven project
call mvnw clean -Dmaven.test.skip package
@echo run project
call java -jar target/MyBookShopApp-0.0.1.jar