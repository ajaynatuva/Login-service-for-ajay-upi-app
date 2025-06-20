FROM advancedpricing.azurecr.io/openjdk11:0.0.4
COPY ipu-user-service-version.jar /ipu-user-service/
ENTRYPOINT java -jar /ipu-user-service/ipu-user-service-version.jar