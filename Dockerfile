FROM openjdk:17
ADD ToDo.jar app.jar

LABEL image.name="todo"

EXPOSE 80:8081

ENTRYPOINT ["java","-jar","app.jar"]