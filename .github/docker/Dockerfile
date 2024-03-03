FROM gradle:jdk21-graal AS build
COPY . .
RUN gradle nativeCompile

FROM debian:bookworm
COPY --from=build --chmod=755 /home/gradle/build/native/nativeCompile/sympauthy /
ENTRYPOINT ["/sympauthy"]
