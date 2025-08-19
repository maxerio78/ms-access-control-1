# Access Control

Handles access control for the application

### Docker Build
```sh
$ mvn clean package
$ docker build -t accesscontrol .
$ docker run -v <local path to sunseries.ini>:/etc/sunseries.ini -e SPRING_PROFILES_ACTIVE=int -i -t accesscontrol

For Eg:- docker run -v D:\\etc\\sunseries.ini:/etc/sunseries.ini -e SPRING_PROFILES_ACTIVE=int -i -t accesscontrol
```# ms-access-control
# ms-access-control-1
