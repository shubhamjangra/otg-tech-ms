FROM 039912797956.dkr.ecr.ap-south-1.amazonaws.com/distroless/java17-debian11:0.0.1
USER nonroot
COPY build/libs/*.jar /app/app.jar
WORKDIR /app
CMD ["app.jar","server"]