FROM openjdk:17
ADD todo.jar app.jar

LABEL image.name="todo"

EXPOSE 8082:8081

ENTRYPOINT ["java","-jar","app.jar"]