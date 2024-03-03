ARG JAVA_VERSION=21

FROM ghcr.io/graalvm/jdk-community:$JAVA_VERSION

RUN ./gradlew nativeCompile

FROM debian:bookworm

ADD --from=0 --chmod=755 ./build/native/nativeCompile/sympauthy /

ENTRYPOINT ["/sympauthy"]
